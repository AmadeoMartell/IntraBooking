package com.epam.capstone.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebAppConfig.class, WebSecurityConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        configureLogback();
        super.onStartup(servletContext);

        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        FilterRegistration.Dynamic encoding = servletContext
                .addFilter("encodingFilter", encodingFilter);
        encoding.addMappingForUrlPatterns(null, false, "/*");

        servletContext
                .addFilter("hiddenHttpMethodFilter", new HiddenHttpMethodFilter())
                .addMappingForUrlPatterns(null, true, "/*");
    }

    private void configureLogback() {
        LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
        ctx.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(ctx);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
        console.setName("STDOUT");
        console.setContext(ctx);
        console.setEncoder(encoder);
        console.start();

        var root = ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.WARN);
        root.addAppender(console);

        var appLogger = ctx.getLogger("com.epam.capstone");
        appLogger.setLevel(Level.DEBUG);
        appLogger.setAdditive(false);
        appLogger.addAppender(console);

        ctx.getLogger("jdbc-driver").setLevel(Level.WARN);
        ctx.getLogger("org.springframework").setLevel(Level.WARN);
        ctx.getLogger("org.springframework.web").setLevel(Level.WARN);
        ctx.getLogger("org.hibernate").setLevel(Level.WARN);
        ctx.getLogger("org.apache").setLevel(Level.WARN);
    }
}
