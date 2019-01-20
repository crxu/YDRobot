package com.readyidu.robot.message.exception;

/**
 * Created by gx on 2017/10/20.
 */
public class NoSupportMessageException extends RuntimeException {

    public NoSupportMessageException() {
    }

    public NoSupportMessageException(String message) {
        super(message);
    }

    public NoSupportMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSupportMessageException(Throwable cause) {
        super(cause);
    }
}
