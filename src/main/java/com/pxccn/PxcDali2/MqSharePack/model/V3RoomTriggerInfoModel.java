package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class V3RoomTriggerInfoModel implements IPbModel<LcsProtos.V3RoomTriggerInfo> {

    public V3RoomTriggerInfoModel(
            List<Trigger> triggers
    ) {
        this.triggers = triggers;
    }

    public V3RoomTriggerInfoModel(LcsProtos.V3RoomTriggerInfo pb) {
        this.triggers = pb.getTriggersList().stream().map(Trigger::new).collect(Collectors.toList());
    }


    public LcsProtos.V3RoomTriggerInfo getPb() {
        return LcsProtos.V3RoomTriggerInfo.newBuilder()
                .addAllTriggers(this.triggers.stream().map(Trigger::getPb).collect(Collectors.toList()))
                .build();
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    List<Trigger> triggers;

    public static class Trigger{
        public Trigger(UUID triggerUUID, String triggerType, UUID roomUUID, String config) {
            this.triggerUUID = triggerUUID;
            this.triggerType = triggerType;
            this.roomUUID = roomUUID;
            this.config = config;
        }

        public Trigger(LcsProtos.V3RoomTriggerInfo.Trigger pb){
            this.triggerUUID = Util.ToUuid(pb.getTriggerUuid());
            this.triggerType = pb.getTriggerType();
            this.roomUUID = Util.ToUuid(pb.getRoomUuid());
            this.config = pb.getTriggerConfig();
        }

        public UUID getTriggerUUID() {
            return triggerUUID;
        }

        public String getTriggerType() {
            return triggerType;
        }

        public UUID getRoomUUID() {
            return roomUUID;
        }

        public String getConfig() {
            return config;
        }

        public LcsProtos.V3RoomTriggerInfo.Trigger getPb(){
            return LcsProtos.V3RoomTriggerInfo.Trigger.newBuilder()
                    .setTriggerUuid(Util.ToPbUuid(this.triggerUUID))
                    .setTriggerType(this.triggerType)
                    .setRoomUuid(Util.ToPbUuid(this.roomUUID))
                    .setTriggerConfig(this.config)
                    .build();
        }

        @Override
        public String toString() {
            return "Trigger{" +
                    "triggerUUID=" + triggerUUID +
                    ", triggerType='" + triggerType + '\'' +
                    ", roomUUID=" + roomUUID +
                    ", config='" + config + '\'' +
                    '}';
        }

        UUID triggerUUID;
        String triggerType;
        UUID roomUUID;
        String config;

    }





}
