package com.pxccn.PxcDali2.server.opcua.type;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.TypeDefinitionId;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaObjectType;
import com.prosysopc.ua.nodes.UaReference;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.nodes.UaCallable;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.types.opcua.server.FolderTypeNode;
import com.pxccn.PxcDali2.server.opcua.LcsNodeManager;

@TypeDefinitionId(nsu = LcsNodeManager.Namespace, i = 50889)
public class LCS_Folder extends FolderTypeNode {
    protected LCS_Folder(NodeManagerUaNode nodeManagerUaNode, NodeId nodeId, QualifiedName qualifiedName, LocalizedText localizedText) {
        super(nodeManagerUaNode, nodeId, qualifiedName, localizedText);
    }

    public static NodeId initType(NodeManagerUaNode manager) throws StatusException {
        int ns = manager.getNamespaceIndex();
        NodeId thisTypeNodeId = new NodeId(ns, 50889);
        UaObjectType thisType = new UaObjectTypeNode(manager, thisTypeNodeId, new QualifiedName(ns, "LCS_Folder"), new LocalizedText("LCS_Folder"));
        manager.getServer().getNodeManagerRoot().getType(Identifiers.FolderType).addSubType(thisType);
        thisType.setDescription(LocalizedText.EMPTY);
        manager.getServer().registerClass(LCS_Folder.class, thisTypeNodeId);
        return thisTypeNodeId;
    }

    public Variant[] callMethod(ServiceContext var1, NodeId var2, Variant[] var3, StatusCode[] var4, DiagnosticInfo[] var5) throws StatusException {
        for (UaReference ref : this.getInverseReferences(Identifiers.HasComponent)) {
            UaNode n = ref.getSourceNode();
            if (n instanceof UaCallable) {
                return ((UaCallable) n).callMethod(var1, var2, var3, var4, var5);
            }
        }
        return super.callMethod(var1, var2, var3, var4, var5);
    }
}


