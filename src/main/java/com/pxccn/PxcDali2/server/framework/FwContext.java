package com.pxccn.PxcDali2.server.framework;

public class FwContext extends FwObject {

    public static FwContext BY_OPCUA = new FwContext(true);
    public static FwContext INTERNAL = new FwContext(false);
    final boolean byOpcua;

    public FwContext(boolean isOpc) {
        this.byOpcua = isOpc;
    }

    public boolean isByOpcua() {
        return this.byOpcua;
    }
}
