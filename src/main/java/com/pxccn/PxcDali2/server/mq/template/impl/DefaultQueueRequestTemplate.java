//package com.pxccn.PxcDali2.server.mq.template.impl;
//
//import com.google.common.util.concurrent.Futures;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.common.util.concurrent.SettableFuture;
//import com.pxccn.PxcDali2.server.mq.message.QueueMsg;
//import com.pxccn.PxcDali2.server.mq.message.QueueMsgMetadata;
//import com.pxccn.PxcDali2.server.mq.template.*;
//import com.pxccn.PxcDali2.server.util.LcsThreadFactory;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.LockSupport;
//import java.util.concurrent.locks.ReentrantLock;
//
//@Slf4j
//public class DefaultQueueRequestTemplate<Request extends QueueMsg, Response extends QueueMsg> extends AbstractQueueTemplate
//        implements QueueRequestTemplate<Request, Response> {
//
//    private final QueueAdmin queueAdmin;
//    private final QueueProducer<Request> requestTemplate;
//    private final QueueConsumer<Response> responseTemplate;
//    final ConcurrentHashMap<UUID, DefaultQueueRequestTemplate.ResponseMetaData<Response>> pendingRequests = new ConcurrentHashMap<>();
//    final boolean internalExecutor;
//    final ExecutorService executor;
//    final long maxRequestTimeoutNs;
//    final long maxPendingRequests;
//    final long pollInterval;
//    volatile boolean stopped = false;
//    long nextCleanupNs = 0L;
//    private final Lock cleanerLock = new ReentrantLock();
//
//    private MessagesStats messagesStats;
//
//    @Builder
//    public DefaultQueueRequestTemplate(QueueAdmin queueAdmin,
//                                       QueueProducer<Request> requestTemplate,
//                                       QueueConsumer<Response> responseTemplate,
//                                       long maxRequestTimeout,
//                                       long maxPendingRequests,
//                                       long pollInterval,
//                                       @Nullable ExecutorService executor) {
//        this.queueAdmin = queueAdmin;
//        this.requestTemplate = requestTemplate;
//        this.responseTemplate = responseTemplate;
//        this.maxRequestTimeoutNs = TimeUnit.MILLISECONDS.toNanos(maxRequestTimeout);
//        this.maxPendingRequests = maxPendingRequests;
//        this.pollInterval = pollInterval;
//        this.internalExecutor = (executor == null);
//        this.executor = internalExecutor ? createExecutor() : executor;
//    }
//
//    ExecutorService createExecutor() {
//        return Executors.newSingleThreadExecutor(LcsThreadFactory.forName("queue-request-template-" + responseTemplate.getTopic()));
//    }
//
//    @Override
//    public void init() {
//        queueAdmin.createTopicIfNotExists(responseTemplate.getTopic());
//        requestTemplate.init();
//        responseTemplate.subscribe();
//        executor.submit(this::mainLoop);
//    }
//
//    void mainLoop() {
//        while (!stopped) {
//            myStopWatch sw = myStopWatch.startNew();
//            try {
//                fetchAndProcessResponses();
//            } catch (Throwable e) {
//                long sleepNanos = TimeUnit.MILLISECONDS.toNanos(this.pollInterval) - sw.stopAndGetTotalTimeNanos();
//                log.warn("Failed to obtain and process responses from queue. Going to sleep " + sleepNanos + "ns", e);
//                sleep(sleepNanos);
//            }
//        }
//    }
//
//    void fetchAndProcessResponses() {
//        final long pendingRequestsCount = pendingRequests.mappingCount();
//        log.trace("Starting template pool topic {}, for pendingRequests {}", responseTemplate.getTopic(), pendingRequestsCount);
//        List<Response> responses = doPoll(); //poll js responses
//        log.trace("Completed template poll topic {}, for pendingRequests [{}], received [{}] responses", responseTemplate.getTopic(), pendingRequestsCount, responses.size());
//        responses.forEach(this::processResponse); //this can take a long time
//        responseTemplate.commit();
//        tryCleanStaleRequests();
//    }
//
//    private boolean tryCleanStaleRequests() {
//        if (!cleanerLock.tryLock()) {
//            return false;
//        }
//        try {
//            log.trace("tryCleanStaleRequest...");
//            final long currentNs = getCurrentClockNs();
//            if (nextCleanupNs < currentNs) {
//                pendingRequests.forEach((key, value) -> {
//                    if (value.expTime < currentNs) {
//                        ResponseMetaData<Response> staleRequest = pendingRequests.remove(key);
//                        if (staleRequest != null) {
//                            setTimeoutException(key, staleRequest, currentNs);
//                        }
//                    }
//                });
//                setupNextCleanup();
//            }
//        } finally {
//            cleanerLock.unlock();
//        }
//        return true;
//    }
//
//    void setupNextCleanup() {
//        nextCleanupNs = getCurrentClockNs() + maxRequestTimeoutNs;
//        log.trace("setupNextCleanup {}", nextCleanupNs);
//    }
//
//    List<Response> doPoll() {
//        return responseTemplate.poll(pollInterval);
//    }
//
//    void sleep(long nanos) {
//        LockSupport.parkNanos(nanos);
//    }
//
//    void setTimeoutException(UUID key, ResponseMetaData<Response> staleRequest, long currentNs) {
//        if (currentNs >= staleRequest.getSubmitTime() + staleRequest.getTimeout()) {
//            log.warn("Request timeout detected, currentNs [{}], {}, key [{}]", currentNs, staleRequest, key);
//        } else {
//            log.error("Request timeout detected, currentNs [{}], {}, key [{}]", currentNs, staleRequest, key);
//        }
//        staleRequest.future.setException(new TimeoutException());
//    }
//
//    void processResponse(Response response) {
//        byte[] requestIdHeader = response.getHeaders().get(REQUEST_ID_HEADER);
//        UUID requestId;
//        if (requestIdHeader == null) {
//            log.error("[{}] Missing requestId in header and body", response);
//        } else {
//            requestId = bytesToUuid(requestIdHeader);
//            log.trace("[{}] Response received: {}", requestId, String.valueOf(response).replace("\n", " ")); //TODO remove overhead
//            ResponseMetaData<Response> expectedResponse = pendingRequests.remove(requestId);
//            if (expectedResponse == null) {
//                log.warn("[{}] Invalid or stale request, response: {}", requestId, String.valueOf(response).replace("\n", " "));
//            } else {
//                expectedResponse.future.set(response);
//            }
//        }
//    }
//
//    @Override
//    public void stop() {
//        stopped = true;
//
//        if (responseTemplate != null) {
//            responseTemplate.unsubscribe();
//        }
//
//        if (requestTemplate != null) {
//            requestTemplate.stop();
//        }
//
//        if (internalExecutor) {
//            executor.shutdownNow();
//        }
//    }
//
//    @Override
//    public void setMessagesStats(MessagesStats messagesStats) {
//        this.messagesStats = messagesStats;
//    }
//
//    @Override
//    public ListenableFuture<Response> send(Request request) {
//        return send(request, this.maxRequestTimeoutNs);
//    }
//
//    @Override
//    public ListenableFuture<Response> send(Request request, long requestTimeoutNs) {
//        if (pendingRequests.mappingCount() >= maxPendingRequests) {
//            log.warn("Pending request map is full [{}]! Consider to increase maxPendingRequests or increase processing performance. Request is {}", maxPendingRequests, request);
//            return Futures.immediateFailedFuture(new RuntimeException("Pending request map is full!"));
//        }
//        UUID requestId = UUID.randomUUID();
//        request.getHeaders().put(REQUEST_ID_HEADER, uuidToBytes(requestId));
//        request.getHeaders().put(RESPONSE_TOPIC_HEADER, stringToBytes(responseTemplate.getTopic()));
//        request.getHeaders().put(REQUEST_TIME, longToBytes(getCurrentTimeMs()));
//        long currentClockNs = getCurrentClockNs();
//        SettableFuture<Response> future = SettableFuture.create();
//        ResponseMetaData<Response> responseMetaData = new ResponseMetaData<>(currentClockNs + requestTimeoutNs, future, currentClockNs, requestTimeoutNs);
//        log.trace("pending {}", responseMetaData);
//        if (pendingRequests.putIfAbsent(requestId, responseMetaData) != null) {
//            log.warn("Pending request already exists [{}]!", maxPendingRequests);
//            return Futures.immediateFailedFuture(new RuntimeException("Pending request already exists !" + requestId));
//        }
//        sendToRequestTemplate(request, requestId, future, responseMetaData);
//        return future;
//    }
//
//    /**
//     * MONOTONIC clock instead jumping wall clock.
//     * Wrapped into the method for the test purposes to travel through the time
//     * */
//    long getCurrentClockNs() {
//        return System.nanoTime();
//    }
//
//    /**
//     * Wall clock to send timestamp to an external service
//     * */
//    long getCurrentTimeMs() {
//        return System.currentTimeMillis();
//    }
//
//    void sendToRequestTemplate(Request request, UUID requestId, SettableFuture<Response> future, ResponseMetaData<Response> responseMetaData) {
//        log.trace("[{}] Sending request, key [{}], expTime [{}], request {}", requestId, request.getKey(), responseMetaData.expTime, request);
//        if (messagesStats != null) {
//            messagesStats.incrementTotal();
//        }
//        requestTemplate.send(TopicPartitionInfo.builder().topic(requestTemplate.getDefaultTopic()).build(), request, new QueueCallback() {
//            @Override
//            public void onSuccess(QueueMsgMetadata metadata) {
//                if (messagesStats != null) {
//                    messagesStats.incrementSuccessful();
//                }
//                log.trace("[{}] Request sent: {}, request {}", requestId, metadata, request);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                if (messagesStats != null) {
//                    messagesStats.incrementFailed();
//                }
//                pendingRequests.remove(requestId);
//                future.setException(t);
//            }
//        });
//    }
//
//    @Getter
//    static class ResponseMetaData<T> {
//        private final long submitTime;
//        private final long timeout;
//        private final long expTime;
//        private final SettableFuture<T> future;
//
//        ResponseMetaData(long ts, SettableFuture<T> future, long submitTime, long timeout) {
//            this.submitTime = submitTime;
//            this.timeout = timeout;
//            this.expTime = ts;
//            this.future = future;
//        }
//
//        @Override
//        public String toString() {
//            return "ResponseMetaData{" +
//                    "submitTime=" + submitTime +
//                    ", calculatedExpTime=" + (submitTime + timeout) +
//                    ", deltaMs=" + (expTime - submitTime) +
//                    ", expTime=" + expTime +
//                    ", future=" + future +
//                    '}';
//        }
//    }
//
//}
