package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.model.CabinetLogEventModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.RoomActionLogWrapper;

import java.util.List;

public class RoomActionLogEvent extends FwBaseEvent {
    public RoomActionLogWrapper getWrapper() {
        return wrapper;
    }

    RoomActionLogWrapper wrapper;
    int cabinetId;

    public RoomActionLogEvent(Object source,RoomActionLogWrapper wrapper, int cabinetId) {
        super(source);
        this.wrapper = wrapper;
        this.cabinetId = cabinetId;
    }

    public int getCabinetId() {
        return cabinetId;
    }


}
