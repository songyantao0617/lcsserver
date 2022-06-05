package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.model.LightRealtimeStatusModel;

import java.util.List;

public class LightsRealtimeStatusModelEvent extends FwBaseEvent{
    List<LightRealtimeStatusModel> modelList;

    public List<LightRealtimeStatusModel> getModelList() {
        return modelList;
    }

    public LightsRealtimeStatusModelEvent(Object source, List<LightRealtimeStatusModel> modelList) {
        super(source);
        this.modelList = modelList;
    }

}
