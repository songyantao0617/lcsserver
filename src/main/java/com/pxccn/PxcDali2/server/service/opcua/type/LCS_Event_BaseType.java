package com.pxccn.PxcDali2.server.service.opcua.type;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.TypeDefinitionId;
import com.prosysopc.ua.nodes.UaObjectType;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.types.opcua.server.BaseEventTypeNode;
import com.pxccn.PxcDali2.server.service.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import lombok.extern.slf4j.Slf4j;

//30000~30099

/**
 * LCS_Event_BaseType
 * 事件基类
 */
@TypeDefinitionId(nsu = LcsNodeManager.Namespace, i = LCS_Event_BaseType.TYPE_ID)
@Slf4j
public class LCS_Event_BaseType extends BaseEventTypeNode {
    public static final int TYPE_ID = 30000;
    private static final String TYPE_NAME = LCS_Event_BaseType.class.getSimpleName();
    public final LcsNodeManager NodeManager;

    public LCS_Event_BaseType(NodeManagerUaNode nodeManagerUaNode, NodeId nodeId, QualifiedName qualifiedName, LocalizedText localizedText) {
        super(nodeManagerUaNode, nodeId, qualifiedName, localizedText);
        NodeManager = (LcsNodeManager) nodeManagerUaNode;
    }

    /**
     * 初始化OPCUA Type
     *
     * @param manager 节点管理器
     * @return NodeId
     * @throws StatusException
     */
    public static NodeId initType(LcsNodeManager manager) throws StatusException {
        int ns = manager.getNamespaceIndex();
        NodeId thisTypeNodeId = new NodeId(ns, TYPE_ID);
        UaObjectType thisType = new UaObjectTypeNode(manager, thisTypeNodeId, new QualifiedName(ns, TYPE_NAME), UaHelperUtil.getLocalizedText(TYPE_NAME + UaHelperUtil.lexiconSuffixDisplayName, TYPE_NAME));
        manager.getServer().getNodeManagerRoot().getType(Identifiers.BaseEventType).addSubType(thisType);
        thisType.setDescription(UaHelperUtil.getLocalizedText(TYPE_NAME + UaHelperUtil.lexiconSuffixDescription, null));
//        manager.AddEventTypeField(thisTypeNodeId,BaseEventAdditionalField.values());
        manager.getServer().registerClass(LCS_Event_BaseType.class, thisTypeNodeId);
        return thisTypeNodeId;
    }

    enum BaseEventAdditionalField implements LcsNodeManager.EventAdditionalField {
        cabinetID("cabinetID", 30001, Identifiers.Int32),
        cabinetName("cabinetName", 30002, Identifiers.String),
        cabinetDesc("cabinetDesc", 30003, Identifiers.String);
        public final LcsNodeManager.AdditionalFieldDeclare declare;

        BaseEventAdditionalField(String nodeName, int id, NodeId datatype) {
            this.declare = new LcsNodeManager.AdditionalFieldDeclare(nodeName, id, datatype);
        }

        @Override
        public LcsNodeManager.AdditionalFieldDeclare getDeclare() {
            return declare;
        }
    }

//    public ByteString triggerEvent() {
//        try {
//            NodeManager.fillBaseEventInfo(this);
//        } catch (Exception e) {
//            log.error("填充 LCS_Event_BaseType 字段错误:{}", e.getMessage());
//        }
//        return this.triggerEvent(null);
//    }

}