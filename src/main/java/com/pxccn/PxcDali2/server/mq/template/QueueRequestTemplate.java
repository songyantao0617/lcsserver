package com.pxccn.PxcDali2.server.mq.template;

import com.google.common.util.concurrent.ListenableFuture;
import com.pxccn.PxcDali2.MqSharePack.message.QueueMsg;

public interface QueueRequestTemplate<Request extends QueueMsg, Response extends QueueMsg> {

    void init();

    ListenableFuture<Response> send(Request request);

    ListenableFuture<Response> send(Request request, long timeoutNs);

    void stop();

    void setMessagesStats(MessagesStats messagesStats);
}