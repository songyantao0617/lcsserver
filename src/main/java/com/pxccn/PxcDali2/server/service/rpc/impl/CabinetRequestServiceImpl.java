package com.pxccn.PxcDali2.server.service.rpc.impl;

import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.NiagaraOperateRequestModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.NiagaraOperateRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.PingRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.NiagaraOperateRespWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.PingRespWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.BadMessageException;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.OperationFailure;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.InvokeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class CabinetRequestServiceImpl implements CabinetRequestService {
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
        this.asyncSend(target, new PingRequestWrapper(Util.NewCommonHeaderForClient(), 12, 34), new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(ResponseWrapper result) {
                try {
                    checkRespType(PingRespWrapper.class, result);
                    success.accept((PingRespWrapper) result);

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

    public void invokeMethodAsync(RpcTarget target, String bComponentOrd, String methodName, List<InvokeParam> params, Consumer<Object> success, Consumer<Throwable> failure) {
        var model = NiagaraOperateRequestModel.INVOKE_METHOD(bComponentOrd, methodName);
        if (params != null) {
            params.forEach(p -> {
                model.getMethodParameter().add(p.getCls(), p.getValue());
            });
        }
        if (log.isTraceEnabled())
            log.trace("发起异步函数调用,{},函数名：{}", target.toFriendlyString(), methodName);

        this.asyncSend(target
                , new NiagaraOperateRequestWrapper(Util.NewCommonHeaderForClient(), Collections.singletonList(model), false)
                , new ListenableFutureCallback<>() {
                    @Override
                    public void onSuccess(ResponseWrapper result) {
                        try {
                            checkRespType(NiagaraOperateRespWrapper.class, result);
                            var a = (NiagaraOperateRespWrapper) result;
                            if (a.getResponseList().size() != 1) {
                                throw new IllegalStateException("报文不正确");
                            }
                            var b = a.getResponseList().get(0);
                            if (b.isSuccess()) {
                                if (log.isTraceEnabled())
                                    log.trace("异步函数调用成功,函数名：{}", methodName);
                                success.accept(b.getReturnValue());
                            } else {
                                throw new IllegalStateException(b.getExceptionReason());
                            }

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

    public void test() {

        this.invokeMethodAsync(
                RpcTarget.CommonToAllCabinet,
                "station:|slot:/LightControlSystem",
                "testRpc",
                Arrays.asList(
                        new InvokeParam<>(Integer.TYPE, 123),
                        new InvokeParam<>(String.class, "dd")),
                (rtn) -> {
                    log.info("调,{}", rtn);
                }, (ex) -> {
                });
//        var model = NiagaraOperateRequestModel.READ_PROPERTY("station:|slot:/LightControlSystem/lightManager/DALI2_BUS_03/light1/randomAddress");
//        var model = NiagaraOperateRequestModel.READ_PROPERTY(UUID.fromString("6508d494-738c-48c0-bbef-584efa91c893"),"randomAddress");
//        var model = NiagaraOperateRequestModel.WRITE_PROPERTY("station:|slot:/LightControlSystem/description",String.valueOf(System.currentTimeMillis()));
//        var model = NiagaraOperateRequestModel.INVOKE_METHOD("station:|slot:/LightControlSystem","testRpc");
//        var model = NiagaraOperateRequestModel.INVOKE_METHOD("station:|slot:/LightControlSystem", "testRpc");
//        model.getMethodParameter().add(Integer.TYPE, 159);
//        model.getMethodParameter().add(String.class, "FUCK");
//        this.asyncSend(RpcTarget.CommonToAllCabinet
//                , new NiagaraOperateRequestWrapper(Util.NewCommonHeaderForClient(), Collections.singletonList(model), false)
//                , new ListenableFutureCallback<>() {
//                    @Override
//                    public void onSuccess(ResponseWrapper result) {
//                        try {
//                            checkRespType(NiagaraOperateRespWrapper.class, result);
//                            var a = (NiagaraOperateRespWrapper) result;
//                            a.getResponseList().forEach(i -> {
//                                log.info("{}--{}--{}--{}", i.isSuccess(), i.getExceptionReason(), i.getTargetValue(), i.getReturnValue());
//                            });
//
////                            success.accept((NiagaraOperateRespWrapper) result);
//
//                        } catch (Exception e) {
//                            onFailure(e);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Throwable ex) {
//                        log.error(ex.getMessage());
//
//                    }
//                });
    }


    private void checkRespType(Class<?> expectType, Object testInstance) throws BadMessageException {
        if (!expectType.isInstance(testInstance)) {
            throw new BadMessageException("内部错误！ 预期得到类型‘" + expectType.getSimpleName() + "',但接收到类型’" + testInstance.getClass().getSimpleName() + "'");
        }
    }


}
