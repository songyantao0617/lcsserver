package com.pxccn.PxcDali2.server.service.rpc;

import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.PingRespWrapper;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.function.Consumer;

public interface CabinetRequestService {
    void sendPing(RpcTarget target, Consumer<PingRespWrapper> success, Consumer<Throwable> failure);

    void asyncSend(RpcTarget target, ProtoToPlcQueueMsg request, ListenableFutureCallback<ResponseWrapper> callback);

    void invokeMethodAsync(RpcTarget target, String bComponentOrd, String methodName, List<InvokeParam> params, Consumer<Object> success, Consumer<Throwable> failure);
}
