package com.pxccn.PxcDali2.server.service.opcua.type.enums;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.TypeDefinitionId;
import com.prosysopc.ua.nodes.UaObjectType;
import com.prosysopc.ua.nodes.UaProperty;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.server.service.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_AlarmConditionTypeNode;
@TypeDefinitionId(nsu = LcsNodeManager.Namespace, i = LCS_AlarmLightErrorTypeNode.TYPE_ID)
public class LCS_AlarmLightErrorTypeNode extends LCS_AlarmConditionTypeNode {
    public static final String TYPE_NAME = LCS_AlarmLightErrorTypeNode.class.getSimpleName();
    public static final int TYPE_ID = 9020;
    private final LcsNodeManager NodeManager;

    protected LCS_AlarmLightErrorTypeNode(NodeManagerUaNode nodeManager, NodeId nodeId, QualifiedName browseName, LocalizedText displayName) {
        super(nodeManager, nodeId, browseName, displayName);
        this.NodeManager = (LcsNodeManager) nodeManager;
    }

    public static NodeId initType(NodeManagerUaNode manager) throws StatusException {
        int ns = manager.getNamespaceIndex();
        NodeId thisTypeNodeId = new NodeId(ns, TYPE_ID);
        UaObjectType thisType = new UaObjectTypeNode(manager, thisTypeNodeId, new QualifiedName(ns, TYPE_NAME), UaHelperUtil.getLocalizedText(TYPE_NAME + UaHelperUtil.lexiconSuffixDisplayName, TYPE_NAME));
        manager.getServer().getNodeManagerRoot().getType(new NodeId(ns, LCS_AlarmConditionTypeNode.TYPE_ID)).addSubType(thisType);
        UaHelperUtil.UaDeclare.initModel(manager, thisType, props.values());
        manager.getServer().registerClass(LCS_AlarmLightErrorTypeNode.class, thisTypeNodeId);
        return thisTypeNodeId;
    }

    public static LCS_AlarmLightErrorTypeNode createInstance(NodeManagerUaNode manager, String NodeId, String Name) {
        int ns = manager.getNamespaceIndex();
        return manager.createInstance(LCS_AlarmLightErrorTypeNode.class, new NodeId(ns, NodeId), new QualifiedName(ns, Name), new LocalizedText(Name));
    }

    public void afterCreate() {
        super.afterCreate();
        try {
            this.getEventTypeNode().setValue(new NodeId(this.getNodeManager().getNamespaceIndex(), LCS_AlarmLightErrorTypeNode.TYPE_ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UaProperty getPropNode(props prop) {
        UaProperty ret;
        ret = getProperty(new QualifiedName(getNodeManager().getNamespaceIndex(), prop.name()));
        return ret;
    }

    public void setPropValue(props prop, Object Value) {
        UaProperty variable = getPropNode(prop);
        if (variable != null) {
            try {
                if (!variable.getValue().getValue().equals(Value)) {
                    variable.setValue(Value);
                }
            } catch (StatusException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void activateAlarm(String lightName, String lightDescription, String faultReason, int severity) {
        this.setPropValue(props.lightName, lightName);
        this.setPropValue(props.lightDescription, lightDescription);
        this.setPropValue(props.faultReason, faultReason);
//            this.setMessage(new LocalizedText();
        this.setSeverity(severity);
        super.activateAlarm(-1, "light<" + lightName + "> " + faultReason);
    }


    private enum props implements UaHelperUtil.UaPropDeclare {
        lightName(Identifiers.String),
        lightDescription(Identifiers.String),
        faultReason(Identifiers.String);

        private final NodeId dataType;

        props(NodeId dataType) {
            this.dataType = dataType;
        }

        public int getId() {
            return TYPE_ID + this.ordinal() + 1;
        }

        @Override
        public NodeId getDataType() {
            return this.dataType;
        }
    }
}
