package com.ballistic.fserver.restapi;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;

public class APIResponse<T> {

    @ApiModelProperty(notes = "API Response message.")
    private final String message;
    @ApiModelProperty(notes = "API Response return code.")
    private final HttpStatus returnCode;
    @ApiModelProperty(notes = "Entity")
    private final T entity;


    public APIResponse(String message, HttpStatus returnCode, T entity) {
        this.message = message;
        this.returnCode = returnCode;
        this.entity = entity;
    }


    public String getMessage() { return message; }

    public HttpStatus getReturnCode() { return returnCode; }

    public T getEntity() { return entity; }

    @Override
    public String toString() { return "APIResponse{" + "message='" + message + '\'' + ", returnCode=" + returnCode +
                ", entity=" + entity + '}';
    }
}
