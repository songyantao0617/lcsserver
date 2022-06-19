package com.pxccn.PxcDali2.server.service.opcua;

import com.prosysopc.ua.ValueRanks;
import com.prosysopc.ua.nodes.MethodArgumentException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.nodes.UaReference;
import com.prosysopc.ua.server.ModellingRule;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.nodes.CacheProperty;
import com.prosysopc.ua.server.nodes.CacheVariable;
import com.prosysopc.ua.server.nodes.PlainMethod;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.AccessLevelType;
import com.prosysopc.ua.stack.core.Argument;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.types.opcua.server.FolderTypeNode;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_Folder;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_NodeBasedMethodNode;

import java.util.Arrays;
import java.util.Map;


public class UaHelperUtil {

    public static final String lexiconSuffixDisplayName = ".displayName";
    public static final String lexiconSuffixDescription = ".description";

    /**
     * 获取本地化字符，作为节点名称或描述
     *
     * @param key         键
     * @param defaultText 默认值
     * @return LocalizedText
     */
    public static LocalizedText getLocalizedText(String key, String defaultText) {
        if(defaultText == null)
            return LocalizedText.EMPTY;
        return new LocalizedText(defaultText);
//        String str = Util.GetLightControlSystem().getLex().get(key);
//        if (str != null) {
//            if (str.isEmpty()) {
//                if (defaultText == null) {
//                    return LocalizedText.EMPTY;
//                } else {
//                    return new LocalizedText(defaultText);
//                }
//            } else {
//                return new LocalizedText(str, Util.GetLightControlSystem().localeTag);
//            }
//        } else {
////            System.out.println(key + "=");//打印未找到的键
//            if (defaultText == null) {
//                return LocalizedText.EMPTY;
//            } else {
//                return new LocalizedText(defaultText);
//            }
//        }
    }

    public static LocalizedText getLocalizedText(Class<?> cls, String name, String defaultText) {
        return getLocalizedText(getKeyBase(cls, name), defaultText);
    }

    /**
     * 获取UaDeclare所声明出节点的父节点
     *
     * @param ns       命名空间索引
     * @param enumSlot 枚举条目
     * @param baseNode 基父节点
     * @return 父节点
     */
    public static UaNode getParentNode(int ns, UaDeclare enumSlot, UaNode baseNode) {
        if (enumSlot.getParent() != null) {
            if (enumSlot.getParent().getParent() != null) {
                if (enumSlot.getParent().getParent() instanceof UaFolderDeclare) {
                    return getFolder(getParentNode(ns, enumSlot.getParent(), baseNode), new QualifiedName(ns, enumSlot.getParent().name()));
                } else {
                    return getParentNode(ns, enumSlot.getParent(), baseNode).getComponent(new QualifiedName(ns, enumSlot.getParent().name()));
                }
            } else {
                if (enumSlot.getParent() instanceof UaFolderDeclare) {
                    return getFolder(baseNode, new QualifiedName(ns, enumSlot.getParent().name()));
                } else {
                    return baseNode.getComponent(new QualifiedName(ns, enumSlot.getParent().name()));
                }
            }
        } else {
            return baseNode;
        }
    }

    public static UaNode getFolder(UaNode parent, QualifiedName qn) {
        for (UaReference refs : parent.getReferences(Identifiers.HasComponent, false)) {
            UaNode n = refs.getTargetNode();
            if (n != null && qn.equals(n.getBrowseName())) {
                return n;
            }
        }
        return null;
    }


    /**
     * 根据数据类型获得初始值
     *
     * @param dataType  数据类型节点
     * @param arrLength 数组长度，非数组填-1
     * @return 值
     */
    public static Object getInitValue(NodeId dataType, int arrLength, int valueRanks) {
        if (dataType == null)
            throw new NullPointerException();
        if (dataType.equals(Identifiers.String)) {
            if (arrLength == -1) {
                return "null";
            } else {
                String[] ret = new String[arrLength];
                Arrays.fill(ret, "null");
                return ret;
            }

        } else if (dataType.equals(Identifiers.Boolean)) {
            if (arrLength == -1) {
                return false;
            } else {
                Boolean[] ret = new Boolean[arrLength];
                Arrays.fill(ret, false);
                return ret;
            }
        } else if (dataType.equals(Identifiers.Int64)) {
            if (arrLength == -1) {
                return Long.valueOf(0);
            } else {
                Integer[] ret = new Integer[arrLength];
                Arrays.fill(ret, Long.valueOf(0));
                return ret;
            }
        } else if (dataType.equals(Identifiers.Int32)) {
            if (arrLength == -1) {
                return Integer.valueOf(0);
            } else {
                Integer[] ret = new Integer[arrLength];
                Arrays.fill(ret, Integer.valueOf(0));
                return ret;
            }
        } else if (dataType.equals(Identifiers.Int16)) {
            if (arrLength == -1) {
                return Short.valueOf((short) 0);
            } else {
                Short[] ret = new Short[arrLength];
                Arrays.fill(ret, Short.valueOf((short) 0));
                return ret;
            }
        } else if (dataType.equals(Identifiers.Integer)) {
            if (arrLength == -1) {
                return Integer.valueOf(0);
            } else {
                Integer[] ret = new Integer[arrLength];
                Arrays.fill(ret, Integer.valueOf(0));
                return ret;
            }
        } else if (dataType.equals(Identifiers.UInt16)) {
            if (arrLength == -1) {
                return UnsignedShort.valueOf(0);
            } else {
                UnsignedShort[] ret = new UnsignedShort[arrLength];
                Arrays.fill(ret, UnsignedShort.valueOf(0));
                return ret;
            }
        } else if (dataType.equals(Identifiers.UInt32)) {
            if (arrLength == -1) {
                return UnsignedInteger.valueOf(0);
            } else {
                UnsignedInteger[] ret = new UnsignedInteger[arrLength];
                Arrays.fill(ret, UnsignedInteger.valueOf(0));
                return ret;
            }
        } else if (dataType.equals(Identifiers.UInteger)) {
            if (arrLength == -1) {
                return UnsignedInteger.valueOf(0);
            } else {
                UnsignedInteger[] ret = new UnsignedInteger[arrLength];
                Arrays.fill(ret, UnsignedInteger.valueOf(0));
                return ret;
            }

        } else if (dataType.equals(Identifiers.UInt64)) {
            if (arrLength == -1) {
                return UnsignedLong.valueOf(0);
            } else {
                UnsignedLong[] ret = new UnsignedLong[arrLength];
                Arrays.fill(ret, UnsignedLong.valueOf(0));
                return ret;
            }
        }
        else if (dataType.equals(Identifiers.DateTime)||dataType.equals(Identifiers.UtcTime)) {
            if (arrLength == -1) {
                return DateTime.MIN_VALUE;
            } else {
                DateTime[] ret = new DateTime[arrLength];
                Arrays.fill(ret, DateTime.MIN_VALUE);
                return ret;
            }
        }
        else if (dataType.equals(Identifiers.Double)) {
            if (arrLength == -1) {
                return Double.valueOf(-1);
            } else {
                Double[] ret = new Double[arrLength];
                Arrays.fill(ret, Double.valueOf(-1));
                return ret;
            }
        } else if (dataType.equals(Identifiers.Float)) {
            if (arrLength == -1) {
                return Float.valueOf(-1);
            } else {
                Float[] ret = new Float[arrLength];
                Arrays.fill(ret, Float.valueOf(-1));
                return ret;
            }
        } else if (dataType.getNamespaceIndex() != 0) {
            //说明是自定义类型，一般是枚举
            return -1;
        } else if (dataType.equals(Identifiers.NodeId)) {
            return NodeId.NULL;
        } else {
            throw new RuntimeException("not impl init data type id of " + dataType);
        }
    }

    public static Object getInitValue(UaVariableDeclare variableDeclare) {
        VariableDeclareParams conf = variableDeclare.getConfigure();
        if (conf == null) {
            return getInitValue(variableDeclare.getDataType(), variableDeclare.getArrLength(), variableDeclare.getValueRanks());
        } else {
            return getInitValue(conf.getDataType(), conf.getArrLength(), conf.getValueRanks());
        }

    }

    public static Object getInitValue(NodeId dataType) {
        return getInitValue(dataType, defaultConst.arrayLength, defaultConst.valueRanks);
    }

    /**
     * 在父节点中创建所声明的成员
     *
     * @param manager  节点管理器
     * @param baseNode 基节点
     * @param declares UaDeclare[]
     */
    private static void createDeclaredElements(NodeManagerUaNode manager, UaNode baseNode, UaDeclare[] declares, boolean isModel, Map<UaHelperUtil.UaDeclare, UaNode> buff) {
        if (declares.length == 0) {
            return;
        }
        UaDeclare t = declares[0];
        if (t instanceof UaVariableDeclare) {
            createVarNodeInParent(manager, baseNode, (UaVariableDeclare[]) declares, isModel, buff);
        } else if (t instanceof UaMethodDeclare) {
            createMethodInParent(manager, baseNode, (UaMethodDeclare[]) declares, isModel, buff);
        } else if (t instanceof UaFolderDeclare) {
            createFolderInParent(manager, baseNode, (UaFolderDeclare[]) declares, isModel, buff);
        } else {
            throw new RuntimeException("not impl in createDeclaredElements : " + t);
        }
    }

    /**
     * 在父节点中创建变量类型或属性类型节点
     *
     * @param manager  节点管理器
     * @param baseNode 基节点
     * @param ps       UaPropDeclare[] or UaVariableDeclare[]
     */
    private static void createVarNodeInParent(NodeManagerUaNode manager, UaNode baseNode, UaVariableDeclare[] ps, boolean isModel, Map<UaHelperUtil.UaDeclare, UaNode> buff) {
        for (UaVariableDeclare p : ps) {
            String lexKey = getKeyBase(p);
            VariableDeclareParams conf = p.getConfigure();
            boolean write;
            NodeId dataType;
            int rank;
            int arrLength;
            UnsignedInteger[] arrayDimensions;
            if (conf != null) {
                write = conf.getWriteable();
                dataType = conf.getDataType();
                rank = conf.getValueRanks();
                arrLength = conf.getArrLength();
                arrayDimensions = conf.getArrayDimensions();
            } else {
                write = p.getWriteable();
                dataType = p.getDataType();
                rank = p.getValueRanks();
                arrLength = p.getArrLength();
                arrayDimensions = p.getArrayDimensions();
            }
            QualifiedName qualifiedName = new QualifiedName(manager.getNamespaceIndex(), p.name());
            NodeId nid;
            if (isModel) {
                nid = new NodeId(manager.getNamespaceIndex(), p.getId());
            } else {
                nid = manager.createNodeId(baseNode, qualifiedName);
            }
            CacheVariable valueNode;
            boolean isProperty = p instanceof UaPropDeclare;

            LocalizedText localizedText = getLocalizedText(lexKey + lexiconSuffixDisplayName, p.name());
            if (isProperty) {
                valueNode = new CacheProperty(manager, nid, qualifiedName, localizedText);
            } else {
                valueNode = new CacheVariable(manager, nid, qualifiedName, localizedText);
            }

            valueNode.setDescription(getLocalizedText(lexKey + lexiconSuffixDescription, null));
            valueNode.setDataTypeId(dataType);
            valueNode.setAccessLevel(write ? AccessLevelType.of(AccessLevelType.CurrentRead, AccessLevelType.CurrentWrite) : AccessLevelType.CurrentRead);
            if (isModel) {
                valueNode.addModellingRule(ModellingRule.Mandatory);
            }
            if (arrLength != -1) {
                valueNode.setValueRank(rank);
                valueNode.setArrayDimensions(arrayDimensions);
            }

            if (isProperty) {
                getParentNode(manager.getNamespaceIndex(), p, baseNode).addProperty((CacheProperty) valueNode);
            } else {
                getParentNode(manager.getNamespaceIndex(), p, baseNode).addComponent(valueNode);
            }
            if (buff != null) {
                buff.put(p, valueNode);
            }
        }
    }

    /**
     * 在父节点中创建方法节点
     *
     * @param manager  节点管理器
     * @param baseNode 基节点
     * @param ms       UaMethodDeclare[]
     */
    private static void createMethodInParent(NodeManagerUaNode manager, UaNode baseNode, UaMethodDeclare[] ms, boolean isModel, Map<UaHelperUtil.UaDeclare, UaNode> buff) {
        for (UaMethodDeclare m : ms) {
            String lexKey = getKeyBase(m);

            QualifiedName qn = new QualifiedName(manager.getNamespaceIndex(), m.name());
            NodeId id;
            if (isModel) {
                id = new NodeId(manager.getNamespaceIndex(), m.getId());
            } else {
                id = manager.createNodeId(baseNode, qn);
            }

            PlainMethod actionNode = new PlainMethod(manager, id, qn, getLocalizedText(lexKey + lexiconSuffixDisplayName, m.name()));
            if (isModel) {
                actionNode.addReference(ModellingRule.Mandatory.getNodeId(), Identifiers.HasModellingRule, false);
            }
            actionNode.setDescription(getLocalizedText(lexKey + lexiconSuffixDescription, null));

            Argument[] inRaw = m.getInputRawArguments();
            if (inRaw != null) {
                actionNode.setInputArguments(inRaw);
            } else {
                MethodArgument[] inArgus = m.getInputArguments();
                if (inArgus != null && inArgus.length > 0) {
                    Argument[] inputs = new Argument[inArgus.length];
                    for (int i = 0; i < inArgus.length; i++) {
                        inputs[i] = new Argument();
                        inputs[i].setName(getLocalizedText(lexKey + ".input." + inArgus[i].name + lexiconSuffixDisplayName, inArgus[i].name).getText());
                        inputs[i].setDescription(getLocalizedText(lexKey + ".input." + inArgus[i].name + lexiconSuffixDescription, null));
                        inputs[i].setDataType(inArgus[i].dataType);
                        if (inArgus[i].arrLength == -1) {
                            inputs[i].setValueRank(ValueRanks.Scalar);
                        } else {
                            inputs[i].setValueRank(ValueRanks.OneDimension);
                            inputs[i].setArrayDimensions(new UnsignedInteger[]{UnsignedInteger.ZERO});
                        }
                    }
                    actionNode.setInputArguments(inputs);
                }
            }
            Argument[] outRaw = m.getOutputRawArguments();
            if (outRaw != null) {
                actionNode.setOutputArguments(outRaw);
            } else {
                MethodArgument[] outArgus = m.getOutputArguments();
                if (outArgus != null && outArgus.length > 0) {
                    Argument[] outputs = new Argument[outArgus.length];
                    for (int i = 0; i < outArgus.length; i++) {
                        outputs[i] = new Argument();
                        outputs[i].setName(getLocalizedText(lexKey + ".output." + outArgus[i].name + lexiconSuffixDisplayName, outArgus[i].name).getText());
                        outputs[i].setDescription(getLocalizedText(lexKey + ".output." + outArgus[i].name + lexiconSuffixDescription, null));
                        outputs[i].setDataType(outArgus[i].dataType);
                        if (outArgus[i].arrLength == -1) {
                            outputs[i].setValueRank(ValueRanks.Scalar);
                        } else {
                            outputs[i].setValueRank(ValueRanks.OneDimension);
                            outputs[i].setArrayDimensions(new UnsignedInteger[]{UnsignedInteger.ZERO});
                        }
                    }
                    actionNode.setOutputArguments(outputs);
                }
            }
            UaNode parent = getParentNode(manager.getNamespaceIndex(), m, baseNode);
            parent.addComponent(actionNode);
            try {
                createNodeBasedMethod(manager, parent, actionNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (buff != null) {
                buff.put(m, actionNode);
            }
        }
    }

    private static void createNodeBasedMethod(NodeManagerUaNode manager, UaNode parentNode, PlainMethod methodNode) throws MethodArgumentException {
        LCS_NodeBasedMethodNode.Builder builder = new LCS_NodeBasedMethodNode.Builder();
        builder.setManager(manager);
        LCS_NodeBasedMethodNode n = builder.set(methodNode, parentNode).build();
        methodNode.addComponent(n);
    }

    /**
     * 在父节点中创建文件夹节点
     *
     * @param manager  节点管理器
     * @param baseNode 基节点
     * @param fs       UaFolderDeclare[]
     */
    private static void createFolderInParent(NodeManagerUaNode manager, UaNode baseNode, UaFolderDeclare[] fs, boolean isModel, Map<UaHelperUtil.UaDeclare, UaNode> buff) {
        for (UaFolderDeclare f : fs) {
            String lexKey = getKeyBase(f);

            QualifiedName qn = new QualifiedName(manager.getNamespaceIndex(), f.name());
            NodeId nid;

            if (isModel) {
                nid = new NodeId(manager.getNamespaceIndex(), f.getId());
            } else {
                nid = manager.createNodeId(baseNode, qn);
            }


            FolderTypeNode folderNode = manager.createInstance(LCS_Folder.class, nid, qn, getLocalizedText(lexKey + lexiconSuffixDisplayName, f.name()));
            folderNode.setDescription(getLocalizedText(lexKey + lexiconSuffixDescription, null));
            if (isModel) {
                folderNode.addModellingRule(ModellingRule.Mandatory);
            }

            getParentNode(manager.getNamespaceIndex(), f, baseNode).addReference(folderNode, Identifiers.HasComponent, false);
            if (buff != null) {
                buff.put(f, folderNode);
            }
        }
    }

    //获取Lexicon基键
    private static String getKeyBase(UaDeclare v) {
        return getKeyBase(v.getDeclaringClass(), v.name());
    }

    //获取类基键
    public static String getKeyBase(Class<?> cls, String n) {
        String lexKey = cls.getName();
        return lexKey.substring(lexKey.lastIndexOf(".") + 1) + "." + n;
    }

    /**
     * 对象成员声明器基接口
     */
    public interface UaDeclare {
        /**
         * 初始化成员节点到模型中
         *
         * @param manager  节点管理器
         * @param parent   父节点
         * @param declares 成员声明（folder、props、variables、methods）
         */
        static void initModel(NodeManagerUaNode manager, UaNode parent, UaDeclare[]... declares) {
            for (UaDeclare[] declare : declares) {
                createDeclaredElements(manager, parent, declare, true, null);
            }
        }

        /**
         * 初始化成员节点到运行期父节点中
         *
         * @param manager  节点管理器
         * @param parent   父节点
         * @param buff     节点缓存
         * @param declares 成员声明（folder、props、variables、methods）
         */
        static void initNodes(NodeManagerUaNode manager, UaNode parent, Map<UaDeclare, UaNode> buff, UaDeclare[]... declares) {
            for (UaDeclare[] declare : declares) {
                createDeclaredElements(manager, parent, declare, false, buff);
            }
        }


        /**
         * 获取枚举项名称使用，自动重写
         *
         * @return 作为BrowseName, 并作为默认DisplayName
         */
        String name();

        /**
         * 获取枚举实现类，用于生成Lexicon键值
         *
         * @return class
         */
        Class<?> getDeclaringClass();

        /**
         * 获取节点ID
         *
         * @return nodeId
         */
        default int getId() {
            return -1;
        }

        /**
         * 指定父节点，如果设置，则当前成员将会放置在被指定的父节点下
         * 允许合理情形下跨类型，但务必注意生命顺序，父节点必须先于本节点创建
         *
         * @return 枚举条目
         */
        default UaDeclare getParent() {
            return null;
        }

    }

    /**
     * 变量节点生成器接口
     */
    public interface UaVariableDeclare extends UaDeclare {
        /**
         * 获取对象数据类型
         *
         * @return Identifier.XXX 或者自定义枚举类型
         */
        default NodeId getDataType() {
            return defaultConst.dataType;
        }

        /**
         * 指示该变量是否具备写入权限
         *
         * @return true则允许写入
         */
        default boolean getWriteable() {
            return defaultConst.isWriteable;
        }

        /**
         * 节点监听类型
         * 0:自动-为可写入变量且带有父节点时添加NodeListener， 1为强制NodeListener,2为强制DataChangeListener, 3则都添加
         *
         * @return
         */
        default int getListenerType() {
            return defaultConst.listenerType;
        }

        /**
         * 如果是一维数组，则此设定数组长度
         *
         * @return 一维数组长度
         */
        default int getArrLength() {
            return defaultConst.arrayLength;
        }

        /**
         * 数组维度
         *
         * @return
         */
        default int getValueRanks() {
            return defaultConst.valueRanks;
        }

        /**
         * 数组维度参数
         *
         * @return
         */
        default UnsignedInteger[] getArrayDimensions() {
            return defaultConst.arrayDimensions;
        }

        /**
         * 集总方式获取配置参数
         * 此方式与上述独立获取方式只能二选一！
         *
         * @return
         */
        default VariableDeclareParams getConfigure() {
            return null;
        }

    }

    /**
     * 属性节点生成器接口
     */
    public interface UaPropDeclare extends UaVariableDeclare {


    }

    /**
     * 用于声明方法节点使用的接口
     */
    public interface UaMethodDeclare extends UaDeclare {

        /**
         * 获取输入参数
         *
         * @return MethodArgument
         */
        MethodArgument[] getInputArguments();

        /**
         * 获取输出参数
         *
         * @return MethodArgument
         */
        MethodArgument[] getOutputArguments();

        /**
         * 获取原始输入参数
         *
         * @return
         */
        default Argument[] getInputRawArguments() {
            return null;
        }

        /**
         * 获取原始输出参数
         *
         * @return
         */
        default Argument[] getOutputRawArguments() {
            return null;
        }

    }


    /**
     * 用于声明文件夹节点使用的接口
     */
    public interface UaFolderDeclare extends UaDeclare {


    }

    public static class defaultConst {
        public static final NodeId dataType = Identifiers.BaseDataType;
        public static final boolean isWriteable = false;
        public static final int arrayLength = -1;
        //以下参数仅在数组长度非-1时有效
        public static final int valueRanks = ValueRanks.OneDimension;
        public static final UnsignedInteger[] arrayDimensions = new UnsignedInteger[]{UnsignedInteger.ZERO};
        public static int listenerType = 0; //0:自动-为可写入变量且带有父节点时添加NodeListener， 1为强制NodeListener,2为强制DataChangeListener, 3则都添加
    }

    /**
     * 变量类型配置参数集合
     */
    public static class VariableDeclareParams {
        private boolean isWriteable = defaultConst.isWriteable;
        private NodeId dataType = defaultConst.dataType;
        private int arrLength = defaultConst.arrayLength;
        private int valueRanks = defaultConst.valueRanks;
        private UnsignedInteger[] arrayDimensions = defaultConst.arrayDimensions;
        private int listenerType = defaultConst.listenerType;

        public VariableDeclareParams(boolean isWriteable, NodeId dataType, int arrLength, int valueRanks, UnsignedInteger[] arrayDimensions) {
            this(isWriteable, dataType, arrLength, valueRanks);
            this.arrayDimensions = arrayDimensions;
        }

        public VariableDeclareParams(boolean isWriteable, NodeId dataType, int arrLength, int valueRanks) {
            this(isWriteable, dataType, arrLength);
            this.valueRanks = valueRanks;
        }

        public VariableDeclareParams(boolean isWriteable, NodeId dataType, int arrLength) {
            this(isWriteable, dataType);
            this.arrLength = arrLength;
        }

        public VariableDeclareParams(boolean isWriteable, NodeId dataType) {
            this.isWriteable = isWriteable;
            this.dataType = dataType;
        }

        public NodeId getDataType() {
            return this.dataType;
        }

        public boolean getWriteable() {
            return this.isWriteable;
        }

        public int getArrLength() {
            return this.arrLength;
        }

        public int getValueRanks() {
            return this.valueRanks;
        }

        public UnsignedInteger[] getArrayDimensions() {
            return this.arrayDimensions;
        }

        public int getListenerType() {
            return this.listenerType;
        }

        public VariableDeclareParams setListenerType(int listenerType) {
            this.listenerType = listenerType;
            return this;
        }
    }

    /**
     * 方法参数声明接口
     */
    public static class MethodArgument {
        public static final MethodArgument PASSWORD = new MethodArgument("password", Identifiers.String);
        public static final MethodArgument STR_RESULT = new MethodArgument("result", Identifiers.String);
        public static final MethodArgument NODE_ID = new MethodArgument("NodeId", Identifiers.NodeId);

        public final String name;
        private final NodeId dataType;
        public int arrLength = defaultConst.arrayLength;

        public MethodArgument(String name, NodeId dataType) {
            this.name = name;
            this.dataType = dataType;
        }

        public MethodArgument(String name, NodeId dataType, int arrLength) {
            this(name, dataType);
            this.arrLength = arrLength;
        }
    }


}
