package com.epam.capstone.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.epam.capstone.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 404 – unmapped URL or resource
    @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class, NotFoundException.class })
    public String handleNotFound(
            HttpServletRequest  request,
            HttpServletResponse response,
            Exception           ex,
            org.springframework.ui.Model model
    ) {
        response.setStatus(404);
        model.addAttribute("statusCode", 404);

        String text = (ex instanceof NotFoundException)
                ? ex.getMessage()
                : "Page not found";
        model.addAttribute("errorText", text);
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    // 403 – Spring Security access denied
    @ExceptionHandler(AccessDeniedException.class)
    public String handleForbidden(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex,
            org.springframework.ui.Model model
    ) {
        response.setStatus(403);
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorText", "You don’t have permission to access this resource");
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    // 405 – wrong HTTP method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotAllowed(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpRequestMethodNotSupportedException ex,
            org.springframework.ui.Model model
    ) {
        response.setStatus(405);
        model.addAttribute("statusCode", 405);
        model.addAttribute("errorText", ex.getMethod() + " not allowed for this endpoint");
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    // 400 – validation or missing parameters
    @ExceptionHandler({
            BindException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public String handleBadRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception ex,
            org.springframework.ui.Model model
    ) {
        response.setStatus(400);
        model.addAttribute("statusCode", 400);
        model.addAttribute("errorText", ex.getMessage() != null
                ? ex.getMessage()
                : "Bad request");
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    // 409 – data conflicts (e.g. unique constraint violation)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleConflict(
            HttpServletRequest request,
            HttpServletResponse response,
            DataIntegrityViolationException ex,
            org.springframework.ui.Model model
    ) {
        response.setStatus(409);
        model.addAttribute("statusCode", 409);
        model.addAttribute("errorText", "Conflict: " + ex.getMostSpecificCause().getMessage());
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    // 500 – all other uncaught exceptions
    @ExceptionHandler(Exception.class)
    public String handleServerError(
            HttpServletRequest  request,
            HttpServletResponse response,
            Exception           ex,
            org.springframework.ui.Model model
    ) {
        String incident = UUID.randomUUID().toString();
        log.error("Incident {} on [{} {}]:", incident,
                request.getMethod(), request.getRequestURI(), ex);

        response.setStatus(500);
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorText", "Internal server error");
        model.addAttribute("incidentCode", incident);
        return "error/general";
    }
}

