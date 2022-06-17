package com.pxccn.PxcDali2.server.mq.consumers;

import com.pxccn.PxcDali2.server.events.TopSpaceReadyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
@Slf4j
public abstract class ComsumerBase implements MessageListener, ErrorHandler, InitializingBean, BeanNameAware, SmartLifecycle {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    String beanName;
    DirectMessageListenerContainer container;


    protected abstract void prepare(AmqpAdmin amqpAdmin);

    protected int getParallelism() {
        return 1;
    }

    public abstract String getQueueName();

    @Override
    public void afterPropertiesSet() throws Exception {
        prepare(this.amqpAdmin);
        container = new DirectMessageListenerContainer(this.rabbitTemplate.getConnectionFactory());
        container.setMessageListener(this);
        container.setConsumersPerQueue(this.getParallelism());
        container.setBeanName(this.beanName + "#MqContainer");
        container.setErrorHandler(this);
        container.setQueueNames(this.getQueueName());
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    boolean isRunning = false;


    @EventListener
    public void onTopSpaceReadyEvent(TopSpaceReadyEvent event){
        //TopSpace建立好之后再开始接受
        container.start();
    }


    @Override
    public void start() {
        this.isRunning = true;
    }

    @Override
    public void stop() {
        container.stop();
        this.isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }

}
