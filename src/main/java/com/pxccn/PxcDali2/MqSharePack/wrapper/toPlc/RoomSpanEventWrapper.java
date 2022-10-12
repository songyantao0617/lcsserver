package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class RoomSpanEventWrapper extends ProtoToPlcQueueMsg<LcsProtos.RoomSpanEvent> {
    public static final String TypeUrl = "type.googleapis.com/RoomSpanEvent";

    public int getVersion() {
        return version;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getRandId() {
        return randId;
    }

    public Dali2LightCommandModel getLightCommandModel() {
        return lightCommandModel;
    }

    public Dt8CommandModel getDt8CommandModel() {
        return dt8CommandModel;
    }

    public String getSource() {
        return source;
    }

    public int getFirstFireCabinetId() {
        return firstFireCabinetId;
    }

    int version;
    UUID uuid;
    long randId;
    Dali2LightCommandModel lightCommandModel;
    Dt8CommandModel dt8CommandModel;
    String source;
    int firstFireCabinetId;



    //反向构造
    public RoomSpanEventWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.RoomSpanEvent v = pb.getPayload().unpack(LcsProtos.RoomSpanEvent.class);
        this.version = v.getVersion();
        this.uuid = Util.ToUuid(v.getUuid());
        this.randId = v.getRandId();
        this.lightCommandModel = new Dali2LightCommandModel(v.getLightCommand());
        this.dt8CommandModel = new Dt8CommandModel(v.getDt8Command());
        this.source = v.getSource();
        this.firstFireCabinetId = v.getFirstFireCabinet();
    }

    //正向构造
    public RoomSpanEventWrapper(ProtoHeaders headers,
                                int ver,
                                UUID uuid,
                                long randId,
                                Dali2LightCommandModel lightCommandModel,
                                Dt8CommandModel dt8CommandModel,
                                String source,
                                int firstFireCabinetId
    ) {
        super(headers);
        this.version = ver;
        this.uuid = uuid;
        this.randId = randId;
        this.lightCommandModel = lightCommandModel;
        this.dt8CommandModel = dt8CommandModel;
        this.source = source;
        this.firstFireCabinetId = firstFireCabinetId;
    }

    @Override
    protected LcsProtos.RoomSpanEvent.Builder internal_get_payload() {
        return LcsProtos.RoomSpanEvent.newBuilder()
                .setVersion(this.version)
                .setUuid(Util.ToPbUuid(this.uuid))
                .setRandId(this.randId)
                .setLightCommand(this.lightCommandModel.getPb())
                .setDt8Command(this.dt8CommandModel.getPb())
                .setSource(this.source)
                .setFirstFireCabinet(this.firstFireCabinetId)
                ;
    }

}
