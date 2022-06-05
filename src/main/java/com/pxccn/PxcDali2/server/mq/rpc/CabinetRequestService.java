package com.pxccn.PxcDali2.server.mq.rpc;

import com.google.common.util.concurrent.Futures;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.PingRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.PingRespWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.BadMessageException;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.OperationFailure;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.Future;
import java.util.function.Consumer;

@Service
public class CabinetRequestService implements SmartLifecycle {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AsyncRabbitTemplate asyncRabbitTemplate;


    public void asyncSend(RpcTarget target, ProtoToPlcQueueMsg request, ListenableFutureCallback<ResponseWrapper> callback) {
        ListenableFuture<Object> future = asyncRabbitTemplate.convertSendAndReceive(target.getExchange(), target.getRoutingKey(), request.getData());
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(Object result) {
                try {
                    ProtoToServerQueueMsg m = ProtoToServerQueueMsg.FromData((byte[]) result);
                    checkRespType(ResponseWrapper.class, m);

                    LcsProtos.Response.Status status = ((ResponseWrapper) m).getStatus();
                    switch (status.getNumber()) {
                        case LcsProtos.Response.Status.SUCCESS_VALUE:
                            callback.onSuccess((ResponseWrapper) m);
                            break;
                        case LcsProtos.Response.Status.FAILURE_VALUE:
                        case LcsProtos.Response.Status.FATAL_VALUE:
                            throw new OperationFailure(((ResponseWrapper) m).getExceptionMessage());
                    }


                } catch (Exception e) {
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                callback.onFailure(ex);
            }
        });
    }

    public void sendPing(RpcTarget target, Consumer<PingRespWrapper> success, Consumer<Throwable> failure) {
        this.asyncSend(target, new PingRequestWrapper(12, 34), new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(ResponseWrapper result) {
                try {
                    var response = result.getResponse();
                    checkRespType(PingRespWrapper.class, response);
                    success.accept((PingRespWrapper) response);

                } catch (Exception e) {
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable ex) {
                failure.accept(ex);
            }
        });
    }

    private void checkRespType(Class<?> expectType, Object testInstance) throws BadMessageException {
        if (!expectType.isInstance(testInstance)) {
            throw new BadMessageException("内部错误！ 预期得到类型‘" + expectType.getSimpleName() + "',但接收到类型’" + testInstance.getClass().getSimpleName() + "'");
        }
    }


    public Future<Object> sendWithFixedReplay(Object message) {
        ListenableFuture<Object> future = asyncRabbitTemplate.convertSendAndReceive("RPC_TEST", message);
        future.addCallback(new ListenableFutureCallback<Object>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("onFailure ---");
            }

            @Override
            public void onSuccess(Object result) {
                System.out.println("Result ---" + result);
            }
        });
        return future;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
