package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DetailInfoRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.DetailInfoRequest> {
    public static final String TypeUrl = "type.googleapis.com/DetailInfoRequest";

    public boolean isGetAll() {
        return getAll;
    }

    public List<UUID> getResourceUuids() {
        return resourceUuids;
    }

    private final boolean getAll;
    private final List<UUID> resourceUuids;


    //反序列化使用
    public DetailInfoRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.DetailInfoRequest v = pb.getPayload().unpack(LcsProtos.DetailInfoRequest.class);
        this.getAll = v.getGetAll();
        this.resourceUuids = v.getResourceUuidsList().stream().map(Util::ToUuid).collect(Collectors.toList());
    }

    public DetailInfoRequestWrapper(ProtoHeaders headers, boolean getAll, List<UUID> resourceUuids) {
        super(headers);
        this.getAll = getAll;
        if( resourceUuids == null){
            this.resourceUuids = Collections.EMPTY_LIST;
        }else{
            this.resourceUuids = resourceUuids;
        }
    }

    @Override
    protected LcsProtos.DetailInfoRequest.Builder internal_get_payload() {
        return LcsProtos.DetailInfoRequest.newBuilder()
                .setGetAll(this.getAll)
                .addAllResourceUuids(this.resourceUuids.stream().map(Util::ToPbUuid).collect(Collectors.toList()));
    }

}
