package com.pxccn.PxcDali2.server.service.opcua.node;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.server.nodes.BaseNode;
import com.prosysopc.ua.server.nodes.UaObjectNode;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.core.Identifiers;

import java.util.Locale;

public class BaseLcsObjectNode {
    public final NodeId nodeId;
    public final UaObjectNode node;

    public BaseLcsObjectNode(BaseNode parent, NodeId nodeId, String name) throws StatusException {
        var manager = parent.getNodeManager();
        this.nodeId = nodeId;
        node = new UaObjectNode(manager, this.nodeId, name, Locale.ENGLISH);
        node.setTypeDefinition(manager.getServer().getNodeManagerRoot().getType(Identifiers.BaseObjectType));
        parent.addComponent(node);
    }
}
