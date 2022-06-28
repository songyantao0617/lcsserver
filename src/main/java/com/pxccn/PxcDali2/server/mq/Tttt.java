//package com.pxccn.PxcDali2.server.mq;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.ReturnedMessage;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.SmartLifecycle;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class Tttt implements SmartLifecycle {
//
//    @Autowired
//    RabbitTemplate rabbitTemplate;
//
//
//    @Override
//    public void start() {
//
//        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
//            @Override
//            public void returnedMessage(ReturnedMessage returned) {
//                log.info("RETURN:{}", returned);
//            }
//        });
//
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                log.error("ConfirmCallback:{}",ack);
//            }
//        });
//        rabbitTemplate.setMandatory(true);
//
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","dddewq","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        rabbitTemplate.convertAndSend("DIR123","ddd","Test");
//        System.out.println(212);
////        rabbitTemplate.convertAndSend();
//    }
//
//    @Override
//    public void stop() {
//
//    }
//
//    @Override
//    public boolean isRunning() {
//        return false;
//    }
//}
