package com.pxccn.PxcDali2.server.framework;

public class FwObject {
    private final boolean _stopping = false;
    String _obj_name;

    public String getName() {
        return this._obj_name;
    }

    public void rename(String name) {
        this._obj_name = name;
    }

}
