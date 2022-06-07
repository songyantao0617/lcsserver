package com.pxccn.PxcDali2.learn;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.InvokeParam;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class Learnmmm {


    @Autowired
    CabinetRequestService cabinetRequestService;


    public void readDescription() {
        var future = cabinetRequestService.writePropertyValueAsync(
                RpcTarget.CommonToAllCabinet,
                "station:|slot:/LightControlSystem/description",
                "来自服务器"+System.currentTimeMillis());

    }


    public void getCabinetTime(RpcTarget target, Consumer<String> success, Consumer<Throwable> exception) {
        var future = cabinetRequestService.invokeMethodAsync(
                RpcTarget.CommonToAllCabinet,
                "station:|slot:/LightControlSystem",
                "RPC_GetCurrentTime",
                InvokeParam.set(String.class, "Hello")
        );

        Futures.addCallback(future, new FutureCallback<Object>() {
            @Override
            public void onSuccess(@Nullable Object result) {
                success.accept((String) result);
            }

            @Override
            public void onFailure(Throwable t) {
                exception.accept(t);
            }
        }, MoreExecutors.directExecutor());
    }


    public void ssss() {


        getCabinetTime(RpcTarget.CommonToAllCabinet, (i) -> {

        }, (ex) -> {

        });
    }


}
