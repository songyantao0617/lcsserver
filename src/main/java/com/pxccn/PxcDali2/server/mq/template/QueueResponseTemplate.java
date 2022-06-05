package com.pxccn.PxcDali2.server.mq.template;

import com.pxccn.PxcDali2.MqSharePack.message.QueueMsg;

public interface QueueResponseTemplate<Request extends QueueMsg, Response extends QueueMsg> {

    void init(QueueHandler<Request, Response> handler);

    void stop();
}