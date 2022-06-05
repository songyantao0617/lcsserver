package com.pxccn.PxcDali2.server.mq;


import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LcsQueueFactory {



//    public QueueRequestTemplate<ProtoQueueMsg<TransportApiRequestMsg>, ProtoQueueMsg<TransportApiResponseMsg>> createTransportApiRequestTemplate() {
//        QueueProducer<ProtoQueueMsg<TransportApiRequestMsg>> producerTemplate = new RabbitMqProducerTemplate<>(transportApiAdmin, rabbitMqSettings, transportApiSettings.getRequestsTopic());
//
//        TbQueueConsumer<ProtoQueueMsg<TransportApiResponseMsg>> consumerTemplate = new RabbitMqConsumerTemplate<>(transportApiAdmin, rabbitMqSettings, transportApiSettings.getResponsesTopic() + "." + serviceInfoProvider.getServiceId(),
//                msg -> new ProtoQueueMsg<>(msg.getKey(), TransportApiResponseMsg.parseFrom(msg.getData()), msg.getHeaders()));
//
//        DefaultTbQueueRequestTemplate.DefaultTbQueueRequestTemplateBuilder<TbProtoQueueMsg<TransportApiRequestMsg>, TbProtoQueueMsg<TransportApiResponseMsg>> templateBuilder = DefaultTbQueueRequestTemplate.builder();
//        templateBuilder.queueAdmin(transportApiAdmin);
//        templateBuilder.requestTemplate(producerTemplate);
//        templateBuilder.responseTemplate(consumerTemplate);
//        templateBuilder.maxPendingRequests(transportApiSettings.getMaxPendingRequests());
//        templateBuilder.maxRequestTimeout(transportApiSettings.getMaxRequestsTimeout());
//        templateBuilder.pollInterval(transportApiSettings.getResponsePollInterval());
//        return templateBuilder.build();
//    }


//    @Bean
//    public

}
