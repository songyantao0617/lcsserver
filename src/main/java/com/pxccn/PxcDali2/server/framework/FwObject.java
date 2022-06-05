package com.pxccn.PxcDali2.server.framework;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class FwObject {
    private boolean _stopping = false;
    String _obj_name;

    public String getName() {
        return this._obj_name;
    }

    public void rename(String name) {
        this._obj_name = name;
    }

}
