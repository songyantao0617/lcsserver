package com.pxccn.PxcDali2.MqSharePack.wrapper.toServer;


import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.UUID;

public class CabinetSimpleEventWrapper extends ProtoToServerQueueMsg<LcsProtos.CabinetSimpleEvent> {
    public static final String TypeUrl = "type.googleapis.com/CabinetSimpleEvent";

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

    private UUID uuid;
    private Event event;

    public enum Event {
        UNKNOW,
        RoomAdded,
        RoomRemoved,
        RoomInfoChange,
        LightAdded,
        LightRemoved,
        LightInfoChange,
        CabinetInfoChange
    }

    public CabinetSimpleEventWrapper(LcsProtos.ToServerMessage pb) throws InvalidProtocolBufferException {
        super(pb);
        LcsProtos.CabinetSimpleEvent v = pb.getPayload().unpack(LcsProtos.CabinetSimpleEvent.class);
        this.uuid = Util.ToUuid(v.getUuid());
        switch (v.getEventValue()) {
            case LcsProtos.CabinetSimpleEvent.E.RoomAdded_VALUE:
                this.event = Event.RoomAdded;
                break;
            case LcsProtos.CabinetSimpleEvent.E.RoomRemoved_VALUE:
                this.event = Event.RoomRemoved;
                break;
            case LcsProtos.CabinetSimpleEvent.E.RoomInfoChange_VALUE:
                this.event = Event.RoomInfoChange;
                break;
            case LcsProtos.CabinetSimpleEvent.E.LightAdded_VALUE:
                this.event = Event.LightAdded;
                break;
            case LcsProtos.CabinetSimpleEvent.E.LightRemoved_VALUE:
                this.event = Event.LightRemoved;
                break;
            case LcsProtos.CabinetSimpleEvent.E.LightInfoChange_VALUE:
                this.event = Event.LightInfoChange;
                break;
            case LcsProtos.CabinetSimpleEvent.E.CabinetInfoChange_VALUE:
                this.event = Event.CabinetInfoChange;
                break;
            default:
                this.event = Event.UNKNOW;
                break;
        }
    }

    public CabinetSimpleEventWrapper(ToServerMsgParam param, UUID uuid, Event event) {
        super(param);
        this.uuid = uuid;
        this.event = event;
    }


    @Override
    protected LcsProtos.CabinetSimpleEvent.Builder internal_get_payload() {
        LcsProtos.CabinetSimpleEvent.Builder b = LcsProtos.CabinetSimpleEvent.newBuilder()
                .setUuid(Util.ToPbUuid(this.uuid));
        switch (this.event) {
            case RoomAdded:
                b.setEvent(LcsProtos.CabinetSimpleEvent.E.RoomAdded);
                break;
            case RoomRemoved:
                b.setEvent(LcsProtos.CabinetSimpleEvent.E.RoomRemoved);
                break;
            case LightAdded:
                b.setEvent(LcsProtos.CabinetSimpleEvent.E.LightAdded);
                break;
            case LightRemoved:
                b.setEvent(LcsProtos.CabinetSimpleEvent.E.LightRemoved);
                break;
            case LightInfoChange:
                b.setEvent(LcsProtos.CabinetSimpleEvent.E.LightInfoChange);
                break;
            case RoomInfoChange:
                b.setEvent(LcsProtos.CabinetSimpleEvent.E.RoomInfoChange);
                break;
            case CabinetInfoChange:
                b.setEvent(LcsProtos.CabinetSimpleEvent.E.CabinetInfoChange);
                break;
        }
        return b;
    }
}
