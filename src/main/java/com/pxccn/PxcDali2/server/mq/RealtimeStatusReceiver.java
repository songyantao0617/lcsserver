package com.pxccn.PxcDali2.server.mq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.RealtimeStatusWrapper;
import com.pxccn.PxcDali2.server.events.LightsRealtimeStatusModelEvent;
import com.pxccn.PxcDali2.server.util.CabinetVersion;
import com.pxccn.PxcDali2.server.util.VersionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.pxccn.PxcDali2.server.mq.MqConfigure.consumerQueue_realtime;

@Component
@Slf4j
public class RealtimeStatusReceiver {

    @Autowired
    ApplicationContext context;

    @RabbitListener(queues = consumerQueue_realtime)
    public void inComin1(Object omsg) {
        this.doProcess(omsg);
    }

    @RabbitListener(queues = consumerQueue_realtime)
    public void inComin2(Object omsg) {
        this.doProcess(omsg);
    }

    @RabbitListener(queues = consumerQueue_realtime)
    public void inComing3(Object omsg) {
        this.doProcess(omsg);
    }

    @RabbitListener(queues = consumerQueue_realtime)
    public void inComing4(Object omsg) {
        this.doProcess(omsg);
    }

    private void doProcess(Object omsg) {
        Message msg = (Message) omsg;
        try {
            var decodedMsg = ProtoToServerQueueMsg.FromData(msg.getBody());
            var cabinetVersion = VersionHelper.GetCabinetVersionFromId(decodedMsg.getHeaders().get("ver"));
            if (cabinetVersion == CabinetVersion.A0052) {
                if (decodedMsg instanceof RealtimeStatusWrapper) {
                    var lights = ((RealtimeStatusWrapper) decodedMsg).getLightRealtimeStatus();
                    var devs = ((RealtimeStatusWrapper) decodedMsg).getDeviceRealtimeStatus();
                    context.publishEvent(new LightsRealtimeStatusModelEvent(this, lights));
                }
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("Fail to decode pb: {}", e.getMessage());
        }
    }
}
