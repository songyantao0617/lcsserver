package com.pxccn.PxcDali2.MqSharePack.message;


public interface QueueMsg {

    long getTimestamp();

    QueueMsgHeaders getHeaders();

    byte[] getData();
}