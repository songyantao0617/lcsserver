package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.model.DeviceRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.model.DoLightRealtimeStatusModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RealtimeStatusWrapper extends ProtoToServerQueueMsg<LcsProtos.RealtimeStatus> {
    public static final String TypeUrl = "type.googleapis.com/RealtimeStatus";
    private List<Dali2LightRealtimeStatusModel> dali2LightRealtimeStatus = Collections.EMPTY_LIST;
    private List<DeviceRealtimeStatusModel> deviceRealtimeStatus = Collections.EMPTY_LIST;
    private List<DoLightRealtimeStatusModel> doLightRealtimeStatus = Collections.EMPTY_LIST;

    public RealtimeStatusWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.RealtimeStatus v = pb.getPayload().unpack(LcsProtos.RealtimeStatus.class);
        if (v.getDali2LightStatusList().size() > 0) {
            this.dali2LightRealtimeStatus = v.getDali2LightStatusList().stream().map(Dali2LightRealtimeStatusModel::new).collect(Collectors.toList());
        }
        if (v.getDali2DeviceStatusList().size() > 0) {
            this.deviceRealtimeStatus = v.getDali2DeviceStatusList().stream().map(DeviceRealtimeStatusModel::new).collect(Collectors.toList());
        }
        if (v.getDoLightStatusList().size() > 0) {
            this.doLightRealtimeStatus = v.getDoLightStatusList().stream().map(DoLightRealtimeStatusModel::new).collect(Collectors.toList());
        }
    }

    public RealtimeStatusWrapper(ToServerMsgParam param,
                                 List<Dali2LightRealtimeStatusModel> dali2LightRealtimeStatus,
                                 List<DeviceRealtimeStatusModel> deviceRealtimeStatus,
                                 List<DoLightRealtimeStatusModel> doLightRealtimeStatus

    ) {
        super(param);
        this.dali2LightRealtimeStatus = dali2LightRealtimeStatus;
        this.deviceRealtimeStatus = deviceRealtimeStatus;
        this.doLightRealtimeStatus = doLightRealtimeStatus;
    }

    public List<Dali2LightRealtimeStatusModel> getDali2LightRealtimeStatus() {
        return dali2LightRealtimeStatus;
    }

    public List<DeviceRealtimeStatusModel> getDeviceRealtimeStatus() {
        return deviceRealtimeStatus;
    }

    public List<DoLightRealtimeStatusModel> getDoLightRealtimeStatus() {
        return doLightRealtimeStatus;
    }

    @Override
    protected LcsProtos.RealtimeStatus.Builder internal_get_payload() {
        return LcsProtos.RealtimeStatus.newBuilder()
                .addAllDali2LightStatus(this.dali2LightRealtimeStatus.stream().map(Dali2LightRealtimeStatusModel::getPb).collect(Collectors.toList()))
                .addAllDali2DeviceStatus(this.deviceRealtimeStatus.stream().map(DeviceRealtimeStatusModel::getPb).collect(Collectors.toList()))
                .addAllDoLightStatus(this.doLightRealtimeStatus.stream().map(DoLightRealtimeStatusModel::getPb).collect(Collectors.toList()))
                ;
    }
}
