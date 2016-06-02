package com.xz.intclient;

import com.xz.AppException;

/**
 * (description)
 * created at 16/06/01
 *
 * @author yiding_he
 */
public class InterfaceException extends AppException {

    public InterfaceException() {
    }

    public InterfaceException(String message) {
        super(message);
    }

    public InterfaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterfaceException(Throwable cause) {
        super(cause);
    }
}
