package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightRealtimeStatusModel;

import java.util.List;

public class DaliLightsRealtimeStatusModelEvent extends FwBaseEvent {
    List<Dali2LightRealtimeStatusModel> modelList;
    int cabinetId;

    public DaliLightsRealtimeStatusModelEvent(Object source, List<Dali2LightRealtimeStatusModel> modelList, int cabinetId) {
        super(source);
        this.modelList = modelList;
        this.cabinetId = cabinetId;
    }

    public int getCabinetId() {
        return cabinetId;
    }

    public List<Dali2LightRealtimeStatusModel> getModelList() {
        return modelList;
    }

}
