package com.rubicon.Rubicon.Water.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CannotCancelException extends RuntimeException{

    public CannotCancelException()
    {
        super();
    }

    public CannotCancelException(String message)
    {
        super(message);
    }

}
