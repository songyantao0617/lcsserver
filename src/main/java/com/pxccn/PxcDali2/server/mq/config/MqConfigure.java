package com.pxccn.PxcDali2.server.mq.config;

import com.pxccn.PxcDali2.common.LcsThreadFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.ConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionFactoryBeanConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfigure {

    public static final String switch_realtime = "Fanout-RealtimeStatus";
    public static final String switch_log = "Fanout-CabinetLog";
    public static final String switch_broadcastToPlc = "Fanout-BroadcastToPlc";
    public static final String switch_cabinet_common = "Fanout-CabinetCommon";
    public static final String switch_cabinet_event = "Fanout-CabinetEvent";
    public static final String switch_ToPlcCommon = "Direct-ToPlcCommon";


    @Bean
    public ConnectionNameStrategy connectionName(@Value("${LcsServer.name}") String name) {
        return f -> (name);
    }

    @Bean
    public DirectExchange ToPlcCommonExchange() {
        return new DirectExchange(switch_ToPlcCommon, true, false);
    }

    @Bean
    public FanoutExchange RealtimeStatusExchange() {
        return new FanoutExchange(switch_realtime, true, false, null);
    }

    @Bean
    public FanoutExchange CabinetLogExchange() {
        return new FanoutExchange(switch_log, true, false, null);
    }

    @Bean
    public FanoutExchange CabinetCommonExchange() {
        return new FanoutExchange(switch_cabinet_common, true, false, null);
    }

    @Bean
    public FanoutExchange CabinetEventExchange() {
        return new FanoutExchange(switch_cabinet_event, true, false, null);
    }

    /**
     * 全局广播
     *
     * @return
     */
    @Bean
    public FanoutExchange BroadcastToExchange() {
        return new FanoutExchange(switch_broadcastToPlc, true, false, null);
    }

    @Bean
    CachingConnectionFactory rabbitConnectionFactory(
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
            CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer,
            ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception {
        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        connectionFactoryBean.setThreadFactory(LcsThreadFactory.forName("rabbitmq"));
        rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
        connectionFactoryBean.afterPropertiesSet();
        com.rabbitmq.client.ConnectionFactory connectionFactory = connectionFactoryBean.getObject();
        connectionFactoryCustomizers.orderedStream().forEach((customizer) -> customizer.customize(connectionFactory));
        CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
        rabbitCachingConnectionFactoryConfigurer.configure(factory);
        return factory;
    }

    @Bean
    RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        var r = new RabbitTemplate();
        r.setMessageConverter(new LCSMessageConverter());
        configurer.configure(r, connectionFactory);
        return r;
    }

    @Bean
    AsyncRabbitTemplate asyncRabbitTemplate(ObjectProvider<RabbitTemplate> rabbitTemplate) {
        var a = new AsyncRabbitTemplate(rabbitTemplate.getIfAvailable());
        a.setMandatory(true);
        a.setReceiveTimeout(1000 * 60 * 10);
        return a;
    }

}
