package com.pxccn.PxcDali2.server.database.model;

import java.io.Serializable;

public class RoomLightMapV3 implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table room_light_map_V3
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private static final long serialVersionUID = 1L;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column room_light_map_V3.lightUUID
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private String lightuuid;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column room_light_map_V3.roomUUID
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private String roomuuid;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column room_light_map_V3.cabinet_ID
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Integer cabinetId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column room_light_map_V3.lightUUID
     *
     * @return the value of room_light_map_V3.lightUUID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public String getLightuuid() {
        return lightuuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column room_light_map_V3.lightUUID
     *
     * @param lightuuid the value for room_light_map_V3.lightUUID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setLightuuid(String lightuuid) {
        this.lightuuid = lightuuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column room_light_map_V3.roomUUID
     *
     * @return the value of room_light_map_V3.roomUUID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public String getRoomuuid() {
        return roomuuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column room_light_map_V3.roomUUID
     *
     * @param roomuuid the value for room_light_map_V3.roomUUID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setRoomuuid(String roomuuid) {
        this.roomuuid = roomuuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column room_light_map_V3.cabinet_ID
     *
     * @return the value of room_light_map_V3.cabinet_ID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Integer getCabinetId() {
        return cabinetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column room_light_map_V3.cabinet_ID
     *
     * @param cabinetId the value for room_light_map_V3.cabinet_ID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setCabinetId(Integer cabinetId) {
        this.cabinetId = cabinetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table room_light_map_V3
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", lightuuid=").append(lightuuid);
        sb.append(", roomuuid=").append(roomuuid);
        sb.append(", cabinetId=").append(cabinetId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}