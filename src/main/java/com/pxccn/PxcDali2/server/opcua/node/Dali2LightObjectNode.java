package com.pxccn.PxcDali2.server.opcua.node;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.nodes.BaseNode;
import com.prosysopc.ua.server.nodes.UaObjectNode;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.server.opcua.LcsNodeManager;

import java.util.Locale;
import java.util.UUID;

public class Dali2LightObjectNode extends BaseLcsObjectNode {


    public Dali2LightObjectNode(BaseNode parent, UUID lightUuid) throws StatusException {
        super(parent, new NodeId(parent.getNodeManager().getNamespaceIndex(), lightUuid), lightUuid.toString());

    }
}
