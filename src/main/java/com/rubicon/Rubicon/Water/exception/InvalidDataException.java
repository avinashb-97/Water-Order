package com.rubicon.Rubicon.Water.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDataException extends RuntimeException{

    public InvalidDataException()
    {
        super();
    }

    public InvalidDataException(String message)
    {
        super(message);
    }
}