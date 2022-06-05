package com.pxccn.PxcDali2.server.opcua.type;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.TypeDefinitionId;
import com.prosysopc.ua.nodes.*;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.io.UaTypeIoListener;
import com.prosysopc.ua.server.nodes.CacheProperty;
import com.prosysopc.ua.server.nodes.PlainMethod;
import com.prosysopc.ua.server.nodes.UaCallable;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.*;
import com.prosysopc.ua.stack.utils.NumericRange;
import com.prosysopc.ua.types.opcua.server.FolderTypeNode;


import com.pxccn.PxcDali2.server.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.opcua.UaHelperUtil;

@TypeDefinitionId(nsu = LcsNodeManager.Namespace, i = LCS_NodeBasedMethodNode.TYPE_ID)
public class LCS_NodeBasedMethodNode extends FolderTypeNode {
    static final int TYPE_ID = 50888;
    static UaObjectType TYPE;
    CacheProperty Operate_invoke;
    CacheProperty Operate_running;
    CacheProperty Operate_done;
    UaCallable callOn;

    protected LCS_NodeBasedMethodNode(NodeManagerUaNode nodeManagerUaNode, NodeId nodeId, QualifiedName qualifiedName, LocalizedText localizedText) {
        super(nodeManagerUaNode, nodeId, qualifiedName, localizedText);
//        System.out.println("2121");
    }

    public static void createType(NodeManagerUaNode manager) throws StatusException {
        int ns = manager.getNamespaceIndex();
        NodeId nid = new NodeId(ns, TYPE_ID);
        UaObjectType thisType = new UaObjectTypeNode(manager, nid, new QualifiedName(ns, "LCS_NodeBasedMethod"), new LocalizedText("LCS_NodeBasedMethod"));
        manager.getServer().getNodeManagerRoot().getType(Identifiers.FolderType).addSubType(thisType);
        manager.getIoManager().addTypeListener(nid, _typeIoListener);
        TYPE = thisType;
        manager.getServer().registerClass(LCS_NodeBasedMethodNode.class, nid);
    }

    public void afterCreate() {
        super.afterCreate();
        this.setTypeDefinition(TYPE);
        this.setDescription(LocalizedText.EMPTY);
        try {

            {
                QualifiedName qn = new QualifiedName(nodeManager.getNamespaceIndex(), "Operate_invoke");
                Operate_invoke = new CacheProperty(nodeManager, nodeManager.createNodeId(this, qn), "Operate_invoke", null);
                Operate_invoke.setAccessLevel(AccessLevelType.of(AccessLevelType.CurrentRead, AccessLevelType.CurrentWrite));
                Operate_invoke.setDataTypeId(Identifiers.Boolean);
                Operate_invoke.setDescription(LocalizedText.EMPTY);
                Operate_invoke.setValue(false);
                this.addProperty(Operate_invoke);
            }
            {
                QualifiedName qn = new QualifiedName(nodeManager.getNamespaceIndex(), "Operate_running");
                Operate_running = new CacheProperty(nodeManager, nodeManager.createNodeId(this, qn), "Operate_running", null);
                Operate_running.setAccessLevel(AccessLevelType.CurrentRead);
                Operate_running.setDataTypeId(Identifiers.Boolean);
                Operate_running.setDescription(LocalizedText.EMPTY);
                Operate_running.setValue(false);
                this.addProperty(Operate_running);
            }
            {
                QualifiedName qn = new QualifiedName(nodeManager.getNamespaceIndex(), "Operate_done");
                Operate_done = new CacheProperty(nodeManager, nodeManager.createNodeId(this, qn), "Operate_done", null);
                Operate_done.setAccessLevel(AccessLevelType.CurrentRead);
                Operate_done.setDataTypeId(Identifiers.Boolean);
                Operate_done.setDescription(LocalizedText.EMPTY);
                Operate_done.setValue(false);
                this.addProperty(Operate_done);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    PlainMethod method;

    private static final UaTypeIoListener _typeIoListener = new UaTypeIoListener() {
        @Override
        public AccessLevelType onGetUserAccessLevel(ServiceContext serviceContext, UaInstance uaInstance, UaVariable uaVariable) {
            return null;
        }

        @Override
        public Boolean onGetUserExecutable(ServiceContext serviceContext, UaInstance uaInstance, UaMethod uaMethod) {
            return null;
        }

        @Override
        public AttributeWriteMask onGetUserWriteMask(ServiceContext serviceContext, UaInstance uaInstance, UaNode uaNode) {
            return null;
        }

        @Override
        public boolean onReadNonValue(ServiceContext serviceContext, UaInstance uaInstance, UaNode uaNode, UnsignedInteger unsignedInteger, DataValue dataValue) throws StatusException {
            return false;
        }

        @Override
        public boolean onReadValue(ServiceContext serviceContext, UaInstance uaInstance, UaValueNode uaValueNode, NumericRange numericRange, TimestampsToReturn timestampsToReturn, DateTime dateTime, DataValue dataValue) throws StatusException {
            return false;
        }

        @Override
        public boolean onWriteNonValue(ServiceContext serviceContext, UaInstance uaInstance, UaNode uaNode, UnsignedInteger unsignedInteger, DataValue dataValue) throws StatusException {
            return false;
        }

        @Override
        public boolean onWriteValue(ServiceContext serviceContext, UaInstance uaInstance, UaValueNode uaValueNode, NumericRange numericRange, DataValue dataValue) throws StatusException {
            if (uaInstance instanceof LCS_NodeBasedMethodNode) {
                ((LCS_NodeBasedMethodNode) uaInstance).onWriteValue(uaValueNode, numericRange, dataValue);
            }
            return false;
        }
    };

    protected boolean onWriteValue(UaValueNode uaValueNode, NumericRange numericRange, DataValue value) throws StatusException {
        if (uaValueNode == Operate_invoke) {
            try {
                onInvoke(value.getValue().booleanValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public void onInvoke(boolean value) throws MethodArgumentException, StatusException {
        if (this.callOn == null) {
            System.out.println("!!  NOT IMPL !!");
            return;
        }
        if (Operate_running.getValue().getValue().booleanValue()) {
            //重复运行
            return;
        }

        if (value) {
            Operate_running.setValue(true);
            Operate_done.setValue(false);
            LcsNodeManager.executorService.execute(() -> {
                try {
                    Variant[] args = createArgs();
                    Variant[] rets = callOn.callMethod(null, method.getNodeId(), args, null, null);
                    writeReturn(rets);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Operate_running.setValue(false);
                        if (Operate_invoke.getValue().getValue().booleanValue()) {
                            Operate_done.setValue(true);
                        } else {
                            Operate_done.setValue(false);
                        }
                    } catch (Exception ignore) {

                    }

                }

            });
        } else {
            Operate_done.setValue(false);
        }

    }

    Variant[] createArgs() throws MethodArgumentException {
        Argument[] inputs = method.getInputArguments();
        Variant[] args = new Variant[inputs.length];
        int index = -1;
        for (Argument arg : inputs) {
            index++;
            if (arg == null) {
                continue;
            }
            UaProperty p = this.getProperty(new QualifiedName(getNodeManager().getNamespaceIndex(), "Param_" + arg.getName()));
            if (p != null) {
                Variant v = p.getValue().getValue();
                args[index] = v;
            } else {
                throw new RuntimeException("INTERNAL ERROR");
            }
        }
        return args;
    }

    void writeReturn(Variant[] ret) throws MethodArgumentException, StatusException {
        if (ret == null) {
            return;
        }
        Argument[] outputs = method.getOutputArguments();
        int index = -1;
        for (Variant v : ret) {
            index++;
            if (v == null) {
                continue;
            }
            if (index > outputs.length) {
                throw new RuntimeException("INTERNAL ERROR");
            }
            UaProperty p = this.getProperty(new QualifiedName(getNodeManager().getNamespaceIndex(), "Ret_" + outputs[index].getName()));
            if (p != null) {
                p.setValue(v.getValue());
            } else {
                throw new RuntimeException("INTERNAL ERROR");
            }
        }
    }

    public static class Builder {
        Argument[] inputs, outputs;
        UaNode parent;
        NodeId nid;
        QualifiedName qn;
        LocalizedText displayName;
        LocalizedText desc;
        NodeManagerUaNode manager;
        PlainMethod m;

        public LCS_NodeBasedMethodNode build() {
            LCS_NodeBasedMethodNode target = new LCS_NodeBasedMethodNode(manager, nid, qn, displayName);
            for (Argument arg : inputs) {
                if (arg == null) {
                    continue;
                }
                CacheProperty cp = createParameterProp(target, arg, true);
                target.addProperty(cp);
            }

            for (Argument arg : outputs) {
                if (arg == null) {
                    continue;
                }
                CacheProperty cp = createParameterProp(target, arg, false);
                target.addProperty(cp);
            }
            target.method = m;
            try {
                target.callOn = (UaCallable) parent;
//                System.out.println(1);
            } catch (Exception ignore) {

            }
            target.afterCreate();
            return target;
        }

        private CacheProperty createParameterProp(UaNode parent, Argument arg, boolean isParameter) {
            String paramName = arg.getName();
            NodeId paramType = arg.getDataType();
            Integer paramValueRank = arg.getValueRank();
            UnsignedInteger[] paramArrayDimensions = arg.getArrayDimensions();
            LocalizedText paramDesc = arg.getDescription();
            QualifiedName pqn = new QualifiedName(manager.getNamespaceIndex(), (isParameter ? "Param_" : "Ret_") + paramName);
            CacheProperty cp = new CacheProperty(manager, manager.createNodeId(parent, pqn), pqn, new LocalizedText((isParameter ? "Param_" : "Ret_") + paramName));
            cp.setDescription(paramDesc);
            if (isParameter) {
                cp.setAccessLevel(AccessLevelType.of(AccessLevelType.CurrentRead, AccessLevelType.CurrentWrite));
            } else {
                cp.setAccessLevel(AccessLevelType.CurrentRead);
            }
            cp.setDataTypeId(paramType);
            cp.setArrayDimensions(paramArrayDimensions);
            cp.setValueRank(paramValueRank);
            try {
                cp.updateValue(UaHelperUtil.getInitValue(paramType, -1, paramValueRank));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cp;
        }


        public Builder setManager(NodeManagerUaNode manager) {
            this.manager = manager;
            return this;
        }

        public Builder set(PlainMethod method, UaNode parent) throws MethodArgumentException {
            m = method;
            inputs = method.getInputArguments();
            outputs = method.getOutputArguments();
            this.parent = parent;
            this.displayName = new LocalizedText("operateNodes");
            this.qn = new QualifiedName(manager.getNamespaceIndex(), "operateNodes");
            this.nid = manager.createNodeId(method, qn);
            this.desc = method.getDescription();
            return this;
        }


    }

}
