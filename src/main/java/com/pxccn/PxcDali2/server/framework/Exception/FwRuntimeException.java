package com.pxccn.PxcDali2.server.framework.Exception;

public class FwRuntimeException extends RuntimeException {
    String message;

    public FwRuntimeException(String message) {
        super(message);
    }
}
