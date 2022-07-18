package com.pxccn.PxcDali2.server.service.opcua.type;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.TypeDefinitionId;
import com.prosysopc.ua.nodes.UaObjectType;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.types.opcua.server.AlarmConditionTypeNode;
import com.pxccn.PxcDali2.server.service.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import lombok.extern.slf4j.Slf4j;

@TypeDefinitionId(nsu = LcsNodeManager.Namespace, i = LCS_AlarmConditionTypeNode.TYPE_ID)
@Slf4j
public class LCS_AlarmConditionTypeNode extends AlarmConditionTypeNode {
    public static final String TYPE_NAME = LCS_AlarmConditionTypeNode.class.getSimpleName();
    public static final int TYPE_ID = 9001;
    private final LcsNodeManager NodeManager;

    protected LCS_AlarmConditionTypeNode(NodeManagerUaNode nodeManager, NodeId nodeId, QualifiedName browseName, LocalizedText displayName) {
        super(nodeManager, nodeId, browseName, displayName);
        NodeManager = (LcsNodeManager) nodeManager;
    }

    public static NodeId initType(NodeManagerUaNode manager) throws StatusException {
        int ns = manager.getNamespaceIndex();
        NodeId thisTypeNodeId = new NodeId(ns, TYPE_ID);
        UaObjectType thisType = new UaObjectTypeNode(manager, thisTypeNodeId, new QualifiedName(ns, TYPE_NAME), UaHelperUtil.getLocalizedText(TYPE_NAME + UaHelperUtil.lexiconSuffixDisplayName, TYPE_NAME));
        manager.getServer().getNodeManagerRoot().getType(Identifiers.AlarmConditionType).addSubType(thisType);
        manager.getServer().registerClass(LCS_AlarmConditionTypeNode.class, thisTypeNodeId);
        return thisTypeNodeId;
    }
    public static LCS_AlarmConditionTypeNode createInstance(NodeManagerUaNode manager, String NodeId, String Name) {
        int ns = manager.getNamespaceIndex();
        return manager.createInstance(LCS_AlarmConditionTypeNode.class, new NodeId(ns, NodeId), new QualifiedName(ns, Name), new LocalizedText(Name));
    }
    public void afterCreate() {
        super.afterCreate();
        try {
            this.getEventTypeNode().setValue(Identifiers.AlarmConditionType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void triggerAlarm(DateTime activeTime) {
        ByteString myEventId = this.NodeManager.getNextUserEventId();
//        try {
//            NodeManager.fillBaseEventInfo(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        triggerEvent(DateTime.currentTime(), activeTime, myEventId);
    }
    public void refreshAlarm() {
        if (this.isActive()) {
            triggerEvent(this.getTime(), this.getReceiveTime(), this.getEventId());
        }
    }
    public void activateAlarm(int severity, String Message) {
        if(getMessage() ==null){
            setMessage(new LocalizedText(""));
        }
        if(!isEnabled()){
            return;
        }
        if (!isActive() || (this.getSeverity().getValue() != severity || !getMessage().getText().equals(Message))) {

            //(!isActive())

            if (severity > -1) {
                setSeverity(severity);
            }

            if (Message != null) {
                setMessage(new LocalizedText(Message));
            }
            setActive(true);
            setRetain(true);
            setAcked(false);
            triggerAlarm(DateTime.currentTime());
        }
    }
    public void inactivateAlarm(boolean retainOff) {
        if (isEnabled() && isActive()) {
            setActive(false);
            if (retainOff) {
                setRetain(false);
            } else {
//                setRetain(!isAcked() && !isConfirmed());
                setRetain(!isAcked());
            }
            triggerAlarm(DateTime.currentTime());
        }
    }

    @Override
    protected void onEnable(ServiceContext serviceContext) throws StatusException {
        super.onEnable(serviceContext);
//        if (onEnableListener != null) {
//            onEnableListener.run();
//        }
    }

    @Override
    protected void onDisable(ServiceContext var1) throws StatusException {
        setActive(false);
        setRetain(false);
        super.onDisable(var1);
//        if (onDisableListener != null) {
//            onDisableListener.run();
//        }
    }
    @Override
    protected void onAddComment(ServiceContext serviceContext, ByteString bytes, LocalizedText localizedText) throws StatusException {
        super.onAddComment(serviceContext, bytes, localizedText);
    }

    @Override
    protected void onAcknowledge(ServiceContext serviceContext, ByteString bytes, LocalizedText localizedText) throws StatusException {
        super.onAcknowledge(serviceContext, bytes, localizedText);
        if (!this.isActive()) {
            this.setRetain(false);
        }
    }

    @Override
    @Deprecated
    protected void onConfirm(ServiceContext serviceContext, ByteString bytes, LocalizedText localizedText) throws StatusException {
        super.onConfirm(serviceContext, bytes, localizedText);
        if (!this.isActive()) {
            this.setRetain(false);
        }
    }
}
