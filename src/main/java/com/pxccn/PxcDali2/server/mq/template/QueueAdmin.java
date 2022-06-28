package com.pxccn.PxcDali2.server.mq.template;

public interface QueueAdmin {

    //创建Topic
    void createTopicIfNotExists(String topic);

    void destroy();
}