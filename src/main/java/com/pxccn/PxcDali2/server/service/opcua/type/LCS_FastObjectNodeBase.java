package com.pxccn.PxcDali2.server.service.opcua.type;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.*;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.io.UaTypeIoListener;
import com.prosysopc.ua.server.io.UaVariableIoListener;
import com.prosysopc.ua.server.nodes.UaObjectNode;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.*;
import com.prosysopc.ua.stack.utils.NumericRange;
import com.pxccn.PxcDali2.server.service.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class LCS_FastObjectNodeBase extends UaObjectNode {
    private static final Map<NodeId, UaObjectType> objectTypeNodes = new HashMap<NodeId, UaObjectType>();
    private static final Object lockForCreateType = new Object();
    //TypeIoListener
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
            if (uaInstance instanceof LCS_FastObjectNodeBase) {
                ((LCS_FastObjectNodeBase) uaInstance).onReadNonValue(((LCS_FastObjectNodeBase) uaInstance)._variableCache.get(uaNode.getNodeId()), uaNode, unsignedInteger, dataValue);
            }
            return false;
        }

        @Override
        public boolean onReadValue(ServiceContext serviceContext, UaInstance uaInstance, UaValueNode uaValueNode, NumericRange numericRange, TimestampsToReturn timestampsToReturn, DateTime dateTime, DataValue dataValue) throws StatusException {
            if (uaInstance instanceof LCS_FastObjectNodeBase) {
                ((LCS_FastObjectNodeBase) uaInstance).onReadValue(((LCS_FastObjectNodeBase) uaInstance)._variableCache.get(uaValueNode.getNodeId()), uaValueNode, numericRange, timestampsToReturn, dateTime, dataValue);
            }
            return false;
        }

        @Override
        public boolean onWriteNonValue(ServiceContext serviceContext, UaInstance uaInstance, UaNode uaNode, UnsignedInteger unsignedInteger, DataValue dataValue) throws StatusException {
            if (uaInstance instanceof LCS_FastObjectNodeBase) {
                ((LCS_FastObjectNodeBase) uaInstance).onWriteNonValue(((LCS_FastObjectNodeBase) uaInstance)._variableCache.get(uaNode.getNodeId()), uaNode, unsignedInteger, dataValue);
            }
            return false;
        }

        @Override
        public boolean onWriteValue(ServiceContext serviceContext, UaInstance uaInstance, UaValueNode uaValueNode, NumericRange numericRange, DataValue dataValue) throws StatusException {
            if (uaInstance instanceof LCS_FastObjectNodeBase) {
                ((LCS_FastObjectNodeBase) uaInstance).onWriteValue(((LCS_FastObjectNodeBase) uaInstance)._variableCache.get(uaValueNode.getNodeId()), uaValueNode, numericRange, dataValue);
            }
            return false;
        }
    };
    public static LcsNodeManager manager;
    static UaType objectTypeContainer;
    private static boolean basicInitFinish = false;
    private final Map<UaHelperUtil.UaDeclare, UaNode> _nodeCache = new HashMap<>();
    private final Map<NodeId, UaHelperUtil.UaMethodDeclare> _methodCache = new HashMap<>();
    private final Map<NodeId, UaHelperUtil.UaVariableDeclare> _variableCache = new HashMap<>();


    protected LCS_FastObjectNodeBase(String typeNodeIdString, String typeDisplayNameString, NodeId objNodeId, QualifiedName objQualifiedName, String objLocalizedText, boolean addTypeListener) {
        super(manager, objNodeId, objQualifiedName, new LocalizedText(objLocalizedText));
        UaObjectType objType = getObjectType(typeNodeIdString, typeDisplayNameString);
        setTypeDefinition(objType);
        if (addTypeListener) {
            addTypeListener(objType.getNodeId());
        }
    }

    protected LCS_FastObjectNodeBase(String typeNodeIdString, String typeDisplayNameString, NodeId objNodeId, QualifiedName objQualifiedName, String objLocalizedText, boolean addTypeListener, UaHelperUtil.UaDeclare[]... declares) {
        this(typeNodeIdString, typeDisplayNameString, objNodeId, objQualifiedName, objLocalizedText, addTypeListener);
        init(declares);
    }

    //由Manager初始化调用
    public static void init(UaType objectTypeContainer, LcsNodeManager manager) {
        if (!basicInitFinish) {
            LCS_FastObjectNodeBase.objectTypeContainer = objectTypeContainer;
            LCS_FastObjectNodeBase.manager = manager;
        }
        basicInitFinish = true;
    }

    /**
     * 添加Type监听
     *
     * @param nodeId 节点ID
     */
    protected static void addTypeListener(NodeId nodeId) {
        manager.getIoManager().addTypeListener(nodeId, _typeIoListener);
    }

    protected void init(UaHelperUtil.UaDeclare[]... declares) {
        setDescription(LocalizedText.EMPTY);
        UaHelperUtil.UaDeclare.initNodes(manager, this, _nodeCache, declares);
        initDeclaredInInstance(declares);
    }

    public int getNamespaceIndex() {
        return manager.getNamespaceIndex();
    }

    /**
     * 获取通过UaHelperUtil方式声明的节点实例
     *
     * @param enumSlot 枚举条目
     * @return 节点
     */
    public UaNode getDeclaredNode(UaHelperUtil.UaDeclare enumSlot) {
        return getDeclaredNodeImpl(enumSlot);
    }

    /**
     * 写入 Variable 或 Property 变量
     * 重复值检查，本函数将只在目标值与当前值不同时才进行写入
     *
     * @param enumSlot 枚举条目
     * @param Value    值
     * @return 写入有效
     */
    public boolean setDeclaredNodeValue(UaHelperUtil.UaVariableDeclare enumSlot, Object Value) {
        return setDeclaredNodeValueImpl(enumSlot, Value);
    }

    /**
     * 接收到 Variable 或 Property 变化事件
     *
     * @param declared  枚举条目
     * @param uaNode    原始节点
     * @param prevValue 先前值
     * @param value     当前值
     * @return 是否已经匹配
     */
    protected boolean onDataChange(UaHelperUtil.UaVariableDeclare declared, UaNode uaNode, DataValue prevValue, DataValue value) {
        return false;
    }

    /**
     * OPCUA客户端写入变量事件
     *
     * @param declared     枚举条目
     * @param uaValueNode  原始节点
     * @param numericRange 值范围
     * @param value        值
     * @return 是否已经匹配
     * @throws StatusException 阻止写入时抛出异常
     */
    protected boolean onWriteValue(UaHelperUtil.UaVariableDeclare declared, UaValueNode uaValueNode, NumericRange numericRange, DataValue value) throws StatusException {
        return false;
    }

    /**
     * OPCUA客户端写入非值变量事件
     *
     * @param declared        枚举条目
     * @param uaNode          原始节点
     * @param unsignedInteger 值ID
     * @param value           值
     * @return 是否已经匹配
     * @throws StatusException 阻止写入时抛出异常
     */
    protected boolean onWriteNonValue(UaHelperUtil.UaVariableDeclare declared, UaNode uaNode, UnsignedInteger unsignedInteger, DataValue value) throws StatusException {
        return false;
    }

    /**
     * OPCUA客户端读取变量事件
     *
     * @param declared           枚举条目
     * @param uaValueNode        原始节点
     * @param numericRange       数值范围
     * @param timestampsToReturn 时间戳类型
     * @param dateTime           时间戳
     * @param dataValue          值
     * @return 是否已经匹配
     * @throws StatusException 阻止读取时抛出异常
     */
    protected boolean onReadValue(UaHelperUtil.UaVariableDeclare declared, UaValueNode uaValueNode, NumericRange numericRange, TimestampsToReturn timestampsToReturn, DateTime dateTime, DataValue dataValue) throws StatusException {
        return false;
    }

    /**
     * OPCUA客户端读取非值变量事件
     *
     * @param declared        枚举条目
     * @param uaNode          原始节点
     * @param unsignedInteger 值ID
     * @param value           值
     * @return 是否已经匹配
     * @throws StatusException 阻止读取时抛出异常
     */
    protected boolean onReadNonValue(UaHelperUtil.UaVariableDeclare declared, UaNode uaNode, UnsignedInteger unsignedInteger, DataValue value) throws StatusException {
        return false;
    }

    /**
     * 接收到Method调用
     *
     * @param declared 枚举条目
     * @param input    输入参数
     * @return 返回值
     * @throws StatusException 返回给OPCUA的异常
     */
    protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
        throw new StatusException(StatusCodes.Bad_MethodInvalid);
    }

    /**
     * 获取或创建类型节点
     *
     * @param typeNodeIdString      类型字符串ID
     * @param typeDisplayNameString 类型显示名称
     * @return UaObjectType
     */
    private UaObjectType getObjectType(String typeNodeIdString, String typeDisplayNameString) {
        NodeId typeNodeId = new NodeId((getNamespaceIndex()), typeNodeIdString);


        UaObjectType typeNode = objectTypeNodes.get(typeNodeId);
        if (typeNode == null) {
            synchronized (lockForCreateType) {
                if ((typeNode = objectTypeNodes.get(typeNodeId)) == null) {
                    try {
                        typeNode = new UaObjectTypeNode(manager, new NodeId(getNamespaceIndex(), typeNodeIdString), typeDisplayNameString, null);
                        manager.addNodeAndReference(objectTypeContainer, typeNode, Identifiers.HasSubtype);
                        LCS_FastObjectNodeBase.objectTypeNodes.put(typeNode.getNodeId(), typeNode);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
        return typeNode;
    }

    //接收到Method调用,转换为UaMethodDeclare并转发给onMethodCall
    public Variant[] callMethod(ServiceContext serviceContext, NodeId methodId, Variant[] inputArguments, StatusCode[] inputArgumentResults, DiagnosticInfo[] inputArgumentDiagnosticInfos) throws StatusException {
        UaHelperUtil.UaMethodDeclare t = _methodCache.get(methodId);
        if (t != null) {
            return this.onMethodCall(t, inputArguments);
        }
        return super.callMethod(serviceContext, methodId, inputArguments, inputArgumentResults, inputArgumentDiagnosticInfos);
    }

    /**
     * 初始化 UaDeclare 所声明的变量，
     * 对所有节点添加缓存引用
     * 对于变量节点，设定初始值并对可写变量添加监听
     *
     * @param vars 枚举数组
     */
    private void initDeclaredInInstance(UaHelperUtil.UaDeclare[]... vars) {
        for (UaHelperUtil.UaDeclare[] var : vars) {
            for (UaHelperUtil.UaDeclare va : var) {

                //创建缓存用
                UaNode n = this.getDeclaredNode(va);
                if (va instanceof UaHelperUtil.UaMethodDeclare) {
                    _methodCache.put(n.getNodeId(), (UaHelperUtil.UaMethodDeclare) va);
                }
                //如果是变量的实例，则写初值
                else if (va instanceof UaHelperUtil.UaVariableDeclare) {
                    UaHelperUtil.UaVariableDeclare var2 = (UaHelperUtil.UaVariableDeclare) va;
                    this.setDeclaredNodeValue(var2, UaHelperUtil.getInitValue(var2));
                    _variableCache.put(n.getNodeId(), var2);
                    //如果是可写变量，增加事件回调
                    boolean writeable;
                    int listenerType;
                    UaHelperUtil.VariableDeclareParams params;
                    if ((params = var2.getConfigure()) != null) {
                        writeable = params.getWriteable();
                        listenerType = params.getListenerType();
                    } else {
                        writeable = var2.getWriteable();
                        listenerType = var2.getListenerType();
                    }
                    switch (listenerType) {
                        case 0:
                            if (writeable && va.getParent() != null) {
                                this.addNodeListener(this.nodeManager, ((UaVariable) n));
                            }
                            break;
                        case 1:
                            this.addNodeListener(this.nodeManager, ((UaVariable) n));
                            break;
                        case 2:
                            addDataChangeListener((UaVariable) n);
                            break;
                        case 3:
                            this.addNodeListener(this.nodeManager, ((UaVariable) n));
                            addDataChangeListener((UaVariable) n);
                            break;
                        default:
                            throw new RuntimeException("UNKNOWN LISTENER TYPE : " + listenerType);
                    }
                }
            }
        }

    }

    private void addDataChangeListener(UaVariable node) {
        node.addDataChangeListener((uaNode, prevValue, value) -> {
            if (value == null) {
                return;
            }
            NodeId current = uaNode.getNodeId();
            UaHelperUtil.UaVariableDeclare t = _variableCache.get(current);
            if (t != null) {
                onDataChange(t, uaNode, prevValue, value);
            } else {
                log.error("internal error : can not get cache of " + uaNode.getNodeId());
            }
        });
    }

    /**
     * 添加Node监听
     *
     * @param node 变量节点
     */
    protected void addNodeListener(NodeManagerUaNode manager, UaValueNode node) {
        manager.getIoManager().addNodeListener(node, new LCS_UaVariableIoListener(node, _variableCache.get(node.getNodeId())));
    }

    private boolean setDeclaredNodeValueImpl(UaHelperUtil.UaVariableDeclare enumSlot, Object Value) {
        boolean ret = false;
        UaVariable variable = (UaVariable) getDeclaredNode(enumSlot);
        if (variable != null) {
            try {
                if (enumSlot.getArrLength() == -1) {
                    if (!Objects.equals(variable.getValue().getValue().getValue(), Value)) {
                        variable.setValue(Value);
                        ret = true;
                    }
                } else {
//                    throw new RuntimeException("not impl ..");
                    variable.setValue(Value);
                }
            } catch (StatusException e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    private UaNode getDeclaredNodeImpl(UaHelperUtil.UaDeclare enumSlot) {
        UaNode ret;
        if ((ret = _nodeCache.get(enumSlot)) != null) {
            return ret;
        }
        if (enumSlot instanceof UaHelperUtil.UaFolderDeclare) {
            ret = UaHelperUtil.getFolder(this, new QualifiedName(getNamespaceIndex(), enumSlot.name()));
        } else if (enumSlot instanceof UaHelperUtil.UaPropDeclare) {
            ret = UaHelperUtil.getParentNode(getNamespaceIndex(), enumSlot, this).getProperty(new QualifiedName(getNamespaceIndex(), enumSlot.name()));
        } else {
            ret = UaHelperUtil.getParentNode(getNamespaceIndex(), enumSlot, this).getComponent(new QualifiedName(getNamespaceIndex(), enumSlot.name()));
        }
        if (ret == null) {
            throw new RuntimeException("can not get node of " + enumSlot);
        }
        _nodeCache.put(enumSlot, ret);
        return ret;
    }

    private class LCS_UaVariableIoListener implements UaVariableIoListener {
        UaValueNode node;
        UaHelperUtil.UaVariableDeclare declare;

        public LCS_UaVariableIoListener(UaValueNode node, UaHelperUtil.UaVariableDeclare declare) {
            this.node = node;
            if (declare == null) {
                throw new RuntimeException("can not get " + node.getNodeId());
            }
            this.declare = declare;
        }

        @Override
        public AccessLevelType onGetUserAccessLevel(ServiceContext serviceContext) {
            return null;
        }

        @Override
        public boolean onReadNonValue(ServiceContext serviceContext, UnsignedInteger unsignedInteger, DataValue dataValue) throws StatusException {
            LCS_FastObjectNodeBase.this.onReadNonValue(declare, node, unsignedInteger, dataValue);
            return false;
        }

        @Override
        public boolean onReadValue(ServiceContext serviceContext, NumericRange numericRange, TimestampsToReturn timestampsToReturn, DateTime dateTime, DataValue dataValue) throws StatusException {
            LCS_FastObjectNodeBase.this.onReadValue(declare, node, numericRange, timestampsToReturn, dateTime, dataValue);
            return false;
        }

        @Override
        public boolean onWriteNonValue(ServiceContext serviceContext, UnsignedInteger unsignedInteger, DataValue dataValue) throws StatusException {
            LCS_FastObjectNodeBase.this.onWriteNonValue(declare, node, unsignedInteger, dataValue);
            return false;
        }

        @Override
        public boolean onWriteValue(ServiceContext serviceContext, NumericRange numericRange, DataValue dataValue) throws StatusException {
            LCS_FastObjectNodeBase.this.onWriteValue(declare, node, numericRange, dataValue);
            return false;
        }

        @Override
        public AttributeWriteMask onGetUserWriteMask(ServiceContext serviceContext) {
            return null;
        }
    }

}
