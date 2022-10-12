package com.pxccn.PxcDali2.server.service.rpc.impl;

import com.google.common.util.concurrent.*;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToPlcQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.message.ProtoToServerQueueMsg;
import com.pxccn.PxcDali2.MqSharePack.model.NiagaraOperateRequestModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.ActionWithFeedbackRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.NiagaraOperateRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toPlc.PingRequestWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.ResponseWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.NiagaraOperateRespWrapper;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.response.PingRespWrapper;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.common.LcsThreadFactory;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.BadMessageException;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.OperationFailure;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.InvokeParam;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import com.pxccn.PxcDali2.server.service.rpc.WritePropertyParameter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.amqp.core.AmqpMessageReturnedException;
import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CabinetRequestServiceImpl implements CabinetRequestService, InitializingBean {
    //    private ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final ConcurrentMap<UUID, Task> pending = new ConcurrentHashMap<>();
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    AsyncRabbitTemplate asyncRabbitTemplate;
    private ExecutorService executor;
    private ScheduledExecutorService timeoutExecutor;

    public static <T> FutureCallback on(Class<T> expectedType, Consumer<T> success, Consumer<Throwable> failure) {
        return new FutureCallback() {
            @Override
            public void onSuccess(Object result) {
                try {
                    checkExpectType(expectedType, result);
                    success.accept((T) result);
                } catch (Throwable e) {
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                failure.accept(t);
            }
        };
    }

    public static void checkExpectType(Class<?> expectType, Object testInstance) {
        if (!expectType.isInstance(testInstance)) {
            throw new BadMessageException("内部错误！ 预期得到类型‘" + expectType.getSimpleName() + "',但接收到类型’" + testInstance.getClass().getSimpleName() + "'");
        }
    }

    @PostConstruct
    public void init() {
        executor = LcsExecutors.newWorkStealingPool(40, getClass());
        timeoutExecutor = Executors.newScheduledThreadPool(2, LcsThreadFactory.forName("lcs-response-timeout"));
//        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
//            @Override
//            public void returnedMessage(ReturnedMessage returned) {
//                log.error("发送失败");
////                returned.getMessage().getMessageProperties().
//            }
//        });
    }

    @Deprecated
    public ResponseWrapper syncSend(RpcTarget target, ProtoToPlcQueueMsg request) throws InvalidProtocolBufferException, BadMessageException, OperationFailure {
        var reply = (byte[]) rabbitTemplate.convertSendAndReceive(target.getExchange(), target.getRoutingKey(), request.getData());
        ProtoToServerQueueMsg m = ProtoToServerQueueMsg.FromData(reply);
        checkExpectType(ResponseWrapper.class, m);
        LcsProtos.Response.Status status = ((ResponseWrapper) m).getStatus();
        switch (status.getNumber()) {
            case LcsProtos.Response.Status.SUCCESS_VALUE:
                return ((ResponseWrapper) m);
            case LcsProtos.Response.Status.FAILURE_VALUE:
            case LcsProtos.Response.Status.FATAL_VALUE:
                throw new OperationFailure(((ResponseWrapper) m).getExceptionMessage());
            default:
                throw new IllegalStateException("internal error");
        }
    }

    public ListenableFuture<ResponseWrapper> asyncSend(RpcTarget target, ProtoToPlcQueueMsg request) {
        var future = asyncRabbitTemplate.convertSendAndReceive(target.getExchange(), target.getRoutingKey(), request.getData());
        SettableFuture<ResponseWrapper> gFuture = SettableFuture.create();//转换为 guava
        future.addCallback(
                new ListenableFutureCallback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        try {
                            ProtoToServerQueueMsg m = ProtoToServerQueueMsg.FromData((byte[]) result);
                            checkExpectType(ResponseWrapper.class, m);
                            LcsProtos.Response.Status status = ((ResponseWrapper) m).getStatus();
                            switch (status.getNumber()) {
                                case LcsProtos.Response.Status.SUCCESS_VALUE:
                                    gFuture.set((ResponseWrapper) m);
                                    break;
                                case LcsProtos.Response.Status.FAILURE_VALUE:
                                case LcsProtos.Response.Status.FATAL_VALUE:
                                    throw new OperationFailure(((ResponseWrapper) m).getExceptionMessage());
                            }
                        } catch (Exception e) {
                            onFailure(e);
                        } finally {
                            future.cancel(false);
                        }
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        gFuture.setException(ex);
                        future.cancel(false);
                    }
                });

        return gFuture;
    }

    public void test2() {
        var future = this.asyncSend(RpcTarget.CommonToAllCabinet, new PingRequestWrapper(Util.NewCommonHeaderForClient(), 12, 34));
        Futures.addCallback(future, on(PingRespWrapper.class, (a) -> {
            log.info("AAAA");
        }, (ex) -> {
            log.error(ex.getMessage());
        }), executor);
    }

    public ListenableFuture<Object> invokeMethodAsync(RpcTarget target, UUID resource, String slotPath, String methodName, InvokeParam... params) {
        var model = NiagaraOperateRequestModel.INVOKE_METHOD(resource, slotPath, methodName);
        if (params != null) {
            Arrays.stream(params).forEach(p -> {
                model.getMethodParameter().add(p.getCls(), p.getValue());
            });
        }
        return this.invokeMethodAsync(target, model);
    }

    public ListenableFuture<Object> invokeNiagaraMethodAsync(RpcTarget target, String actionOrd, String value) {
        return this.invokeMethodAsync(target, "station:|slot:/LightControlSystem", "RPC_InvokeAction", InvokeParam.set(String.class, actionOrd), InvokeParam.set(String.class, value));
    }

    public ListenableFuture<Object> invokeMethodAsync(RpcTarget target, String bComponentOrd, String methodName, InvokeParam... params) {
        var model = NiagaraOperateRequestModel.INVOKE_METHOD(bComponentOrd, methodName);
        if (params != null) {
            Arrays.stream(params).forEach(p -> {
                model.getMethodParameter().add(p.getCls(), p.getValue());
            });
        }
        return this.invokeMethodAsync(target, model);
    }

    private ListenableFuture<Object> invokeMethodAsync(RpcTarget target, NiagaraOperateRequestModel model) {
        if (log.isTraceEnabled())
            log.trace("invokeMethodAsync: RpcTarget={},functionName={}", target.toFriendlyString(), model.getTargetValue());

        return Futures.transform(this.asyncSend(
                target
                , new NiagaraOperateRequestWrapper(Util.NewCommonHeaderForClient()
                        , Collections.singletonList(model)
                        , false
                )
        ), input -> {
            checkExpectType(NiagaraOperateRespWrapper.class, input);
            var a = (NiagaraOperateRespWrapper) input;
            if (a.getResponseList().size() != 1) {
                throw new IllegalStateException("Bad payload! internal error");
            }
            var b = a.getResponseList().get(0);
            if (b.isSuccess()) {
                if (log.isDebugEnabled())
                    log.debug("invokeMethodAsync success,function name : {}", model.getTargetValue());
                return b.getReturnValue();
            } else {
                throw new IllegalStateException(b.getExceptionReason());
            }
        }, executor);
    }

    public ListenableFuture<String> readPropertyValueAsync(RpcTarget target, UUID resourceUuid, String slotOrd) {
        return this.readPropertyValueAsync(target, NiagaraOperateRequestModel.READ_PROPERTY(resourceUuid, slotOrd));
    }

    public ListenableFuture<String> readPropertyValueAsync(RpcTarget target, String slotOrd) {
        return this.readPropertyValueAsync(target, NiagaraOperateRequestModel.READ_PROPERTY(slotOrd));
    }

    public ListenableFuture<Void> writePropertyValueAsync(RpcTarget target, String slotOrd, String newValue) {
        return this.writePropertyValueAsync(target, NiagaraOperateRequestModel.WRITE_PROPERTY(slotOrd, newValue));
    }

    public ListenableFuture<Void> writePropertyValueAsync(RpcTarget target, UUID resourceUuid, String slotOrd, String newValue) {
        return this.writePropertyValueAsync(target, NiagaraOperateRequestModel.WRITE_PROPERTY(resourceUuid, slotOrd, newValue));
    }

    private ListenableFuture<Void> writePropertyValueAsync(RpcTarget target, NiagaraOperateRequestModel request) {
        var responseFuture = this.asyncSend(target, new NiagaraOperateRequestWrapper(Util.NewCommonHeaderForClient(), Collections.singletonList(request), false));
        SettableFuture<Void> future = SettableFuture.create();
        Futures.addCallback(responseFuture, on(NiagaraOperateRespWrapper.class, (resp) -> {
            if (resp.getResponseList().size() != 1) {
                throw new IllegalStateException("Bad response from controller");
            }
            var m = resp.getResponseList().get(0);
            if (m.isSuccess()) {
                future.set(null);
            } else {
                throw new IllegalStateException(m.getExceptionReason());
            }
        }, future::setException), executor);
        return future;
    }


    public ListenableFuture<List<String>> batchWritePropertyAsync(RpcTarget target, List<WritePropertyParameter> parameters) {

        var responseFuture = this.asyncSend(target, new NiagaraOperateRequestWrapper(Util.NewCommonHeaderForClient(),
                parameters
                        .stream()
                        .map(p -> {
                            return NiagaraOperateRequestModel.WRITE_PROPERTY(p.getResourceUUID(), p.getSlot(), p.getNewValue());
                        }).collect(Collectors.toList()), false));
        SettableFuture<List<String>> future = SettableFuture.create();
        Futures.addCallback(responseFuture, on(NiagaraOperateRespWrapper.class, (resp) -> {
            var responseList = resp.getResponseList();
            if (responseList.size() != parameters.size()) {
                var msg = MessageFormatter.arrayFormat("Internal Error! writePropertyValueAsync target={}, expected {} response but {} received!", new Object[]{target, parameters.size(), responseList.size()}).getMessage();
                future.setException(new IllegalStateException(msg));
                return;
            }
            future.set(responseList.stream().map(r -> {
                if (r.isSuccess()) {
                    return "";
                } else {
                    return r.getExceptionReason();
                }
            }).collect(Collectors.toList()));

        }, future::setException), executor);
        return future;
    }


    private ListenableFuture<String> readPropertyValueAsync(RpcTarget target, NiagaraOperateRequestModel request) {
        var responseFuture = this.asyncSend(target, new NiagaraOperateRequestWrapper(Util.NewCommonHeaderForClient(), Collections.singletonList(request), false));
        SettableFuture<String> future = SettableFuture.create();
        Futures.addCallback(responseFuture, on(NiagaraOperateRespWrapper.class, (resp) -> {
            if (resp.getResponseList().size() != 1) {
                throw new IllegalStateException("返回执行结果的数量错误");
            }
            var m = resp.getResponseList().get(0);
            if (m.isSuccess()) {
                future.set(m.getTargetValue());
            } else {
                throw new IllegalStateException(m.getExceptionReason());
            }
        }, future::setException), executor);
        return future;
    }

    public void test() {

        var aa = invokeMethodAsync(RpcTarget.CommonToAllCabinet, "station:|slot:/LightControlSystem", "testRpc2");

        var a = this.writePropertyValueAsync(RpcTarget.CommonToAllCabinet, "station:|slot:/LightControlSystem/description", String.valueOf(System.currentTimeMillis()));
        Futures.addCallback(a, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
                log.info("WriteSuccess");
            }

            @Override
            public void onFailure(Throwable t) {
                log.error(t.getMessage());
            }
        }, MoreExecutors.directExecutor());
    }


    /*
    *
    *     public ListenableFuture<AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction> sendBroadcastCommand(int terminalIndex, Dali2LightCommandModel dali2LightCommandModel) {
        log.trace("sendBroadcastCommand({}): terminalIndex={},dali2LightCommandModel={}", this.Name.get(), terminalIndex, dali2LightCommandModel);
        SettableFuture<AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction> future = SettableFuture.create();
        var f = this.cabinetRequestService.asyncSendWithAsyncFeedback(
                RpcTarget.ToCabinet(this.CabinetId.get()),
                ActionWithFeedbackRequestWrapper.SendBroadcastLevelInstruction(
                        Util.NewCommonHeaderForClient(),
                        terminalIndex,
                        dali2LightCommandModel,
                        new Dt8CommandModel(Dt8CommandModel.Instructions.None, 0, 0)),
                (ResponseWrapper) -> {
                    log.trace("控制柜({})收到灯具广播命令->{}", Name.get(), dali2LightCommandModel);
                }, 20000);
        Futures.addCallback(f, new FutureCallback<AsyncActionFeedbackWrapper>() {
            @Override
            public void onSuccess(@Nullable AsyncActionFeedbackWrapper result) {
                if (result == null || result.getFeedback() == null) {
                    log.error("内部错误,未返回有效内容");
                    return;
                }
                if (result.getFeedback() instanceof AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction) {
                    var cnt = ((AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction) result.getFeedback()).getCountOfLights();
                    log.debug("控制柜({})执行广播命令成功，涉及数量：{}", Name.get(), cnt);
                    future.set((AsyncActionFeedbackWrapper.SendBroadcastLevelInstruction) result.getFeedback());
                } else {
                    future.setException(new IllegalStateException("控制器未返回有效数据"));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("执行命令失败：{}", t.getMessage());
                future.setException(t);
            }
        }, MoreExecutors.directExecutor());
        return future;
    }
    *
    * */
    public ListenableFuture<AsyncActionFeedbackWrapper> asyncSendWithAsyncFeedback(RpcTarget target,
                                                                                   ActionWithFeedbackRequestWrapper request,
                                                                                   @Nullable Consumer<ResponseWrapper> sendSuccess,
                                                                                   int timeout
    ) {
        log.trace("asyncSendWithAsyncFeedback: target={},request={},timeout={}", target.toFriendlyString(), request.getAction().getClass().getSimpleName(), timeout);
        if (timeout < 1000) {
            timeout = 30000;
        }
        SettableFuture<AsyncActionFeedbackWrapper> feedbackFuture = SettableFuture.create();
        var t = new Task();
        t.consumer = feedbackFuture;
        t.timeout = timeout;
        this.pending.put(request.getRequestId(), t);
        ScheduledFuture<?> scheduleFuture = this.timeoutExecutor.schedule(() -> {
            log.debug("Time out waiting for async result");
            if (this.pending.remove(request.getRequestId()) != null) {
                feedbackFuture.setException(new TimeoutException("Time out waiting for async result"));
            }
        }, timeout, TimeUnit.MILLISECONDS);
        t.schedule = scheduleFuture;
        var requestFuture = this.asyncSend(target, request);
        Futures.addCallback(
                Futures.withTimeout(requestFuture, timeout - 100, TimeUnit.MILLISECONDS, this.timeoutExecutor)
                , new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nullable ResponseWrapper result) {
                        log.debug("<{}> receive request <{}>", target.toFriendlyString(), request.getAction().getClass().getSimpleName());
                        if (sendSuccess != null) {
                            sendSuccess.accept(result);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (t instanceof AmqpMessageReturnedException) {
                            log.error("<{}>post request <{}> Message was reject! because cabinet is not online", target.toFriendlyString(), request.getAction().getClass().getSimpleName());
                        } else if (t instanceof AmqpReplyTimeoutException) {
                            log.error("<{}>post request <{}> timeout! didn't receive the resp in time", target.toFriendlyString(), request.getAction().getClass().getSimpleName());
                        } else {
                            log.error("<{}>post request <{}> failure :{}", target.toFriendlyString(), t.getMessage(), request.getAction().getClass().getSimpleName(), t);
                        }
                        scheduleFuture.cancel(true);
                        pending.remove(request.getRequestId());
                        feedbackFuture.setException(t);
                    }
                }, executor);


        return feedbackFuture;
    }

    @Override
    public void onReceiveFeedback(AsyncActionFeedbackWrapper feedbackWrapper) {
        try {
            UUID requestId = feedbackWrapper.getRequestId();
            var t = this.pending.remove(requestId);
            if (t != null) {
                if (t.schedule != null) {
                    t.schedule.cancel(true);
                }
                String error = feedbackWrapper.getExceptionMessage();
                if (error.isEmpty())
                    t.consumer.set(feedbackWrapper);
                else
                    t.consumer.setException(new IllegalStateException(error));
            } else {
                log.debug("feedback timeout or not belong to this server:{}", feedbackWrapper);
            }
        } catch (Throwable e) {
            log.error("Fatal Error", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        taskScheduler.afterPropertiesSet();
    }

    static class Task {
        public SettableFuture<AsyncActionFeedbackWrapper> consumer;
        public int timeout;
        public ScheduledFuture<?> schedule;
    }
}
