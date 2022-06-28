package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightDetailModel;
import com.pxccn.PxcDali2.MqSharePack.model.DoLightDetailModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LightsDetailUploadWrapper extends ProtoToServerQueueMsg<LcsProtos.LightsDetailUpload> {
    public static final String TypeUrl = "type.googleapis.com/LightsDetailUpload";
    private List<Dali2LightDetailModel> dali2LightDetailModels = Collections.EMPTY_LIST;
    private List<DoLightDetailModel> doLightDetailModels = Collections.EMPTY_LIST;

    public LightsDetailUploadWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.LightsDetailUpload v = pb.getPayload().unpack(LcsProtos.LightsDetailUpload.class);
        if (v.getDali2LightDetailList().size() > 0)
            this.dali2LightDetailModels = v.getDali2LightDetailList().stream().map(Dali2LightDetailModel::new).collect(Collectors.toList());
        if (v.getDoLightDetailList().size() > 0)
            this.doLightDetailModels = v.getDoLightDetailList().stream().map(DoLightDetailModel::new).collect(Collectors.toList());
    }

    public LightsDetailUploadWrapper(ToServerMsgParam param,
                                     List<Dali2LightDetailModel> lightDetailModels,
                                     List<DoLightDetailModel> doDetailModels

    ) {
        super(param);
        this.dali2LightDetailModels = lightDetailModels;
        this.doLightDetailModels = doDetailModels;
    }

    public List<Dali2LightDetailModel> getDali2LightDetailModels() {
        return dali2LightDetailModels;
    }

    public List<DoLightDetailModel> getDoLightDetailModels() {
        return doLightDetailModels;
    }

    @Override
    protected LcsProtos.LightsDetailUpload.Builder internal_get_payload() {
        return LcsProtos.LightsDetailUpload.newBuilder()
                .addAllDali2LightDetail(this.dali2LightDetailModels.stream().map(Dali2LightDetailModel::getPb).collect(Collectors.toList()))
                .addAllDoLightDetail(this.doLightDetailModels.stream().map(DoLightDetailModel::getPb).collect(Collectors.toList()));
    }
}
