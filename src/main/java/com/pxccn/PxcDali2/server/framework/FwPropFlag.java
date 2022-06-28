package com.pxccn.PxcDali2.server.framework;

public class FwPropFlag {

    public static FwPropFlag READ_ONLY = new FwPropFlag(false);
    public static FwPropFlag READ_WRITE = new FwPropFlag(true);
    final boolean writeable;

    public FwPropFlag(boolean writeable) {
        this.writeable = writeable;
    }

    public boolean isWriteable() {
        return writeable;
    }
}
