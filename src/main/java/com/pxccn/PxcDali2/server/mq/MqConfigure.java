package com.pxccn.PxcDali2.server.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfigure {

    public static final String switch_realtime = "Fanout-RealtimeStatus";
    public static final String switch_cabinet_common = "Fanout-CabinetCommon";
    public static final String consumerQueue_realtime = "lcs.server.realtime-status";
    public static final String consumerQueue_cabinet_common = "lcs.server.cabinet-common";
    public static final String switch_ToPlcCommon = "Direct-ToPlcCommon";


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public DirectExchange ToPlcCommonExchange(){
        return new DirectExchange(switch_ToPlcCommon,true,false);
    }


    @Bean
    public FanoutExchange RealtimeStatusExchange() {
        return new FanoutExchange(switch_realtime, true, false, null);
    }

    @Bean
    public FanoutExchange CabinetCommonExchange() {
        return new FanoutExchange(switch_cabinet_common, true, false, null);
    }

    @Bean
    public Queue RealtimeStatusQueue() {
        return new Queue(consumerQueue_realtime);
    }

    @Bean
    public Queue CabinetCommonQueue() {
        return new Queue(consumerQueue_cabinet_common);
    }

    @Bean
    public Binding bindRealtimeStatus() {
        return BindingBuilder
                .bind(RealtimeStatusQueue())   //绑定队列
                .to(RealtimeStatusExchange());       //队列绑定到哪个交换器
    }

    @Bean
    public Binding bindCabinetCommon() {
        return BindingBuilder
                .bind(CabinetCommonQueue())   //绑定队列
                .to(CabinetCommonExchange());       //队列绑定到哪个交换器
    }

    @Bean
    AsyncRabbitTemplate asyncRabbitTemplate(){
        return new AsyncRabbitTemplate(rabbitTemplate);
    }


}
