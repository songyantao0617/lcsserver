package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.model.DoLightRealtimeStatusModel;

import java.util.List;

public class DoLightsRealtimeStatusModelEvent extends FwBaseEvent {
    List<DoLightRealtimeStatusModel> modelList;
    int cabinetId;

    public DoLightsRealtimeStatusModelEvent(Object source, List<DoLightRealtimeStatusModel> modelList, int cabinetId) {
        super(source);
        this.modelList = modelList;
        this.cabinetId = cabinetId;
    }

    public int getCabinetId() {
        return cabinetId;
    }

    public List<DoLightRealtimeStatusModel> getModelList() {
        return modelList;
    }

}
