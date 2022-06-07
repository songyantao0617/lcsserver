package com.pxccn.PxcDali2.server.service;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.InvokeParam;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PathService implements SmartLifecycle {
    @Autowired
    CabinetRequestService cabinetRequestService;

    public void sss(){
        var start =  System.currentTimeMillis();
        var a =cabinetRequestService.invokeMethodAsync(RpcTarget.CommonToAllCabinet,"station:|slot:/LightControlSystem","RPC_QueryParentOrd", InvokeParam.set(String.class,"station:|slot:/LightControlSystem/lightManager"));
        try{
        a.get();
        }catch (Exception ignore){

        }
        var sc = System.currentTimeMillis()-start;
        log.info("sc");
    }


    @Override
    public void start() {
        this.sss();
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
