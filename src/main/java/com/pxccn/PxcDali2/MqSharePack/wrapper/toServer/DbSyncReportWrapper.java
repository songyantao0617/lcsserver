package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;


public class DbSyncReportWrapper extends ProtoToServerQueueMsg<LcsProtos.DbSyncReport> {
    public static final String TypeUrl = "type.googleapis.com/DbSyncReport";

    final boolean success;

    //反向构造
    public DbSyncReportWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.DbSyncReport v = pb.getPayload().unpack(LcsProtos.DbSyncReport.class);
        this.success = v.getSuccess();
    }

    //正向构造
    public DbSyncReportWrapper(ToServerMsgParam param,
                               boolean success
    ) {
        super(param);
        this.success = success;
    }


    @Override
    protected LcsProtos.DbSyncReport.Builder internal_get_payload() {
        return LcsProtos.DbSyncReport.newBuilder()
                .setSuccess(this.success)
                ;
    }
}
