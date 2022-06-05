package com.pxccn.PxcDali2.server.service.opcua.type;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaValueNode;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.builtintypes.QualifiedName;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.utils.NumericRange;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;

import java.util.*;

public class LCS_ComponentFastObjectNode extends LCS_FastObjectNodeBase {

    Map<FwProperty, declareItem> p_d_map_2 = new HashMap<>();
    Map<UaHelperUtil.UaVariableDeclare, FwProperty> d_p_map_2 = new HashMap<>();

    List<UaHelperUtil.UaDeclare[]> additionalDeclares = new LinkedList<>();
    static Map<Class<? extends LCS_ComponentFastObjectNode>, Set<Class<?>>> addedDeclares = new HashMap<>();

    public void addProperty(FwProperty property, NodeId defaultDataType) {
        var isWriteable = property.getFlag().isWriteable();
        var name = property.getName();
        var dataType = getDataType(property, defaultDataType);
        var s = new UaHelperUtil.VariableDeclareParams(isWriteable, dataType);
        declareItem d = new declareItem(name, s);
        p_d_map_2.put(property, d);
        d_p_map_2.put(d, property);
    }

    public void addProperty(FwProperty property) {
        this.addProperty(property, null);
    }

    public void addAdditionalDeclares(UaHelperUtil.UaDeclare[]... declares) {
        for (UaHelperUtil.UaDeclare[] d : declares) {
            if (d.length == 0) {
                continue;
            }
//            Class<?> dc = d[0].getDeclaringClass();
//            if (addedDeclares.get(this.getClass()).contains(dc)) {
//                continue;
//            }
            additionalDeclares.add(d);
//            addedDeclares.get(this.getClass()).add(dc);
        }
    }

    public Object getPropertyValue(FwProperty property) {
        var defaultVal = property.get();
        return defaultVal;
    }

    public void setPropertyValue(FwProperty property, Object value) {
        var t = property.getValueClass();
        if (value.getClass() == t) {
            property.set(value, FwContext.BY_OPCUA);
        } else {
            throw new RuntimeException("NOT IMPL TYPE");
        }
    }

    //TODO
    public void ownerChanged(FwProperty property) {
        declareItem item = p_d_map_2.get(property);
        if (item != null) {
            this.setDeclaredNodeValue(item, getPropertyValue(property));
        }
    }

    protected boolean onWriteValue(UaHelperUtil.UaVariableDeclare declared, UaValueNode uaValueNode, NumericRange numericRange, DataValue value) throws StatusException {

        var p = d_p_map_2.get(declared);
        if (p != null) {
            setPropertyValue(p, value.getValue().getValue());
            return true;
        }
        return false;
    }

    public LCS_ComponentFastObjectNode init() {
        super.init(getAllDeclares());

        p_d_map_2.forEach((p, d) -> {
            Object val = getPropertyValue(p);
            setDeclaredNodeValue(d, val);
        });

        this.afterInit();
        return this;
    }

    protected void afterInit() {

    }


    protected UaHelperUtil.UaDeclare[][] getAllDeclares() {
        UaHelperUtil.UaPropDeclare[] slots = p_d_map_2.values().toArray(new UaHelperUtil.UaPropDeclare[0]);
        List<UaHelperUtil.UaDeclare[]> tmp = new LinkedList<>(additionalDeclares);
        Collections.addAll(tmp, slots);

        return tmp.toArray(new UaHelperUtil.UaDeclare[0][0]);
    }


    private static NodeId getDataType(FwProperty property, NodeId defaultDataType) {
        if (defaultDataType != null) {
            return defaultDataType;
        }
        var t = property.getValueClass();
        if (t == Boolean.TYPE || t == Boolean.class) {
            return Identifiers.Boolean;
        } else if (t == Long.TYPE || t == Long.class) {
            return Identifiers.Int64;
        } else if (t == Double.TYPE || t == Double.class) {
            return Identifiers.Double;
        } else if (t == Float.TYPE || t == Float.class) {
            return Identifiers.Float;
        } else if (t == Integer.TYPE || t == Integer.class) {
            return Identifiers.Int32;
        } else if (t == String.class) {
            return Identifiers.String;
        } else if (t == UUID.class) {
            return Identifiers.String;
        }

//        else if (property.getDefaultValue() instanceof BFrozenEnum) {
//            throw new IllegalArgumentException("Must provide Enum Type Id");
//        } else if (t == BDouble.TYPE) {
//            return Identifiers.Double;
//        }
        throw new RuntimeException("Not Impl slot for Type " + t);
    }

    class declareItem implements UaHelperUtil.UaPropDeclare {
        declareItem(String slotName, UaHelperUtil.VariableDeclareParams setting) {
            this.slotName = slotName;
            this.setting = setting;
        }

        private final UaHelperUtil.VariableDeclareParams setting;
        private final String slotName;

        @Override
        public String name() {
            return slotName;
        }

        @Override
        public Class<?> getDeclaringClass() {
            return LCS_ComponentFastObjectNode.this.getClass();
        }

        @Override
        public UaHelperUtil.VariableDeclareParams getConfigure() {
            return this.setting;
        }
    }

    protected LCS_ComponentFastObjectNode(FwUaComponent uaComponent, NodeId nodeId, String qname, String dname) {
        super(
                uaComponent.getClass().getTypeName() + "_UaType",
                uaComponent.getClass().getTypeName() + "_UaType",
                nodeId,
                new QualifiedName(manager.getNamespaceIndex(), qname),
                dname,
                true
        );
    }

    protected LCS_ComponentFastObjectNode(FwUaComponent uaComponent, String nodeIdString, String qname, String dname) {
        this(
                uaComponent,
                createNodeId(uaComponent, nodeIdString),
                qname,
                dname
        );
    }

    protected LCS_ComponentFastObjectNode(FwUaComponent uaComponent, String nodeIdString, String qname) {
        this(
                uaComponent,
                createNodeId(uaComponent, nodeIdString),
                qname,
                qname
        );
    }

    protected LCS_ComponentFastObjectNode(FwUaComponent uaComponent, String qname) {
        this(
                uaComponent,
                createNodeId(uaComponent, qname),
                qname,
                qname
        );
    }


    public NodeId createNodeId(UaNode var1, QualifiedName var2, int var3) {
        String format = manager.getNodeIdFormat();
        if (format != null && !NodeId.isNull(var1.getNodeId())) {
            String var5 = var2.getNamespaceIndex() + ":" + var2.getName();
            return new NodeId(var3, String.format(format, var1.getNodeId().getValue().toString(), var5));
        } else {
            return new NodeId(var3, UUID.randomUUID());
        }
    }

    static NodeId createNodeId(FwUaComponent component, String objQualifiedName) {
        var parentNode = component.getParentNode();
        if (parentNode != null && parentNode != LcsNodeManager.objectsFolder) {
            return manager.createNodeId(parentNode, new QualifiedName(manager.getNamespaceIndex(), objQualifiedName));
        } else {
            return new NodeId(manager.getNamespaceIndex(), "" + manager.getNamespaceIndex() + ":" + objQualifiedName);

        }
    }
}
