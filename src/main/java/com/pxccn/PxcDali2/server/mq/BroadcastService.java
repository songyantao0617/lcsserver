package com.pxccn.PxcDali2.server.mq;

import com.pxccn.PxcDali2.server.mq.consumers.ComsumerBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import static com.pxccn.PxcDali2.server.mq.config.MqConfigure.switch_broadcastToPlc;


@Service
@Slf4j
public class BroadcastService extends ComsumerBase {
    private final ConcurrentMap<String, Task> pending = new ConcurrentHashMap<>();
    @Value("${LcsServer.name}")
    String serverName;
    @Autowired
    RabbitTemplate rabbitTemplate;
    private final TaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    @Override
    protected void prepare(AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(new Queue(this.getQueueName(), false, false, true));
    }

    @Override
    public String getQueueName() {
        return "lcs.server." + this.serverName + ".broadcast-reply";
    }

    @Override
    public void start() {
        super.start();

    }

    @Override
    public void stop() {
        super.stop();
        ((ThreadPoolTaskScheduler) this.taskScheduler).destroy();
    }

    @Override
    public void onMessage(Message message) {
        if (log.isTraceEnabled())
            log.trace("{}-onMessage:{}", getQueueName(), message);
        try {
            String currentCorrelationId = message.getMessageProperties().getCorrelationId();
            var t = this.pending.get(currentCorrelationId);
            if (t != null) {
                t.consumer.accept(message);
            } else {
                log.warn("信息迟到:{}", message);
            }
        } catch (Throwable e) {
            log.error("严重错误，无法处理!", e);
        }
    }

    @Override
    public void handleError(Throwable t) {
        log.error("", t);
    }

    public void sendBroadcastCommand(Message sendMessage, Consumer<Message> consumer, Runnable timeUp, int timeout) {
        MessageProperties messageProperties = sendMessage.getMessageProperties();

        //如果没有设置过期时间，则给定十秒过期
        if (!StringUtils.hasText(messageProperties.getExpiration())) {
            messageProperties.setExpiration("10000");
        }
        String currentCorrelationId = messageProperties.getCorrelationId();
        if (!StringUtils.hasText(currentCorrelationId)) {
            currentCorrelationId = UUID.randomUUID().toString();
            messageProperties.setCorrelationId(currentCorrelationId);
            Assert.isNull(messageProperties.getReplyTo(), "'replyTo' property must be null");
        }
        messageProperties.setReplyTo(this.getQueueName());
        var t = new Task();
        t.consumer = consumer;
        t.timeout = timeout;
        this.pending.put(currentCorrelationId, t);
        String finalCurrentCorrelationId = currentCorrelationId;
        this.taskScheduler.schedule(() -> {
            this.pending.remove(finalCurrentCorrelationId);
            if (timeUp != null)
                timeUp.run();
        }, new Date(System.currentTimeMillis() + timeout));
        rabbitTemplate.send(switch_broadcastToPlc, "", sendMessage);
    }

    static class Task {
        Consumer<Message> consumer;
        int timeout;
    }
}
