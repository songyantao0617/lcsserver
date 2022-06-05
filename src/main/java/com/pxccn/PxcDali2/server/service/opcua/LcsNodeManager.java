package com.pxccn.PxcDali2.server.service.opcua;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNodeFactoryException;
import com.prosysopc.ua.nodes.UaObject;
import com.prosysopc.ua.nodes.UaObjectType;
import com.prosysopc.ua.nodes.UaType;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.UaServer;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_BaseObjectTypeNode;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_FastObjectNodeBase;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_Folder;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_NodeBasedMethodNode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LcsNodeManager extends NodeManagerUaNode {
    public static final String Namespace = "http://pxc.com/BMW/LCS";
    public static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static LcsNodeManager MANAGER;

    public LcsNodeManager(UaServer server, String namespaceUri) {
        super(server, namespaceUri);
        MANAGER = this;
    }

    @Override
    protected void init() throws StatusException, UaNodeFactoryException {
        super.init();
        objectsFolder = getServer().getNodeManagerRoot().getObjectsFolder();
        BaseObjectType = getServer().getNodeManagerRoot().getType(Identifiers.BaseObjectType);
        registerNodeType();
        createAddressSpace();
    }

    public static UaObject objectsFolder;
    public static UaType BaseObjectType;
//    public static FolderTypeNode allLightsFolderNode;
//    public static FolderTypeNode allRoomsFolderNode;
//    public static FolderTypeNode allCabinetsFolderNode;

    private void initFastObject() throws StatusException {
        UaObjectType fastType = new UaObjectTypeNode(this,
                new NodeId(this.getNamespaceIndex(), "LCS_FastObjectTypes"),
                new QualifiedName(this.getNamespaceIndex(), "LCS_FastObjectTypes"),
                new LocalizedText("LCS_FastObjectTypes"));
        fastType.setDescription(LocalizedText.EMPTY);
        BaseObjectType.addSubType(fastType);
        LCS_FastObjectNodeBase.init(fastType, this);
    }

    private void registerNodeType() throws StatusException {
        initFastObject();

        LCS_BaseObjectTypeNode.initType(this);
        LCS_Folder.initType(this);
        LCS_NodeBasedMethodNode.createType(this);
    }

    private void createAddressSpace() throws StatusException {
//        this.allLightsFolderNode = createInstance(FolderTypeNode.class, "Lights", new NodeId(getNamespaceIndex(), "AllLights"));
//        this.addNodeAndReference(objectsFolder, allLightsFolderNode, Identifiers.Organizes);
//        this.allRoomsFolderNode = createInstance(FolderTypeNode.class, "Rooms", new NodeId(getNamespaceIndex(), "AllRooms"));
//        this.addNodeAndReference(objectsFolder, allRoomsFolderNode, Identifiers.Organizes);
//        this.allCabinetsFolderNode = createInstance(FolderTypeNode.class, "Cabinets", new NodeId(getNamespaceIndex(), "Cabinets"));
//        this.addNodeAndReference(objectsFolder, allCabinetsFolderNode, Identifiers.Organizes);
    }
}
