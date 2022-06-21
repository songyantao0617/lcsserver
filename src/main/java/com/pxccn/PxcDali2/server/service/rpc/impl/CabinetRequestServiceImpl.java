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
import com.pxccn.PxcDali2.common.LcsExecutors;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.common.LcsThreadFactory;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.BadMessageException;
import com.pxccn.PxcDali2.server.mq.rpc.exceptions.OperationFailure;
import com.pxccn.PxcDali2.server.service.rpc.CabinetRequestService;
import com.pxccn.PxcDali2.server.service.rpc.InvokeParam;
import com.pxccn.PxcDali2.server.service.rpc.RpcTarget;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.amqp.core.AmqpMessageReturnedException;
import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static com.pxccn.PxcDali2.server.mq.config.MqConfigure.switch_broadcastToPlc;

@Service
@Slf4j
public class CabinetRequestServiceImpl implements CabinetRequestService, InitializingBean {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AsyncRabbitTemplate asyncRabbitTemplate;

    private ExecutorService executor;
    private ScheduledExecutorService timeoutExecutor;

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
                        }
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        gFuture.setException(ex);
                    }
                });
        return gFuture;
    }

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

    public void test2() {
        var future = this.asyncSend(RpcTarget.CommonToAllCabinet, new PingRequestWrapper(Util.NewCommonHeaderForClient(), 12, 34));
        Futures.addCallback(future, on(PingRespWrapper.class, (a) -> {
            log.info("AAAA");
        }, (ex) -> {
            log.error(ex.getMessage());
        }), executor);
    }

    public ListenableFuture<Object> invokeMethodAsync(RpcTarget target, String bComponentOrd, String methodName, InvokeParam... params) {
        var model = NiagaraOperateRequestModel.INVOKE_METHOD(bComponentOrd, methodName);
        if (params != null) {
            Arrays.stream(params).forEach(p -> {
                model.getMethodParameter().add(p.getCls(), p.getValue());
            });
        }
        if (log.isTraceEnabled())
            log.trace("发起异步函数调用,{},函数名：{}", target.toFriendlyString(), methodName);

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
                throw new IllegalStateException("报文不正确");
            }
            var b = a.getResponseList().get(0);
            if (b.isSuccess()) {
                if (log.isTraceEnabled())
                    log.trace("异步函数调用成功,函数名：{}", methodName);
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
                throw new IllegalStateException("返回执行结果的数量错误");
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


    public static void checkExpectType(Class<?> expectType, Object testInstance) {
        if (!expectType.isInstance(testInstance)) {
            throw new BadMessageException("内部错误！ 预期得到类型‘" + expectType.getSimpleName() + "',但接收到类型’" + testInstance.getClass().getSimpleName() + "'");
        }
    }

    public ListenableFuture<AsyncActionFeedbackWrapper> asyncSendWithAsyncFeedback(RpcTarget target,
                                                                                   ActionWithFeedbackRequestWrapper request,
                                                                                   @Nullable Consumer<ResponseWrapper> sendSuccess,
                                                                                   int timeout
    ) {
        if (timeout < 1000) {
            timeout = 300000;
        }
        SettableFuture<AsyncActionFeedbackWrapper> feedbackFuture = SettableFuture.create();
        var t = new Task();
        t.consumer = feedbackFuture;
        t.timeout = timeout;
        this.pending.put(request.getRequestId(), t);
        ScheduledFuture<?> scheduleFuture = this.timeoutExecutor.schedule(() -> {
            log.debug("异步反馈超时触发");
            var f = this.pending.remove(request.getRequestId());
            if (f != null) {
                feedbackFuture.setException(new TimeoutException("异步反馈超时！"));
            }
        }, timeout, TimeUnit.MILLISECONDS);
        t.schedule = scheduleFuture;
        var requestFuture = this.asyncSend(target, request);
        Futures.addCallback(
                Futures.withTimeout(requestFuture, timeout - 100, TimeUnit.MILLISECONDS, this.timeoutExecutor)
                , new FutureCallback<>() {
                    @Override
                    public void onSuccess(@Nullable ResponseWrapper result) {
                        log.debug("<{}> 收到请求", target.toFriendlyString());
                        if (sendSuccess != null) {
                            sendSuccess.accept(result);
                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        if (t instanceof AmqpMessageReturnedException) {
                            log.error("<{}>投递请求消息被打回，因为目标控制器不在线",target.toFriendlyString());
                        } else if (t instanceof AmqpReplyTimeoutException) {
                            log.error("<{}>超时！未及时收到控制器返回！", target.toFriendlyString());
                        }else{
                            log.error("<{}>投递请求失败:{}",target.toFriendlyString(), t.getMessage());
                        }
                        scheduleFuture.cancel(true);
                        pending.remove(request.getRequestId());
                        feedbackFuture.setException(t);
                    }
                }, executor);
        return feedbackFuture;
    }


    //////////////////////////
//    private ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private final ConcurrentMap<UUID, Task> pending = new ConcurrentHashMap<>();


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
                log.warn("反馈信息迟到:{}", feedbackWrapper);
            }
        } catch (Throwable e) {
            log.error("严重错误，无法处理!", e);
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
