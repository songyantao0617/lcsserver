package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.NiagaraOperateRequestModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.List;
import java.util.stream.Collectors;

public class NiagaraOperateRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.NiagaraOperateRequest> {
    public static final String TypeUrl = "type.googleapis.com/NiagaraOperateRequest";

    List<NiagaraOperateRequestModel> operates;

    public List<NiagaraOperateRequestModel> getOperates() {
        return operates;
    }

    public boolean isStopAtError() {
        return stopAtError;
    }

    boolean stopAtError;

    //反序列化使用
    public NiagaraOperateRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.NiagaraOperateRequest v = pb.getPayload().unpack(LcsProtos.NiagaraOperateRequest.class);
        this.operates = v.getOperatesList()
                .stream()
                .map(NiagaraOperateRequestModel::new)
                .collect(Collectors.toList());
        this.stopAtError = v.getStopAtError();
    }

    public NiagaraOperateRequestWrapper(ProtoHeaders headers, List<NiagaraOperateRequestModel> operates,boolean stopAtError) {
        super(headers);
        this.operates = operates;
        this.stopAtError = stopAtError;
    }

    @Override
    protected LcsProtos.NiagaraOperateRequest.Builder internal_get_payload() {
        return LcsProtos.NiagaraOperateRequest.newBuilder()
                .addAllOperates(this.operates
                        .stream()
                        .map(NiagaraOperateRequestModel::getPb)
                        .collect(Collectors.toList()))
                .setStopAtError(this.stopAtError);
    }
}
