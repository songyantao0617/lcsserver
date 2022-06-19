//package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response;
//
//import com.google.protobuf.Any;
//import com.google.protobuf.InvalidProtocolBufferException;
//import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
//import com.pxccn.PxcDali2.Proto.LcsProtos;
//
//public class CabinetSimpleActionRespWrapper extends ResponseWrapper<LcsProtos.CabinetSimpleActionResp> {
//    public static final String TypeUrl = "type.googleapis.com/CabinetSimpleActionResp";
//
//
//    @Override
//    protected LcsProtos.Response.Builder internal_get_payload() {
//        LcsProtos.Response.Builder builder = super.internal_get_payload();
//        builder.setPayload(Any.pack(LcsProtos.CabinetSimpleActionResp
//                .newBuilder()
//
//                .build()));
//        return builder;
//    }
//
//
//    public CabinetSimpleActionRespWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
//        super(pb);
//        LcsProtos.CabinetSimpleActionResp v = pb.getPayload().unpack(LcsProtos.Response.class).getPayload().unpack(LcsProtos.CabinetSimpleActionResp.class);
//    }
//
//    public CabinetSimpleActionRespWrapper(ResponseParam param) {
//        super(param);
//    }
//
//}
