package com.pxccn.PxcDali2.server.service.rpc;

public class InvokeParam<T> {
    Class<T> t;
    Object v;

    public InvokeParam(Class<T> type, T value) {
        this.v = value;
        this.t = type;
    }

    public Object getValue() {
        return this.v;
    }

    public Class<T> getCls() {
        return this.t;
    }
}