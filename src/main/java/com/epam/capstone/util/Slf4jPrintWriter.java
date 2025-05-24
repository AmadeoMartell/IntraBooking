package com.epam.capstone.util;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import java.io.PrintWriter;
import java.io.Writer;

public class Slf4jPrintWriter extends PrintWriter {

    private final Logger logger;
    private final Level level;

    private static class NoOpWriter extends Writer {
        @Override public void write(char[] cbuf, int off, int len) {}
        @Override public void flush() {}
        @Override public void close() {}
    }

    public Slf4jPrintWriter(Logger logger, Level level) {
        super(new NoOpWriter(), true);
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void println(String x) {
        switch (level.levelInt) {
            case Level.DEBUG_INT: logger.debug(x); break;
            case Level.INFO_INT:  logger.info(x);  break;
            case Level.WARN_INT:  logger.warn(x);  break;
            case Level.ERROR_INT: logger.error(x); break;
            default:              logger.debug(x); break;
        }
    }
}
