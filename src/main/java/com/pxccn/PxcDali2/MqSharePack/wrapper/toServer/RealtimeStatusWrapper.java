package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.MqSharePack.model.DeviceRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.model.LightRealtimeStatusModel;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;


import java.util.List;
import java.util.stream.Collectors;

public class RealtimeStatusWrapper extends ProtoToServerQueueMsg<LcsProtos.RealtimeStatus> {
    public static final String TypeUrl = "type.googleapis.com/RealtimeStatus";

    public List<LightRealtimeStatusModel> getLightRealtimeStatusModelList() {
        return this.payload.getLightStatusList().stream().map(LightRealtimeStatusModel::new).collect(Collectors.toList());
    }

    public final List<DeviceRealtimeStatusModel> getDeviceRealtimeStatusModelList() {
        return this.payload.getDeviceStatusList().stream().map(DeviceRealtimeStatusModel::new).collect(Collectors.toList());
    }


    public RealtimeStatusWrapper(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.RealtimeStatus payload) {
        super(timestamp, cabinetId, headers, payload);
    }

    public RealtimeStatusWrapper(long timestamp, int cabinetId, ProtoHeaders headers, List<LcsProtos.LightRealtimeStatus> lightRealtimeStatus, List<LcsProtos.DeviceRealtimeStatus> deviceRealtimeStatus) {
        this(timestamp, cabinetId, headers, LcsProtos.RealtimeStatus.newBuilder().addAllLightStatus(lightRealtimeStatus).addAllDeviceStatus(deviceRealtimeStatus).build());
    }

    public RealtimeStatusWrapper(long timestamp, int cabinetId, ProtoHeaders headers, List<LightRealtimeStatusModel> LightRealtimeStatus, List<DeviceRealtimeStatusModel> DeviceRealtimeStatus, Object xxx) {
        this(timestamp, cabinetId, headers, LcsProtos.RealtimeStatus.newBuilder()
                .addAllLightStatus(LightRealtimeStatus.stream().map(LightRealtimeStatusModel::getPb).collect(Collectors.toList()))
                .addAllDeviceStatus(DeviceRealtimeStatus.stream().map(DeviceRealtimeStatusModel::getPb).collect(Collectors.toList()))
                .build());
    }

}
