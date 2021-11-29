package com.infilos.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@ControllerAdvice 
public class ExceptionControllerAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String exception(AccessDeniedException e) {
        return "{\"status\":\"access denied\"}";
    }
}