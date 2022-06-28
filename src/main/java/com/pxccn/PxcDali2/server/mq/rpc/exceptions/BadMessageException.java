package com.pxccn.PxcDali2.server.mq.rpc.exceptions;

public class BadMessageException extends IllegalStateException {
    public BadMessageException(String msg) {
        super(msg);
    }
}
