package com.pxccn.PxcDali2.server.database.model;

import java.io.Serializable;

public class LightsV2 implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table lights_V2
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private static final long serialVersionUID = 1L;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.lightUUID
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private String lightuuid;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.syncFlag
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Boolean syncflag;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.name
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private String name;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.description
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private String description;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.cabinet_ID
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Integer cabinetId;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.busIndex
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Integer busindex;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.busType
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private String bustype;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.shortAddress
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Integer shortaddress;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.axis_x
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Integer axisX;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.axis_y
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Integer axisY;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column lights_V2.axis_z
     *
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    private Integer axisZ;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.lightUUID
     *
     * @return the value of lights_V2.lightUUID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public String getLightuuid() {
        return lightuuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.lightUUID
     *
     * @param lightuuid the value for lights_V2.lightUUID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setLightuuid(String lightuuid) {
        this.lightuuid = lightuuid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.syncFlag
     *
     * @return the value of lights_V2.syncFlag
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Boolean getSyncflag() {
        return syncflag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.syncFlag
     *
     * @param syncflag the value for lights_V2.syncFlag
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setSyncflag(Boolean syncflag) {
        this.syncflag = syncflag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.name
     *
     * @return the value of lights_V2.name
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.name
     *
     * @param name the value for lights_V2.name
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.description
     *
     * @return the value of lights_V2.description
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.description
     *
     * @param description the value for lights_V2.description
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.cabinet_ID
     *
     * @return the value of lights_V2.cabinet_ID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Integer getCabinetId() {
        return cabinetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.cabinet_ID
     *
     * @param cabinetId the value for lights_V2.cabinet_ID
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setCabinetId(Integer cabinetId) {
        this.cabinetId = cabinetId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.busIndex
     *
     * @return the value of lights_V2.busIndex
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Integer getBusindex() {
        return busindex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.busIndex
     *
     * @param busindex the value for lights_V2.busIndex
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setBusindex(Integer busindex) {
        this.busindex = busindex;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.busType
     *
     * @return the value of lights_V2.busType
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public String getBustype() {
        return bustype;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.busType
     *
     * @param bustype the value for lights_V2.busType
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setBustype(String bustype) {
        this.bustype = bustype;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.shortAddress
     *
     * @return the value of lights_V2.shortAddress
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Integer getShortaddress() {
        return shortaddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.shortAddress
     *
     * @param shortaddress the value for lights_V2.shortAddress
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setShortaddress(Integer shortaddress) {
        this.shortaddress = shortaddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.axis_x
     *
     * @return the value of lights_V2.axis_x
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Integer getAxisX() {
        return axisX;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.axis_x
     *
     * @param axisX the value for lights_V2.axis_x
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setAxisX(Integer axisX) {
        this.axisX = axisX;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.axis_y
     *
     * @return the value of lights_V2.axis_y
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Integer getAxisY() {
        return axisY;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.axis_y
     *
     * @param axisY the value for lights_V2.axis_y
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setAxisY(Integer axisY) {
        this.axisY = axisY;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column lights_V2.axis_z
     *
     * @return the value of lights_V2.axis_z
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public Integer getAxisZ() {
        return axisZ;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column lights_V2.axis_z
     *
     * @param axisZ the value for lights_V2.axis_z
     * @mbg.generated Sun Jun 26 22:36:19 CST 2022
     */
    public void setAxisZ(Integer axisZ) {
        this.axisZ = axisZ;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table lights_V2
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
        sb.append(", syncflag=").append(syncflag);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", cabinetId=").append(cabinetId);
        sb.append(", busindex=").append(busindex);
        sb.append(", bustype=").append(bustype);
        sb.append(", shortaddress=").append(shortaddress);
        sb.append(", axisX=").append(axisX);
        sb.append(", axisY=").append(axisY);
        sb.append(", axisZ=").append(axisZ);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}