package com.epam.capstone.util.database;

import com.epam.capstone.util.Slf4jPrintWriter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

@Slf4j
public class ConnectionPool implements DataSource, DisposableBean {

    private static final String POOL_OFFER_ERROR_MSG = "Pool is full, cannot return connection to idle queue — closing physical connection";

    private final int maxPoolSize;
    private final int minIdle;
    private final BlockingQueue<PooledEntry> pool;
    private final List<PooledEntry> allEntries = Collections.synchronizedList(new ArrayList<>());

    private final String url;
    private final String user;
    private final String password;
    private final long connectionTimeoutMillis;
    private final long idleTimeoutMillis;
    private final long maxLifetimeMillis;
    private final long leakDetectionThresholdMillis;

    private final ScheduledExecutorService evictor;
    private volatile PrintWriter logWriter;
    private volatile int loginTimeout;

    public ConnectionPool(
            String driver,
            String url,
            String user,
            String password,
            int maxPoolSize,
            int minIdle,
            Duration connectionTimeout,
            Duration idleTimeout,
            Duration maxLifetime,
            Duration leakDetectionThreshold
    ) throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver not found in classpath", e);
        }

        this.maxPoolSize = maxPoolSize;
        this.minIdle = minIdle;
        this.url = Objects.requireNonNull(url);
        this.user = user;
        this.password = password;
        this.connectionTimeoutMillis = connectionTimeout.toMillis();
        this.idleTimeoutMillis = idleTimeout.toMillis();
        this.maxLifetimeMillis = maxLifetime.toMillis();
        this.leakDetectionThresholdMillis = leakDetectionThreshold.toMillis();

        this.logWriter = new Slf4jPrintWriter(
                LoggerFactory.getLogger("jdbc-driver"),
                ch.qos.logback.classic.Level.DEBUG
        );
        DriverManager.setLogWriter(this.logWriter);

        this.pool = new ArrayBlockingQueue<>(maxPoolSize);
        for (int i = 0; i < minIdle; i++) {
            PooledEntry e = createEntry();
            if (pool.offer(e)) {
                allEntries.add(e);
            } else {
                log.warn(POOL_OFFER_ERROR_MSG);
                e.physical.close();
            }
        }

        long evictionInterval = Math.min(idleTimeoutMillis, maxLifetimeMillis);
        evictor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ConnectionPool-Evictor");
            t.setDaemon(true);
            return t;
        });
        evictor.scheduleWithFixedDelay(() -> {
            evict();
            detectLeaks();
        }, evictionInterval, evictionInterval, TimeUnit.MILLISECONDS);
    }

    @Override
    public Connection getConnection() throws SQLException {
        PooledEntry entry = pool.poll();
        if (entry != null) {
            markLeased(entry);
            return entry.proxy;
        }
        synchronized (this) {
            if (allEntries.size() < maxPoolSize) {
                PooledEntry e = createEntry();
                allEntries.add(e);
                markLeased(e);
                return e.proxy;
            }
        }
        try {
            entry = pool.poll(connectionTimeoutMillis, TimeUnit.MILLISECONDS);
            if (entry == null) throw new SQLTimeoutException("Timeout waiting for connection");
            markLeased(entry);
            return entry.proxy;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", ex);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    private void markLeased(PooledEntry entry) {
        entry.inUse.set(true);
        entry.leakTimestamp = System.currentTimeMillis();
        entry.leakTrace = new Exception("Connection leased here");
    }

    private void detectLeaks() {
        long now = System.currentTimeMillis();
        for (PooledEntry e : allEntries) {
            if (e.inUse.get() && now - e.leakTimestamp > leakDetectionThresholdMillis) {
                log.warn("Potential connection leak detected: leased at", e.leakTrace);
            }
        }
    }

    private PooledEntry createEntry() throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
        Connection physical = DriverManager.getConnection(url, user, password);
        long now = System.currentTimeMillis();
        PooledEntry e = new PooledEntry(physical, now);
        e.proxy = createProxy(e);
        return e;
    }

    private Connection createProxy(PooledEntry entry) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("close".equals(name)) {
                        if (entry.inUse.compareAndSet(true, false)) {
                            entry.lastUsed = System.currentTimeMillis();
                            if (!pool.offer(entry)) {
                                log.warn(POOL_OFFER_ERROR_MSG);
                                entry.physical.close();
                                allEntries.remove(entry);
                            }
                        }
                        return null;
                    }
                    if ("isClosed".equals(name)) {
                        return !entry.inUse.get();
                    }
                    entry.inUse.set(true);
                    try {
                        return method.invoke(entry.physical, args);
                    } catch (InvocationTargetException ite) {
                        throw ite.getCause();
                    }
                }
        );
    }

    private void evict() {
        long now = System.currentTimeMillis();
        for (PooledEntry e : pool) {
            if (pool.size() <= minIdle) break;
            boolean idleTooLong = now - e.lastUsed > idleTimeoutMillis;
            boolean tooOld = now - e.creationTime > maxLifetimeMillis;
            if ((idleTooLong || tooOld) && pool.remove(e)) {
                try {
                    e.physical.close();
                } catch (SQLException ignored) {
                }
                allEntries.remove(e);
            }
        }
    }

    public void shutdown() throws SQLException {
        evictor.shutdownNow();
        for (PooledEntry e : allEntries) {
            try {
                e.physical.close();
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(d);
                log.info("Deregistering JDBC driver {}", d);
            } catch (SQLException ex) {
                log.error("Error deregistering JDBC driver {}", d, ex);
            }
        }
    }

    public int getTotalConnections() {
        return allEntries.size();
    }

    public int getIdleConnections() {
        return pool.size();
    }

    public int getActiveConnections() {
        return getTotalConnections() - getIdleConnections();
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        this.loginTimeout = seconds;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) return iface.cast(this);
        throw new SQLException("Not a wrapper for " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    private static class PooledEntry {
        final Connection physical;
        final long creationTime;
        final AtomicBoolean inUse = new AtomicBoolean(false);
        Connection proxy;
        volatile long lastUsed;
        volatile long leakTimestamp;
        Throwable leakTrace;

        PooledEntry(Connection physical, long now) {
            this.physical = physical;
            this.creationTime = now;
            this.lastUsed = now;
            this.leakTimestamp = 0;
            this.leakTrace = null;
        }
    }
}
