package com.pxccn.PxcDali2.server.service.opcua.type;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.*;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.ServiceContext;
import com.prosysopc.ua.server.io.UaTypeIoListener;
import com.prosysopc.ua.server.io.UaVariableIoListener;
import com.prosysopc.ua.server.nodes.UaObjectTypeNode;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.*;
import com.prosysopc.ua.stack.utils.NumericRange;
import com.prosysopc.ua.types.opcua.server.BaseObjectTypeNode;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


//0~99

/**
 * LCS_BaseObjectTypeNode
 * LCS对象基类型
 */
@Slf4j
public class LCS_BaseObjectTypeNode extends BaseObjectTypeNode {
    public static final int TYPE_ID = 0;
    private static final String TYPE_NAME = LCS_BaseObjectTypeNode.class.getSimpleName();
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
            if (uaInstance instanceof LCS_BaseObjectTypeNode) {
                ((LCS_BaseObjectTypeNode) uaInstance).onReadNonValue(((LCS_BaseObjectTypeNode) uaInstance)._variableCache.get(uaNode.getNodeId()), uaNode, unsignedInteger, dataValue);
            }
            return false;
        }

        @Override
        public boolean onReadValue(ServiceContext serviceContext, UaInstance uaInstance, UaValueNode uaValueNode, NumericRange numericRange, TimestampsToReturn timestampsToReturn, DateTime dateTime, DataValue dataValue) throws StatusException {
            if (uaInstance instanceof LCS_BaseObjectTypeNode) {
                ((LCS_BaseObjectTypeNode) uaInstance).onReadValue(((LCS_BaseObjectTypeNode) uaInstance)._variableCache.get(uaValueNode.getNodeId()), uaValueNode, numericRange, timestampsToReturn, dateTime, dataValue);
            }
            return false;
        }

        @Override
        public boolean onWriteNonValue(ServiceContext serviceContext, UaInstance uaInstance, UaNode uaNode, UnsignedInteger unsignedInteger, DataValue dataValue) throws StatusException {
            if (uaInstance instanceof LCS_BaseObjectTypeNode) {
                ((LCS_BaseObjectTypeNode) uaInstance).onWriteNonValue(((LCS_BaseObjectTypeNode) uaInstance)._variableCache.get(uaNode.getNodeId()), uaNode, unsignedInteger, dataValue);
            }
            return false;
        }

        @Override
        public boolean onWriteValue(ServiceContext serviceContext, UaInstance uaInstance, UaValueNode uaValueNode, NumericRange numericRange, DataValue dataValue) throws StatusException {
            if (uaInstance instanceof LCS_BaseObjectTypeNode) {
                ((LCS_BaseObjectTypeNode) uaInstance).onWriteValue(((LCS_BaseObjectTypeNode) uaInstance)._variableCache.get(uaValueNode.getNodeId()), uaValueNode, numericRange, dataValue);
            }
            return false;
        }
    };
    //所有节点缓存
    private final Map<UaHelperUtil.UaDeclare, UaNode> _nodeCache = new HashMap<>();
    //方法节点缓存(专用)
    private final Map<NodeId, UaHelperUtil.UaMethodDeclare> _methodCache = new HashMap<>();
    //变量、属性节点缓存（专用）
    private final Map<NodeId, UaHelperUtil.UaVariableDeclare> _variableCache = new HashMap<>();

    protected LCS_BaseObjectTypeNode(NodeManagerUaNode nodeManagerUaNode, NodeId nodeId, QualifiedName qualifiedName, LocalizedText localizedText) {
        super(nodeManagerUaNode, nodeId, qualifiedName, localizedText);
    }

    /**
     * 初始化OPCUA Type
     *
     * @param manager 节点管理器
     * @return NodeId
     * @throws StatusException 异常
     */
    public static NodeId initType(NodeManagerUaNode manager) throws StatusException {
        int ns = manager.getNamespaceIndex();
        NodeId thisTypeNodeId = new NodeId(ns, TYPE_ID);
        UaObjectType thisType = new UaObjectTypeNode(manager, thisTypeNodeId, new QualifiedName(ns, TYPE_NAME), UaHelperUtil.getLocalizedText(TYPE_NAME + UaHelperUtil.lexiconSuffixDisplayName, TYPE_NAME));
        manager.getServer().getNodeManagerRoot().getType(Identifiers.BaseObjectType).addSubType(thisType);
        thisType.setDescription(UaHelperUtil.getLocalizedText(TYPE_NAME + UaHelperUtil.lexiconSuffixDescription, null));
        manager.getServer().registerClass(LCS_BaseObjectTypeNode.class, thisTypeNodeId);
        return thisTypeNodeId;
    }

    /**
     * 添加Type监听
     *
     * @param manager 节点管理器
     * @param nodeId  节点ID
     */
    protected static void addTypeListener(NodeManagerUaNode manager, NodeId nodeId) {
        manager.getIoManager().addTypeListener(nodeId, _typeIoListener);
    }

    /**
     * 获取命名空间索引
     *
     * @return 索引
     */
    public int getNs() {
        return this.getNodeManager().getNamespaceIndex();
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
    //TODO
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
     * 初始化 UaDeclare 所声明的变量，
     * 对所有节点添加缓存引用
     * 对于变量节点，设定初始值并对可写变量添加监听
     *
     * @param vars 枚举数组
     */
    protected void initDeclaredInInstance(UaHelperUtil.UaDeclare[] vars) {
        for (UaHelperUtil.UaDeclare var : vars) {

            //创建缓存用
            UaNode n = this.getDeclaredNode(var);
            if (var instanceof UaHelperUtil.UaMethodDeclare) {
                _methodCache.put(n.getNodeId(), (UaHelperUtil.UaMethodDeclare) var);
            }
            //如果是变量的实例，则写初值
            if (var instanceof UaHelperUtil.UaVariableDeclare) {
                UaHelperUtil.UaVariableDeclare var2 = (UaHelperUtil.UaVariableDeclare) var;
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
                        if (writeable && var.getParent() != null) {
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

    private UaNode getDeclaredNodeImpl(UaHelperUtil.UaDeclare enumSlot) {
        UaNode ret;
        if ((ret = _nodeCache.get(enumSlot)) != null) {
            return ret;
        }

        if (enumSlot instanceof UaHelperUtil.UaFolderDeclare) {
            ret = UaHelperUtil.getFolder(this, new QualifiedName(getNs(), enumSlot.name()));
        } else if (enumSlot instanceof UaHelperUtil.UaPropDeclare) {
            ret = UaHelperUtil.getParentNode(getNs(), enumSlot, this).getProperty(new QualifiedName(getNs(), enumSlot.name()));
        } else {
            ret = UaHelperUtil.getParentNode(getNs(), enumSlot, this).getComponent(new QualifiedName(getNs(), enumSlot.name()));
        }
        if (ret == null) {
            throw new RuntimeException("can not get node of " + enumSlot);
        }
        _nodeCache.put(enumSlot, ret);
        return ret;
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


    //接收到Method调用,转换为UaMethodDeclare并转发给onMethodCall
    public Variant[] callMethod(ServiceContext serviceContext, NodeId objectId, Variant[] inputArguments, StatusCode[] inputArgumentResults, DiagnosticInfo[] inputArgumentDiagnosticInfos) throws StatusException {
        UaHelperUtil.UaMethodDeclare t = _methodCache.get(objectId);
        if (t != null) {
            return this.onMethodCall(t, inputArguments);
        }
        return super.callMethod(serviceContext, objectId, inputArguments, inputArgumentResults, inputArgumentDiagnosticInfos);
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
            LCS_BaseObjectTypeNode.this.onReadNonValue(declare, node, unsignedInteger, dataValue);
            return false;
        }

        @Override
        public boolean onReadValue(ServiceContext serviceContext, NumericRange numericRange, TimestampsToReturn timestampsToReturn, DateTime dateTime, DataValue dataValue) throws StatusException {
            LCS_BaseObjectTypeNode.this.onReadValue(declare, node, numericRange, timestampsToReturn, dateTime, dataValue);
            return false;
        }

        @Override
        public boolean onWriteNonValue(ServiceContext serviceContext, UnsignedInteger unsignedInteger, DataValue dataValue) throws StatusException {
            LCS_BaseObjectTypeNode.this.onWriteNonValue(declare, node, unsignedInteger, dataValue);
            return false;
        }

        @Override
        public boolean onWriteValue(ServiceContext serviceContext, NumericRange numericRange, DataValue dataValue) throws StatusException {
            LCS_BaseObjectTypeNode.this.onWriteValue(declare, node, numericRange, dataValue);
            return false;
        }

        @Override
        public AttributeWriteMask onGetUserWriteMask(ServiceContext serviceContext) {
            return null;
        }
    }
}


