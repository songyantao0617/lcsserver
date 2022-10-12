package com.pxccn.PxcDali2.server.service.opcua;

import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_Event_BaseType;
import com.pxccn.PxcDali2.server.space.TopSpace;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OPCUA 事件服务
 */
@Component
@Slf4j
public class UaAlarmEventService {
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Autowired
    OpcuaService opcuaService;
    @Autowired
    TopSpace topSpace;

    @Value("${LcsServer.enableDebugEvent:true}")
    boolean enableDebugEvent;

    @Value("${LcsServer.debugEventSeverity:0}")
    int debugEventSeverity;

    @Value("${LcsServer.successEventSeverity:100}")
    int successEventSeverity;

    @Value("${LcsServer.failureEventSeverity:1000}")
    int failureEventSeverity;

    public void successEvent(FwUaComponent source, String message) {
        this.sendBasicUaEvent(source, message, successEventSeverity);
    }

    public void debugEvent(FwUaComponent source, String message) {
        if (enableDebugEvent)
            this.sendBasicUaEvent(source, message, debugEventSeverity);
    }

    public void failureEvent(FwUaComponent source, String message) {
        this.sendBasicUaEvent(source, message, failureEventSeverity);
    }

    public void sendBasicUaEvent(FwUaComponent source, String message, int severity) {
        log.trace("sendBasicEvent: source={},message={},severity={}", source, message, severity);

        if (source == null) {
            source = topSpace.getCockpit();
        }

        if (!opcuaService.getServer().isRunning()) {
            log.error("UA server not started ,fail to send event:{}", message);
        }
        var node = source.getNode();
        if (node == null) {
            log.error("can not get ua node of '{}' to send event:{}", source.getName(), message);
            return;
        }
        executorService.execute(() -> {
            try {
                LCS_Event_BaseType ev = opcuaService.getLcsNodeManager().createEvent(LCS_Event_BaseType.class);
                ev.setMessage(new LocalizedText(message));
                ev.setSource(node);
                ev.setSeverity(severity);
                ev.triggerEvent(null);
            } catch (Exception e) {
                log.error("fail to send base event:{}", e.getMessage());
            }
        });
    }
}
