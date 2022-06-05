package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.MqSharePack.model.DeviceRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.model.LightRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RealtimeStatusWrapper extends ProtoToServerQueueMsg<LcsProtos.RealtimeStatus> {
    public static final String TypeUrl = "type.googleapis.com/RealtimeStatus";

    public RealtimeStatusWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.RealtimeStatus v = pb.getPayload().unpack(LcsProtos.RealtimeStatus.class);
        if (v.getLightStatusList().size() > 0) {
            this.lightRealtimeStatus = v.getLightStatusList().stream().map(LightRealtimeStatusModel::new).collect(Collectors.toList());
        }
        if (v.getDeviceStatusList().size() > 0) {
            this.deviceRealtimeStatus = v.getDeviceStatusList().stream().map(DeviceRealtimeStatusModel::new).collect(Collectors.toList());
        }
    }

    public RealtimeStatusWrapper(ToServerMsgParam param,List<LightRealtimeStatusModel> lightRealtimeStatus, List<DeviceRealtimeStatusModel> deviceRealtimeStatus){
        super(param);
        this.lightRealtimeStatus = lightRealtimeStatus;
        this.deviceRealtimeStatus = deviceRealtimeStatus;
    }

    public List<LightRealtimeStatusModel> getLightRealtimeStatus() {
        return lightRealtimeStatus;
    }

    public List<DeviceRealtimeStatusModel> getDeviceRealtimeStatus() {
        return deviceRealtimeStatus;
    }

    private List<LightRealtimeStatusModel> lightRealtimeStatus = Collections.EMPTY_LIST;
    private List<DeviceRealtimeStatusModel> deviceRealtimeStatus = Collections.EMPTY_LIST;

    @Override
    protected LcsProtos.RealtimeStatus.Builder internal_get_payload() {
        return LcsProtos.RealtimeStatus.newBuilder()
                .addAllLightStatus(this.lightRealtimeStatus.stream().map(LightRealtimeStatusModel::getPb).collect(Collectors.toList()))
                .addAllDeviceStatus(this.deviceRealtimeStatus.stream().map(DeviceRealtimeStatusModel::getPb).collect(Collectors.toList()));
    }
}
