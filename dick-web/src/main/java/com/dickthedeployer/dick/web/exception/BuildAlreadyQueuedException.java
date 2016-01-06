package com.dickthedeployer.dick.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by mariusz.luciow on 06/01/16.
 */
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class BuildAlreadyQueuedException extends Exception {
}
