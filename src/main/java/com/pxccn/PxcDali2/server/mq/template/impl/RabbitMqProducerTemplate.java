//package com.pxccn.PxcDali2.server.mq.template.impl;
//
//import com.google.common.util.concurrent.ListeningExecutorService;
//import com.google.common.util.concurrent.MoreExecutors;
//import com.google.gson.Gson;
//import com.pxccn.PxcDali2.server.mq.message.QueueMsg;
//import com.pxccn.PxcDali2.server.mq.template.*;
//import com.rabbitmq.client.AMQP;
//import com.rabbitmq.client.Channel;
//import com.rabbitmq.client.Connection;
//import com.rabbitmq.client.ConnectionFactory;
//import lombok.extern.slf4j.Slf4j;
//
//
//import java.io.IOException;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeoutException;
//
//@Slf4j
//public class RabbitMqProducerTemplate<T extends QueueMsg> implements QueueProducer<T> {
//    private final String defaultTopic;
//    private final Gson gson = new Gson();
//    private final QueueAdmin admin;
//    private final Channel channel;
//
//    private final Set<TopicPartitionInfo> topics = ConcurrentHashMap.newKeySet();
//
//    public RabbitMqProducerTemplate(QueueAdmin admin, Connection connection, String defaultTopic) {
//        this.admin = admin;
//        this.defaultTopic = defaultTopic;
//        try {
//            channel = connection.createChannel();
//        } catch (IOException e) {
//            log.error("Failed to create chanel.", e);
//            throw new RuntimeException("Failed to create chanel.", e);
//        }
//    }
//
//    @Override
//    public void init() {
//
//    }
//
//    @Override
//    public String getDefaultTopic() {
//        return defaultTopic;
//    }
//
//    @Override
//    public void send(TopicPartitionInfo tpi, T msg, QueueCallback callback) {
//        createTopicIfNotExist(tpi);
//        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
//                .expiration("30000") //过期时间
//                .build();
//        try {
//            channel.basicPublish(tpi.getExchange(), tpi.getRoutingKey(), properties, msg.getData());
//            if (callback != null) {
//                callback.onSuccess(null);
//            }
//        } catch (IOException e) {
//            log.error("Failed publish message: [{}].", msg, e);
//            if (callback != null) {
//                callback.onFailure(e);
//            }
//        }
//    }
//
//    @Override
//    public void stop() {
//        if (channel != null) {
//            try {
//                channel.close();
//            } catch (IOException | TimeoutException e) {
//                log.error("Failed to close the channel.");
//            }
//        }
//    }
//
//    private void createTopicIfNotExist(TopicPartitionInfo tpi) {
//        if (topics.contains(tpi)) {
//            return;
//        }
//        admin.createTopicIfNotExists(tpi.getFullTopicName());
//        topics.add(tpi);
//    }
//}
