package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CabinetStatusWrapper extends ProtoToServerQueueMsg<LcsProtos.CabinetStatus> {
    public static final String TypeUrl = "type.googleapis.com/CabinetStatus";

    private Map<String, String> propsMap = new HashMap<>();

    public Map<String, String> getPropsMap() {
        return this.propsMap;
    }


    public CabinetStatusWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.CabinetStatus v = pb.getPayload().unpack(LcsProtos.CabinetStatus.class);
        v.getPropsList().forEach(kv -> {
            this.propsMap.put(kv.getK(), kv.getV());
        });
    }

    public CabinetStatusWrapper(ToServerMsgParam param, Map<String, String> propsMap) {
        super(param);
        this.propsMap = propsMap;
    }


    @Override
    protected LcsProtos.CabinetStatus.Builder internal_get_payload() {
        return LcsProtos.CabinetStatus.newBuilder()
                .addAllProps(
                        this.propsMap
                                .entrySet()
                                .stream()
                                .map((v) -> LcsProtos.sKsV
                                        .newBuilder()
                                        .setK(v.getKey())
                                        .setV(v.getValue())
                                        .build())
                                .collect(Collectors.toList()));
    }
}
