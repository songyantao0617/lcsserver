package com.pxccn.PxcDali2.server.mq.template;

import com.google.common.util.concurrent.ListenableFuture;
import com.pxccn.PxcDali2.MqSharePack.message.QueueMsg;

public interface QueueHandler<Request extends QueueMsg, Response extends QueueMsg> {

    ListenableFuture<Response> handle(Request request);

}