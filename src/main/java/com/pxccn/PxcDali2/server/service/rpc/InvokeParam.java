package com.pxccn.PxcDali2.server.service.rpc;

public class InvokeParam<T> {
    Class<T> t;
    Object v;

    private InvokeParam(Class<T> type, T value) {
        this.v = value;
        this.t = type;
    }

    public static <T> InvokeParam<T> set(Class<T> type, T value) {
        return new InvokeParam<T>(type, value);
    }

    public Object getValue() {
        return this.v;
    }

    public Class<T> getCls() {
        return this.t;
    }
}