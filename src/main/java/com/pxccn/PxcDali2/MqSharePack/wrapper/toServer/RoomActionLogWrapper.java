package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;


public class RoomActionLogWrapper extends ProtoToServerQueueMsg<LcsProtos.RoomActionLog> {
    public static final String TypeUrl = "type.googleapis.com/RoomActionLog";

    @Override
    public String toString() {
        return "RoomActionLogWrapper{" +
                "timestamp=" + timestamp +
                ", roomUuid=" + roomUuid +
                ", dali2LightCommandModel=" + dali2LightCommandModel +
                ", dt8CommandModel=" + dt8CommandModel +
                ", source='" + source + '\'' +
                '}';
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public UUID getRoomUuid() {
        return roomUuid;
    }

    public Dali2LightCommandModel getDali2LightCommandModel() {
        return dali2LightCommandModel;
    }

    public Dt8CommandModel getDt8CommandModel() {
        return dt8CommandModel;
    }

    public String getSource() {
        return source;
    }

    final long timestamp;
    final UUID roomUuid;
    final Dali2LightCommandModel dali2LightCommandModel;
    final Dt8CommandModel dt8CommandModel;
    final String source;

    //反向构造
    public RoomActionLogWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.RoomActionLog v = pb.getPayload().unpack(LcsProtos.RoomActionLog.class);
        this.timestamp = v.getTimestamp();
        this.roomUuid = Util.ToUuid(v.getRoomUuid());
        this.dali2LightCommandModel = new Dali2LightCommandModel(v.getLightCommand());
        this.dt8CommandModel = new Dt8CommandModel(v.getDt8Command());
        this.source = v.getSource();
    }

    //正向构造
    public RoomActionLogWrapper(ToServerMsgParam param,
                                long timestamp,
                                UUID roomUuid,
                                Dali2LightCommandModel dali2LightCommandModel,
                                Dt8CommandModel dt8CommandModel,
                                String source
    ) {
        super(param);
        this.timestamp = timestamp;
        this.roomUuid = roomUuid;
        this.dali2LightCommandModel = dali2LightCommandModel;
        this.dt8CommandModel = dt8CommandModel;
        this.source = source;
    }


    @Override
    protected LcsProtos.RoomActionLog.Builder internal_get_payload() {
        return LcsProtos.RoomActionLog.newBuilder()
                .setTimestamp(this.timestamp)
                .setRoomUuid(Util.ToPbUuid(this.roomUuid))
                .setLightCommand(this.dali2LightCommandModel.getPb())
                .setDt8Command(this.dt8CommandModel.getPb())
                .setSource(this.source)
                ;
    }
}
