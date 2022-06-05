package com.pxccn.PxcDali2.server.util;

public enum CabinetVersion {
    NONE(""),
    A0052("Alpha V0.052");

    public String identifier;

    CabinetVersion(String id) {
        this.identifier = id;
    }
}
