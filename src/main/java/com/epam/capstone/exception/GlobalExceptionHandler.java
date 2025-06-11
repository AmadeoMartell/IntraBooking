package com.epam.capstone.exception;

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

/**
 * Centralized handler for application-wide exceptions, mapping them to
 * appropriate HTTP status codes and error views.
 * <p>
 * Handles:
 * <ul>
 *   <li>404 Not Found: unmapped URLs or missing resources</li>
 *   <li>403 Forbidden: Spring Security access denied</li>
 *   <li>405 Method Not Allowed: unsupported HTTP methods</li>
 *   <li>400 Bad Request: validation failures or missing parameters</li>
 *   <li>409 Conflict: database constraint violations</li>
 *   <li>500 Internal Server Error: all other uncaught exceptions</li>
 * </ul>
 * The error view template is {@code error/general}, into which it injects:
 * <ul>
 *   <li>{@code statusCode} – HTTP status code</li>
 *   <li>{@code errorText} – human-readable message</li>
 *   <li>{@code incidentCode} – unique ID for server errors (500)</li>
 * </ul>
 * </p>
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle 404 Not Found for:
     * <ul>
     *   <li>{@link NoHandlerFoundException}</li>
     *   <li>{@link NoResourceFoundException}</li>
     *   <li>{@link NotFoundException}</li>
     * </ul>
     *
     * @param request  the current HTTP request
     * @param response the HTTP response to configure
     * @param ex       the thrown exception
     * @param model    the view model to populate
     * @return the logical view name "error/general"
     */
    @ExceptionHandler({ NoHandlerFoundException.class,
            NoResourceFoundException.class,
            NotFoundException.class })
    public String handleNotFound(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Exception ex,
                                 org.springframework.ui.Model model) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String message = (ex instanceof NotFoundException)
                ? ex.getMessage()
                : "Page not found";
        log.debug("404 Not Found at [{} {}]: {}", request.getMethod(),
                request.getRequestURI(), message);
        model.addAttribute("statusCode", 404);
        model.addAttribute("errorText", message);
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    /**
     * Handle 403 Forbidden when Spring Security denies access.
     *
     * @param request  the current HTTP request
     * @param response the HTTP response to configure
     * @param ex       the thrown AccessDeniedException
     * @param model    the view model to populate
     * @return the logical view name "error/general"
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleForbidden(HttpServletRequest request,
                                  HttpServletResponse response,
                                  AccessDeniedException ex,
                                  org.springframework.ui.Model model) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        log.warn("403 Forbidden at [{} {}]: {}", request.getMethod(),
                request.getRequestURI(), ex.getMessage());
        model.addAttribute("statusCode", 403);
        model.addAttribute("errorText", "You don’t have permission to access this resource");
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    /**
     * Handle 405 Method Not Allowed for unsupported HTTP methods.
     *
     * @param request  the current HTTP request
     * @param response the HTTP response to configure
     * @param ex       the thrown HttpRequestMethodNotSupportedException
     * @param model    the view model to populate
     * @return the logical view name "error/general"
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleMethodNotAllowed(HttpServletRequest request,
                                         HttpServletResponse response,
                                         HttpRequestMethodNotSupportedException ex,
                                         org.springframework.ui.Model model) {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        String method = ex.getMethod();
        log.debug("405 Method Not Allowed at [{} {}]: {}", request.getMethod(),
                request.getRequestURI(), method + " not allowed");
        model.addAttribute("statusCode", 405);
        model.addAttribute("errorText", method + " not allowed for this endpoint");
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    /**
     * Handle 400 Bad Request for validation failures or missing parameters.
     *
     * @param request  the current HTTP request
     * @param response the HTTP response to configure
     * @param ex       the thrown exception (BindException, MissingServletRequestParameterException, IllegalArgumentException)
     * @param model    the view model to populate
     * @return the logical view name "error/general"
     */
    @ExceptionHandler({ BindException.class,
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class })
    public String handleBadRequest(HttpServletRequest request,
                                   HttpServletResponse response,
                                   Exception ex,
                                   org.springframework.ui.Model model) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String msg = ex.getMessage() != null ? ex.getMessage() : "Bad request";
        log.warn("400 Bad Request on [{} {}]: {}", request.getMethod(),
                request.getRequestURI(), msg, ex);
        model.addAttribute("statusCode", 400);
        model.addAttribute("errorText", msg);
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    /**
     * Handle 409 Conflict for database constraint violations.
     *
     * @param request  the current HTTP request
     * @param response the HTTP response to configure
     * @param ex       the thrown DataIntegrityViolationException
     * @param model    the view model to populate
     * @return the logical view name "error/general"
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleConflict(HttpServletRequest request,
                                 HttpServletResponse response,
                                 DataIntegrityViolationException ex,
                                 org.springframework.ui.Model model) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        String cause = ex.getMostSpecificCause().getMessage();
        log.debug("409 Conflict on [{} {}]: {}", request.getMethod(),
                request.getRequestURI(), cause, ex);
        model.addAttribute("statusCode", 409);
        model.addAttribute("errorText", "Conflict: " + cause);
        model.addAttribute("incidentCode", null);
        return "error/general";
    }

    /**
     * Handle all other uncaught exceptions as 500 Internal Server Error.
     *
     * @param request  the current HTTP request
     * @param response the HTTP response to configure
     * @param ex       the thrown exception
     * @param model    the view model to populate
     * @return the logical view name "error/general"
     */
    @ExceptionHandler(Exception.class)
    public String handleServerError(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Exception ex,
                                    org.springframework.ui.Model model) {
        String incident = UUID.randomUUID().toString();
        log.error("500 Internal Server Error (incident {}) on [{} {}]", incident,
                request.getMethod(), request.getRequestURI(), ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("statusCode", 500);
        model.addAttribute("errorText", "Internal server error");
        model.addAttribute("incidentCode", incident);
        return "error/general";
    }
}
