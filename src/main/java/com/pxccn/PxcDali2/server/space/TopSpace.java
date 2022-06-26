package com.pxccn.PxcDali2.server.space;

import com.pxccn.PxcDali2.server.events.ToComponent.ToComponentEvent;
import com.pxccn.PxcDali2.server.events.TopSpaceReadyEvent;
import com.pxccn.PxcDali2.server.framework.FwComponent;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.cockpit.Cockpit;
import com.pxccn.PxcDali2.server.space.lights.LightsManager;
import com.pxccn.PxcDali2.server.space.rooms.RoomsManager;
import com.pxccn.PxcDali2.server.space.v3Rooms.V3RoomsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TopSpace extends FwComponent {

    FwProperty<LightsManager> LightsManager;
    FwProperty<CabinetsManager> CabinetsManager;
    FwProperty<RoomsManager> RoomsManager;
    FwProperty<V3RoomsManager> V3RoomsManager;

    FwProperty<Cockpit> Cockpit;

    @Autowired
    ApplicationContext context;

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
        RoomsManager = addProperty(context.getBean(RoomsManager.class), "RoomsManager");
        V3RoomsManager = addProperty(context.getBean(V3RoomsManager.class), "V3RoomsManager");
        Cockpit = addProperty(context.getBean(Cockpit.class), "Cockpit");
    }

    public void onServerReady(LcsNodeManager nodeManager) {
        this.start();
        log.info("Top Space created");
        this.ready = true;
        context.publishEvent(new TopSpaceReadyEvent(this));
    }

    public LightsManager getLightsManager() {
        return this.LightsManager.get();
    }

    public CabinetsManager getCabinetsManager() {
        return this.CabinetsManager.get();
    }

    public V3RoomsManager getV3RoomsManager(){
        return this.V3RoomsManager.get();
    }

    public Cockpit getCockpit(){
        return this.Cockpit.get();
    }
}
