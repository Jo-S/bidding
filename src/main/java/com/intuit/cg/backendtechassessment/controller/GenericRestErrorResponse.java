package com.intuit.cg.backendtechassessment.controller;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

// keeping it simple. not uncommon to have an internal error code, or a URL with error documentation, etc.

@Getter
public class GenericRestErrorResponse {
    private Integer status;
    private String message;
    private LocalDateTime timeStamp;

    public GenericRestErrorResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
        timeStamp = LocalDateTime.now(ZoneOffset.UTC);
    }
}
