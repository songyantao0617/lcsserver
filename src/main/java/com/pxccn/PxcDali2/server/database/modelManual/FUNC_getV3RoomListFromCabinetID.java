package com.pxccn.PxcDali2.server.database.modelManual;


import java.io.Serializable;

public class FUNC_getV3RoomListFromCabinetID implements Serializable {
    private static final long serialVersionUID = 1L;
    String roomUUID;

    public String getRoomUUID() {
        return roomUUID;
    }

    public void setRoomUUID(String roomUUID) {
        this.roomUUID = roomUUID;
    }

    @Override
    public String toString() {
        return "FUNC_getV3RoomListFromCabinetID{" +
                "roomUUID=" + roomUUID +
                '}';
    }
}
