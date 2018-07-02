package com.intuit.cg.backendtechassessment;

import com.intuit.cg.backendtechassessment.controller.GenericRestErrorResponse;
import com.intuit.cg.backendtechassessment.exception.ProjectClosedForBiddingException;
import com.intuit.cg.backendtechassessment.exception.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

// simple and not full-coverage.

@ControllerAdvice
public class SimpleExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public GenericRestErrorResponse handleProjectNotFoundException(ProjectNotFoundException e) {
        return new GenericRestErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(ProjectClosedForBiddingException.class)
    @ResponseStatus(HttpStatus.GONE)
    @ResponseBody
    public GenericRestErrorResponse handleProjectClosedForBiddingException(ProjectClosedForBiddingException e) {
        return new GenericRestErrorResponse(HttpStatus.GONE.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public GenericRestErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new GenericRestErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public GenericRestErrorResponse handleUnspecifiedException(Exception e) {
        return new GenericRestErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unspecified Error");
    }
}
