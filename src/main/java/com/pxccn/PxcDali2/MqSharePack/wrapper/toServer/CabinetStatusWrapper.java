package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CabinetStatusWrapper extends ProtoToServerQueueMsg<LcsProtos.CabinetStatus> {
    public static final String TypeUrl = "type.googleapis.com/CabinetStatus";

    private final Map<String, String> propsMap = new HashMap<>();
    public Map<String, String> getPropsMap(){
        return this.propsMap;
    }

    public CabinetStatusWrapper(long timestamp, int cabinetId, ProtoHeaders headers, LcsProtos.CabinetStatus payload) {
        super(timestamp, cabinetId, headers, payload);
        payload.getPropsList().forEach(kv -> {
            this.propsMap.put(kv.getK(), kv.getV());
        });
    }

    public CabinetStatusWrapper(long timestamp, int cabinetId, ProtoHeaders headers, Map<String, String> propsMap) {
        this(timestamp, cabinetId, headers,
                LcsProtos.CabinetStatus
                        .newBuilder()
                        .addAllProps(
                                propsMap
                                        .entrySet()
                                        .stream()
                                        .map((v) -> LcsProtos.sKsV
                                                .newBuilder()
                                                .setK(v.getKey())
                                                .setV(v.getValue())
                                                .build())
                                        .collect(Collectors.toList()))
                        .build());
    }

}
