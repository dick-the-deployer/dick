package com.dickthedeployer.dick.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class RepositoryParsingException extends Exception {

    public RepositoryParsingException(Throwable cause) {
        super(cause);
    }
}
