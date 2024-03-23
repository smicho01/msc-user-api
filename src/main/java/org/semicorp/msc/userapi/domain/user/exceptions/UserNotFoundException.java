package org.semicorp.msc.userapi.domain.user.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {}

    public UserNotFoundException(String message) {
        super(message);
    }
}
