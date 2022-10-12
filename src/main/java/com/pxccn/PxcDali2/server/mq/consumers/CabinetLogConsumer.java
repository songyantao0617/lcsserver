package com.pxccn.PxcDali2.server.mq.consumers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetLogEventsWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.RealtimeStatusWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.RoomActionLogWrapper;
import com.pxccn.PxcDali2.common.VersionHelper;
import com.pxccn.PxcDali2.server.events.CabinetLogsEvent;
import com.pxccn.PxcDali2.server.events.DaliLightsRealtimeStatusModelEvent;
import com.pxccn.PxcDali2.server.events.DoLightsRealtimeStatusModelEvent;
import com.pxccn.PxcDali2.server.events.RoomActionLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.pxccn.PxcDali2.server.mq.config.MqConfigure.switch_log;
import static com.pxccn.PxcDali2.server.mq.config.MqConfigure.switch_realtime;

@Component
@Slf4j
public class CabinetLogConsumer extends ComsumerBase {

    @Value("${LcsServer.name}")
    String serverName;

    @Autowired
    ApplicationContext context;

    @Override
    protected void prepare(AmqpAdmin amqpAdmin) {
        amqpAdmin.declareQueue(new Queue(this.getQueueName(), true, false, false));
        amqpAdmin.declareBinding(new Binding(this.getQueueName(), Binding.DestinationType.QUEUE, switch_log, "", null));
    }

    protected int getParallelism() {
        return 8;
    }

    @Override
    public String getQueueName() {
        return "lcs.server." + this.serverName + ".cabinet-log";
    }

    @Override
    public void onMessage(Message message) {
        if (log.isTraceEnabled())
            log.trace("{}-onMessage:{}", getQueueName(), message);
        try {
            var decodedMsg = ProtoToServerQueueMsg.FromData(message.getBody());
            var cabinetVersion = VersionHelper.GetCabinetVersionFromId(decodedMsg.getHeaders().get("ver"));
//            if (cabinetVersion == CabinetVersion.A0052) {
            if (decodedMsg instanceof CabinetLogEventsWrapper) {
                int cabinetId = decodedMsg.getCabinetId();
                var logsWrapper = (CabinetLogEventsWrapper)decodedMsg;
                if(logsWrapper.getModels().size()>0){
                    context.publishEvent(new CabinetLogsEvent(this,logsWrapper.getModels(),cabinetId));
                }
            }else if(decodedMsg instanceof RoomActionLogWrapper){
                int cabinetId = decodedMsg.getCabinetId();
                var wrapper = (RoomActionLogWrapper)decodedMsg;
                context.publishEvent(new RoomActionLogEvent(this,wrapper,cabinetId));
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
