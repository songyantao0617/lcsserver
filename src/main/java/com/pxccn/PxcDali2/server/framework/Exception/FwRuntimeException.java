package com.pxccn.PxcDali2.server.framework.Exception;

public class FwRuntimeException extends RuntimeException {
    public FwRuntimeException(String message) {
        super(message);
    }

    String message;
}
