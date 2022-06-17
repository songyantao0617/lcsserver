package com.pxccn.PxcDali2.server.mq.consumers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.*;
import com.pxccn.PxcDali2.common.VersionHelper;
import com.pxccn.PxcDali2.server.events.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.pxccn.PxcDali2.server.mq.config.MqConfigure.switch_cabinet_common;

@Component
@Slf4j
public class CabinetCommonConsumer extends ComsumerBase {

    @Value("${LcsServer.name}")
    String serverName;

    @Autowired
    ApplicationContext context;


    @Override
    protected void prepare(AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(new Queue(this.getQueueName(), false, false, true));
        amqpAdmin.declareBinding(new Binding(this.getQueueName(), Binding.DestinationType.QUEUE, switch_cabinet_common, "", null));
    }

    @Override
    public String getQueueName() {
        return "lcs.server." + this.serverName + ".cabinet-common";
    }

    @Override
    public void onMessage(Message message) {
        try {
            var decodedMsg = ProtoToServerQueueMsg.FromData(message.getBody());
            var cabinetVersion = VersionHelper.GetCabinetVersionFromId(decodedMsg.getHeaders().get("ver"));
//            if (cabinetVersion == CabinetVersion.A0052) {
            if (decodedMsg instanceof CabinetStatusWrapper) {
                context.publishEvent(new CabinetStatusMqEvent(this, (CabinetStatusWrapper) decodedMsg));
            } else if (decodedMsg instanceof LightsDetailUploadWrapper) {
                context.publishEvent(new LightsDetailUploadEvent(this, (LightsDetailUploadWrapper) decodedMsg));
            } else if (decodedMsg instanceof RoomsDetailUploadWrapper) {
                context.publishEvent(new RoomsDetailUploadEvent(this, (RoomsDetailUploadWrapper) decodedMsg));
            } else if (decodedMsg instanceof CabinetDetailUploadWrapper) {
                context.publishEvent(new CabinetDetailUploadEvent(this, (CabinetDetailUploadWrapper) decodedMsg));
            } else if (decodedMsg instanceof CabinetSimpleEventWrapper) {
                context.publishEvent(new CabinetSimpleEvent(this, (CabinetSimpleEventWrapper) decodedMsg));
            }
//            }
        } catch (InvalidProtocolBufferException e) {
            log.error("Fail to decode pb: {}", e.getMessage());
        } catch (Throwable e) {
            log.error("严重错误，无法处理!", e);
        }
    }

    @Override
    public void handleError(Throwable t) {
        log.error("", t);
    }


}
