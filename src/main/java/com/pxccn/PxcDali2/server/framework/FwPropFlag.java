package com.pxccn.PxcDali2.server.framework;

public class FwPropFlag {

    public static FwPropFlag READ_ONLY = new FwPropFlag(false);
    public static FwPropFlag READ_WRITE = new FwPropFlag(true);

    public FwPropFlag (boolean writeable){
        this.writeable = writeable;
    }

    final boolean writeable;
    public boolean isWriteable() {
        return writeable;
    }
}
