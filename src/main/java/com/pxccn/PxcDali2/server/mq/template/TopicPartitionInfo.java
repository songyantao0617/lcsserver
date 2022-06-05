package com.pxccn.PxcDali2.server.mq.template;

import lombok.Data;

@Data
public class TopicPartitionInfo {
    String exchange;
    String routingKey;


}
