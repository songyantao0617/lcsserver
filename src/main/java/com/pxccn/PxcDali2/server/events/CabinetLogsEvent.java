package com.pxccn.PxcDali2.server.events;

import com.pxccn.PxcDali2.MqSharePack.model.CabinetLogEventModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightRealtimeStatusModel;

import java.util.List;

public class CabinetLogsEvent extends FwBaseEvent {
    List<CabinetLogEventModel> modelList;
    int cabinetId;

    public CabinetLogsEvent(Object source, List<CabinetLogEventModel> modelList, int cabinetId) {
        super(source);
        this.modelList = modelList;
        this.cabinetId = cabinetId;
    }

    public int getCabinetId() {
        return cabinetId;
    }

    public List<CabinetLogEventModel> getModelList() {
        return modelList;
    }

}
