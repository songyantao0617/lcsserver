package com.pxccn.PxcDali2.server.service.opcua;

import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_Event_BaseType;
import com.pxccn.PxcDali2.server.space.TopSpace;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class UaAlarmEventService {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    @Autowired
    OpcuaService opcuaService;
    @Autowired
    TopSpace topSpace;

    public void sendBasicEvent(FwUaComponent source, String message, int severity) {
        if (source == null) {
            source = topSpace.getCockpit();
        }

        if (!opcuaService.getServer().isRunning()) {
            log.error("UA 服务未启动，无法发送事件:{}", message);
        }
        var node = source.getNode();
        if (node == null) {
            log.error("无法获取{}对象Ua节点,事件发送失败:{}", source.getName(), message);
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace("发送UA基本事件 source:{},msg:{},severity:{}", source.getNode().toString(), message, severity);
        }
        executorService.execute(() -> {
            try {
                LCS_Event_BaseType ev = opcuaService.getLcsNodeManager().createEvent(LCS_Event_BaseType.class);
                ev.setMessage(new LocalizedText(message));
                ev.setSource(node);
                ev.setSeverity(severity);
                ev.triggerEvent(null);
            } catch (Exception e) {
                log.error("无法发送BaseEvent:{}", e.getMessage());
            }
        });
    }
}
