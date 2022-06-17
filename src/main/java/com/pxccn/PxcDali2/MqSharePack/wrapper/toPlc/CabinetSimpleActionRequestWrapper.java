package com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoHeaders;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class CabinetSimpleActionRequestWrapper extends ProtoToPlcQueueMsg<LcsProtos.CabinetSimpleActionRequest> {
    public static final String TypeUrl = "type.googleapis.com/CabinetSimpleActionRequest";

    public static CabinetSimpleActionRequestWrapper SYNC_DB_BASIC(ProtoHeaders headers) {
        return new CabinetSimpleActionRequestWrapper(headers, Action.SYNC_DB_BASIC);
    }

    public static CabinetSimpleActionRequestWrapper SYNC_DB_ROOM(ProtoHeaders headers) {
        return new CabinetSimpleActionRequestWrapper(headers, Action.SYNC_DB_ROOM);
    }

    public enum Action {
        UNKNOW,
        SYNC_DB_BASIC,
        SYNC_DB_ROOM
    }

    public final Action getAction() {
        return action;
    }

    private final UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    private Action action;

    //反序列化使用
    public CabinetSimpleActionRequestWrapper(LcsProtos.ToPlcMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.CabinetSimpleActionRequest v = pb.getPayload().unpack(LcsProtos.CabinetSimpleActionRequest.class);
        this.uuid = Util.ToUuid(v.getUuid());
        switch (v.getActionValue()) {
            case LcsProtos.CabinetSimpleActionRequest.A.SYNC_DB_BASIC_VALUE:
                this.action = Action.SYNC_DB_BASIC;
                break;
            case LcsProtos.CabinetSimpleActionRequest.A.SYNC_DB_ROOM_VALUE:
                this.action = Action.SYNC_DB_ROOM;
                break;
            default:
                this.action = Action.UNKNOW;
                break;
        }
    }

    public CabinetSimpleActionRequestWrapper(ProtoHeaders headers, Action action) {
        super(headers);
        this.action = action;
        this.uuid = new UUID(0, 0);
    }

    public CabinetSimpleActionRequestWrapper(ProtoHeaders headers, Action action, UUID uuid) {
        super(headers);
        this.action = action;
        this.uuid = uuid;
    }

    @Override
    protected LcsProtos.CabinetSimpleActionRequest.Builder internal_get_payload() {
        LcsProtos.CabinetSimpleActionRequest.Builder builder = LcsProtos.CabinetSimpleActionRequest.newBuilder()
                .setUuid(Util.ToPbUuid(this.uuid));
        switch (this.action) {
            case SYNC_DB_BASIC:
                builder.setAction(LcsProtos.CabinetSimpleActionRequest.A.SYNC_DB_BASIC);
                break;
            case SYNC_DB_ROOM:
                builder.setAction(LcsProtos.CabinetSimpleActionRequest.A.SYNC_DB_ROOM);
                break;
        }

        return builder;
    }

}
