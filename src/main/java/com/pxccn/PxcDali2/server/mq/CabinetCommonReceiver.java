package com.pxccn.PxcDali2.server.mq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;
import com.pxccn.PxcDali2.common.CabinetVersion;
import com.pxccn.PxcDali2.common.VersionHelper;
import com.pxccn.PxcDali2.server.events.CabinetStatusMqEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.pxccn.PxcDali2.server.mq.MqConfigure.consumerQueue_cabinet_common;

@Component
@Slf4j
public class CabinetCommonReceiver {

    @Autowired
    ApplicationContext context;

    @RabbitListener(queues = consumerQueue_cabinet_common)
    public void inComing1(Object omsg) {
        this.doProcess(omsg);
    }
//
//    @RabbitListener(queues = consumerQueue_cabinet_common)
//    public void inComing2(Object omsg) {
//        this.doProcess(omsg);
//    }
//
//    @RabbitListener(queues = consumerQueue_cabinet_common)
//    public void inComing3(Object omsg) {
//        this.doProcess(omsg);
//    }
//
//    @RabbitListener(queues = consumerQueue_cabinet_common)
//    public void inComing4(Object omsg) {
//        this.doProcess(omsg);
//    }

    private void doProcess(Object omsg) {
        Message msg = (Message) omsg;
        try {
            var decodedMsg = ProtoToServerQueueMsg.FromData(msg.getBody());
            var cabinetVersion = VersionHelper.GetCabinetVersionFromId(decodedMsg.getHeaders().get("ver"));
            if (cabinetVersion == CabinetVersion.A0052) {
                log.trace("fetch:{}", decodedMsg);
                if (decodedMsg instanceof CabinetStatusWrapper) {
                    context.publishEvent(new CabinetStatusMqEvent(this, (CabinetStatusWrapper) decodedMsg));
                }
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("Fail to decode pb: {}", e.getMessage());
        }

    }
}
