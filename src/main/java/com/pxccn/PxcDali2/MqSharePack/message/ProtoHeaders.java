package com.pxccn.PxcDali2.MqSharePack.message;

import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.HashMap;
import java.util.Map;

public class ProtoHeaders implements QueueMsgHeaders<LcsProtos.Headers> {

    public ProtoHeaders() {
    }

    public ProtoHeaders(LcsProtos.Headers headers){
        headers.getKvsList().forEach(kv->{
            this.kv_map.put(kv.getK(),kv.getV());
        });
    }

    protected final Map<String, String> kv_map = new HashMap<>();

    @Override
    public void put(String key, String value) {
        kv_map.put(key, value);
    }

    @Override
    public String get(String key) {
        return kv_map.get(key);
    }

    @Override
    public LcsProtos.Headers getInternalData() {
        LcsProtos.Headers.Builder builder = LcsProtos.Headers.newBuilder();
        kv_map.forEach((k, v) -> {
            builder.addKvs(LcsProtos.sKsV.newBuilder().setK(k).setV(v).build());
        });
        return builder.build();
    }

}