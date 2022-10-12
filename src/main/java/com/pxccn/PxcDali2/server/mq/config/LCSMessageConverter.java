package com.pxccn.PxcDali2.server.mq.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

public class LCSMessageConverter extends SimpleMessageConverter {
    protected Message createMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        //60秒过过期
        messageProperties.setExpiration("60000");
        return super.createMessage(object,messageProperties);
    }
}
