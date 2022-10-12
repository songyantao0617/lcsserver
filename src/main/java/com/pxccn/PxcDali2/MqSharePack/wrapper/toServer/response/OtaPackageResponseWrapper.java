package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;


public class OtaPackageResponseWrapper extends ResponseWrapper<LcsProtos.OtaPackageResponse> {
    public static final String TypeUrl = "type.googleapis.com/OtaPackageResponse";

    @Override
    public String toString() {
        return "OtaPackageResponseWrapper{" +
                "success=" + success +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }

    boolean success;
    String errorMsg;

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    protected LcsProtos.Response.Builder internal_get_payload() {
        LcsProtos.Response.Builder builder = super.internal_get_payload();
        builder.setPayload(Any.pack(LcsProtos.OtaPackageResponse
                .newBuilder()
                        .setErrorMsg(this.errorMsg)
                        .setSuccess(this.success)

                .build()));
        return builder;
    }


    //反向构造
    public OtaPackageResponseWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.OtaPackageResponse v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.OtaPackageResponse.class);
        this.success = v.getSuccess();
        this.errorMsg = v.getErrorMsg();
    }

    //正向构造
    public OtaPackageResponseWrapper(ResponseParam param,
                                     boolean success,
                                     String errorMsg
    ) {
        super(param);
        this.success = success;
        this.errorMsg = errorMsg;
    }

}