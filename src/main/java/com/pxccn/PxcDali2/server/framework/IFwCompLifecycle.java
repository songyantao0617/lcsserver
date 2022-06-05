package com.pxccn.PxcDali2.server.framework;

public interface IFwCompLifecycle {

    void start();

    void started();

    void stop();

    void stopped();

    default void descendantsStopped(){}
}
