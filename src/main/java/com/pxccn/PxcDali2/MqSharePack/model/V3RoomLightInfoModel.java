package com.pxccn.PxcDali2.MqSharePack.model;

import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class V3RoomLightInfoModel implements IPbModel<LcsProtos.V3RoomLightInfo> {

    public V3RoomLightInfoModel(
            List<Room> rooms
    ) {
        this.rooms = rooms;
    }

    public V3RoomLightInfoModel(LcsProtos.V3RoomLightInfo pb) {
        this.rooms = pb.getRoomsList().stream().map(Room::new).collect(Collectors.toList());
    }


    public LcsProtos.V3RoomLightInfo getPb() {
        return LcsProtos.V3RoomLightInfo.newBuilder()
                .addAllRooms(this.rooms.stream().map(Room::getPb).collect(Collectors.toList()))
                .build();
    }

    public List<Room> getRooms() {
        return rooms;
    }

    List<Room> rooms;

    public static class Room {
        public Room(UUID uuid, String name,String description,List<TargetLight> targets) {
            if(description==null){
                description = "";
            }
            if(name == null){
                name="unname";
            }
            this.uuid = uuid;
            this.name = name;
            this.description = description;
            this.targets = targets;
        }

        public Room(LcsProtos.V3RoomLightInfo.Room pb) {
            this.uuid = Util.ToUuid(pb.getUuid());
            this.name =pb.getName();
            this.description = pb.getDescription();
            this.targets = pb.getTargetLightsList().stream().map(TargetLight::new).collect(Collectors.toList());
        }

        public UUID getUuid() {
            return uuid;
        }

        public List<TargetLight> getTargets() {
            return targets;
        }


        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Room{" +
                    "uuid=" + uuid +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", targets=" + targets +
                    '}';
        }

        UUID uuid;
        String name;
        String description;
        List<TargetLight> targets;

        LcsProtos.V3RoomLightInfo.Room getPb() {
            return LcsProtos.V3RoomLightInfo.Room.newBuilder()
                    .setUuid(Util.ToPbUuid(this.uuid))
                    .setName(this.name)
                    .setDescription(this.description)
                    .addAllTargetLights(this.targets.stream()
                            .map(TargetLight::getPb)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    public static class TargetLight {
        public TargetLight(UUID lightUuid, int cabinetId, String ipAddress) {
            this.lightUuid = lightUuid;
            this.cabinetId = cabinetId;
            this.ipAddress = ipAddress;
        }

        public TargetLight(LcsProtos.V3RoomLightInfo.TargetLight pb) {
            this.lightUuid = Util.ToUuid(pb.getLightUuid());
            this.cabinetId = pb.getCabinetId();
            this.ipAddress = pb.getIpAddress();
        }

        public UUID getLightUuid() {
            return lightUuid;
        }

        public int getCabinetId() {
            return cabinetId;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        @Override
        public String toString() {
            return "TargetLight{" +
                    "lightUuid=" + lightUuid +
                    ", cabinetId=" + cabinetId +
                    ", ipAddress='" + ipAddress + '\'' +
                    '}';
        }

        UUID lightUuid;
        int cabinetId;
        String ipAddress;

        LcsProtos.V3RoomLightInfo.TargetLight getPb() {
            return LcsProtos.V3RoomLightInfo.TargetLight.newBuilder()
                    .setLightUuid(Util.ToPbUuid(this.lightUuid))
                    .setCabinetId(this.cabinetId)
                    .setIpAddress(this.ipAddress)
                    .build();
        }

    }


}
