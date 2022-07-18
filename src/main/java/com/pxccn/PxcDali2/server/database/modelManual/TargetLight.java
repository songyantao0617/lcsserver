package com.pxccn.PxcDali2.server.database.modelManual;


import java.io.Serializable;

public class TargetLight implements Serializable {
    private static final long serialVersionUID = 1L;
    String lightUUID;
    int cabinet_ID;
    String ip_address;

    public String getLightUUID() {
        return lightUUID;
    }

    public void setLightUUID(String lightUUID) {
        this.lightUUID = lightUUID;
    }

    public int getCabinet_ID() {
        return cabinet_ID;
    }

    public void setCabinet_ID(int cabinet_ID) {
        this.cabinet_ID = cabinet_ID;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    @Override
    public String toString() {
        return "TargetLight{" +
                "lightUUID=" + lightUUID +
                ", cabinet_ID=" + cabinet_ID +
                ", ip_address='" + ip_address + '\'' +
                '}';
    }
}
