package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.CabinetLogEventModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;

import java.util.List;
import java.util.stream.Collectors;


public class CabinetLogEventsWrapper extends ProtoToServerQueueMsg<LcsProtos.CabinetLogEvents> {
    public static final String TypeUrl = "type.googleapis.com/CabinetLogEvents";

    public List<CabinetLogEventModel> getModels() {
        return models;
    }

    List<CabinetLogEventModel> models;

    //反向构造
    public CabinetLogEventsWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.CabinetLogEvents v = pb.getPayload().unpack(LcsProtos.CabinetLogEvents.class);
        this.models = v.getEventsList().stream().map(CabinetLogEventModel::new).collect(Collectors.toList());
    }

    //正向构造
    public CabinetLogEventsWrapper(ToServerMsgParam param,
                                   List<CabinetLogEventModel> models
    ) {
        super(param);
        this.models = models;
    }


    @Override
    protected LcsProtos.CabinetLogEvents.Builder internal_get_payload() {
        return LcsProtos.CabinetLogEvents.newBuilder()
                .addAllEvents(this.models.stream().map(CabinetLogEventModel::getPb).collect(Collectors.toList()))
                ;
    }
}
