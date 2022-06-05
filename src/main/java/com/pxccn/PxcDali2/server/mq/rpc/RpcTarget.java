package com.pxccn.PxcDali2.server.mq.rpc;

import static com.pxccn.PxcDali2.server.mq.MqConfigure.switch_ToPlcCommon;

public class RpcTarget {

    public static RpcTarget CommonToAllCabinet = new RpcTarget(-1);

    private RpcTarget(int targetCabinetId){
        if (targetCabinetId<0){
            this.rk = "all";
        }else{
            this.rk = String.valueOf(targetCabinetId);

        }
        this.ec = switch_ToPlcCommon;
    }


    private String rk;
    private String ec;



    public String getRoutingKey(){
        return this.rk;
    }

    public String getExchange(){
        return this.ec;
    }
}
