package com.pxccn.PxcDali2.server.space;

import com.pxccn.PxcDali2.server.events.ToComponent.ToComponentEvent;
import com.pxccn.PxcDali2.server.framework.FwComponent;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.lights.LightsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TopSpace extends FwComponent {

    FwProperty<LightsManager> LightsManager;
    FwProperty<CabinetsManager> CabinetsManager;


    public boolean isReady() {
        return ready;
    }

    private boolean ready;

    @EventListener
    public void EventEntry(ToComponentEvent e) {
        this.routeEvent(e.getRoutingKey(), e);
    }

    public void started() {
        LightsManager = addProperty(context.getBean(LightsManager.class), "LightsManager");
        CabinetsManager = addProperty(context.getBean(CabinetsManager.class), "CabinetsManager");
    }

    public void onServerReady(LcsNodeManager nodeManager) {
        this.start();
        log.info("Top Space created");
        this.ready = true;
    }

    public LightsManager getLightsManager() {
        return this.LightsManager.get();
    }

    public CabinetsManager getCabinetsManager() {
        return this.CabinetsManager.get();
    }
}
