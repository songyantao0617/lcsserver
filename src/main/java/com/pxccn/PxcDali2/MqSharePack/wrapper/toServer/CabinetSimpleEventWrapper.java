package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class CabinetSimpleEventWrapper extends ProtoToServerQueueMsg<LcsProtos.CabinetSimpleEvent> {
    public static final String TypeUrl = "type.googleapis.com/CabinetSimpleEvent";
    private final UUID uuid;
    private final Event event;

    public CabinetSimpleEventWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.CabinetSimpleEvent v = pb.getPayload().unpack(LcsProtos.CabinetSimpleEvent.class);
        this.uuid = Util.ToUuid(v.getUuid());
        Event event;
        try {
            event = Event.valueOf(v.getEvent());
        } catch (IllegalArgumentException ignore) {
            event = Event.Unknown;
        }
        this.event = event;
    }

    public CabinetSimpleEventWrapper(ToServerMsgParam param, UUID uuid, Event event) {
        super(param);
        this.uuid = uuid;
        this.event = event;
    }

    public static CabinetSimpleEventWrapper RoomAdded(ToServerMsgParam param, UUID uuid) {
        return new CabinetSimpleEventWrapper(param, uuid, Event.RoomAdded);
    }

    public static CabinetSimpleEventWrapper RoomRemoved(ToServerMsgParam param, UUID uuid) {
        return new CabinetSimpleEventWrapper(param, uuid, Event.RoomRemoved);
    }

    public static CabinetSimpleEventWrapper RoomInfoChange(ToServerMsgParam param, UUID uuid) {
        return new CabinetSimpleEventWrapper(param, uuid, Event.RoomInfoChange);
    }

    public static CabinetSimpleEventWrapper LightAdded(ToServerMsgParam param, UUID uuid) {
        return new CabinetSimpleEventWrapper(param, uuid, Event.LightAdded);
    }

    public static CabinetSimpleEventWrapper LightRemoved(ToServerMsgParam param, UUID uuid) {
        return new CabinetSimpleEventWrapper(param, uuid, Event.LightRemoved);
    }

    public static CabinetSimpleEventWrapper LightInfoChange(ToServerMsgParam param, UUID uuid) {
        return new CabinetSimpleEventWrapper(param, uuid, Event.LightInfoChange);
    }

    public static CabinetSimpleEventWrapper CabinetInfoChange(ToServerMsgParam param) {
        return new CabinetSimpleEventWrapper(param, new UUID(0, 0), Event.CabinetInfoChange);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    protected LcsProtos.CabinetSimpleEvent.Builder internal_get_payload() {
        return LcsProtos.CabinetSimpleEvent.newBuilder()
                .setUuid(Util.ToPbUuid(this.uuid))
                .setEvent(this.event.name())
                ;
    }


    public enum Event {
        Unknown,
        RoomAdded,
        RoomRemoved,
        RoomInfoChange,
        LightAdded,
        LightRemoved,
        LightInfoChange,
        CabinetInfoChange
    }
}
