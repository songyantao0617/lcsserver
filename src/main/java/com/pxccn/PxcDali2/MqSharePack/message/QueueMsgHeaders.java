package com.pxccn.PxcDali2.MqSharePack.message;

public interface QueueMsgHeaders<T> {

    void put(String key, String value);

    Object get(String key);

    T getInternalData();

}