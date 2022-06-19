package com.pxccn.PxcDali2.server.service.rpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionWithFeedbackRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.BadMessageException;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.OperationFailure;

import java.util.UUID;
import java.util.function.Consumer;

public interface CabinetRequestService {

    /**
     * 同步发送命令
     *
     * @param target
     * @param request
     * @return
     * @throws InvalidProtocolBufferException
     * @throws BadMessageException
     * @throws OperationFailure
     */
    ResponseWrapper syncSend(RpcTarget target, ProtoToPlcQueueMsg request) throws InvalidProtocolBufferException, BadMessageException, OperationFailure;

    /**
     * 异步发送命令
     *
     * @param target
     * @param request
     * @return
     */
    ListenableFuture<ResponseWrapper> asyncSend(RpcTarget target, ProtoToPlcQueueMsg request);

    /**
     * 异步调用控制器全局函数
     *
     * @param target
     * @param bComponentOrd
     * @param methodName
     * @param params
     */

    ListenableFuture<Object> invokeMethodAsync(RpcTarget target, String bComponentOrd, String methodName, InvokeParam... params);

    /**
     * 异步读取 常规资源的 Property
     *
     * @param target
     * @param slotOrd
     * @return
     */
    ListenableFuture<String> readPropertyValueAsync(RpcTarget target, String slotOrd);

    /**
     * 异步读取 UUID资源的 Property
     *
     * @param target
     * @param resourceUuid
     * @param slotOrd
     * @return
     */
    ListenableFuture<String> readPropertyValueAsync(RpcTarget target, UUID resourceUuid, String slotOrd);

    /**
     * 异步写入 常规资源的 Property
     *
     * @param target
     * @param slotOrd
     * @param newValue
     * @return
     */
    ListenableFuture<Void> writePropertyValueAsync(RpcTarget target, String slotOrd, String newValue);

    /**
     * 异步写入 UUID资源的 Property
     *
     * @param target
     * @param resourceUuid
     * @param slotOrd
     * @param newValue
     * @return
     */
    ListenableFuture<Void> writePropertyValueAsync(RpcTarget target, UUID resourceUuid, String slotOrd, String newValue);


    ListenableFuture<AsyncActionFeedbackWrapper> asyncSendWithAsyncFeedback(RpcTarget target,
                                                                            ActionWithFeedbackRequestWrapper request,
                                                                            Consumer<ResponseWrapper> sendSuccess, int timeout
    );
}
