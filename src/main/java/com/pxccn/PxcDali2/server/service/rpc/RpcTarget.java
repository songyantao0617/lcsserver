package com.pxccn.PxcDali2.server.service.rpc;

import static com.pxccn.PxcDali2.server.mq.config.MqConfigure.switch_ToPlcCommon;

public class RpcTarget {

    public static RpcTarget CommonToAllCabinet = new RpcTarget(-1);

    public static RpcTarget ToCabinet(int cabinetId){
        return new RpcTarget(cabinetId);
    }

    public String toFriendlyString() {
        if (this.isAll) {
            return "目的地：全场控制器";
        } else {
            return "目的地：" + this.target;
        }
    }

    private boolean isAll;
    private int target;

    private RpcTarget(int targetCabinetId) {
        if (targetCabinetId < 0) {
            this.isAll = true;
            this.rk = "all";
        } else {
            this.target = targetCabinetId;
            this.rk = String.valueOf(targetCabinetId);

        }
        this.ec = switch_ToPlcCommon;
    }


    private final String rk;
    private final String ec;


    public String getRoutingKey() {
        return this.rk;
    }

    public String getExchange() {
        return this.ec;
    }
}
