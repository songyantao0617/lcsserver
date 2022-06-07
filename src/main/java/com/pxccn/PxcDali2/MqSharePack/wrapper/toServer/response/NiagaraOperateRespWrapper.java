package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.model.NiagaraOperateRespModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.List;
import java.util.stream.Collectors;

public class NiagaraOperateRespWrapper extends ResponseWrapper<LcsProtos.NiagaraOperateResp> {
    public static final String TypeUrl = "type.googleapis.com/NiagaraOperateResp";


    List<NiagaraOperateRespModel> responseList;

    public List<NiagaraOperateRespModel> getResponseList() {
        return responseList;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        LcsProtos.Response.Builder builder = super.internal_get_payload();
        builder.setPayload(Any.pack(LcsProtos.NiagaraOperateResp
                .newBuilder()
                .addAllResponses(this.responseList
                        .stream()
                        .map(NiagaraOperateRespModel::getPb)
                        .collect(Collectors.toList()))
                .build()));
        return builder;
    }


    public NiagaraOperateRespWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.NiagaraOperateResp v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.NiagaraOperateResp.class);
        this.responseList = v.getResponsesList().stream().map(NiagaraOperateRespModel::new).collect(Collectors.toList());
    }

    public NiagaraOperateRespWrapper(ResponseParam param, List<NiagaraOperateRespModel> responseList) {
        super(param);
        this.responseList = responseList;
    }

}
