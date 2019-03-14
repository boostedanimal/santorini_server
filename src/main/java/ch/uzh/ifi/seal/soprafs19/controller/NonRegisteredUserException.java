package ch.uzh.ifi.seal.soprafs19.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//with this exception we want to catch non registered users during the login phase
//Error code: 404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NonRegisteredUserException extends RuntimeException{
    public NonRegisteredUserException(String exception) {
        super(exception);
    }
}