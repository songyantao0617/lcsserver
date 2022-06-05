package com.pxccn.PxcDali2.server.mq.template;

import com.pxccn.PxcDali2.MqSharePack.message.QueueMsg;

import java.util.List;
import java.util.Set;

public interface QueueConsumer<T extends QueueMsg> {

    String getTopic();

    void subscribe();

    void subscribe(Set<TopicPartitionInfo> partitions);

    void unsubscribe();

    List<T> poll(long durationInMillis);

    void commit();

    boolean isStopped();

}