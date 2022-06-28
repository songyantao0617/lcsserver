package com.pxccn.PxcDali2.server.space.ua;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;

public class FolderNodeComponent extends FwUaComponent<FolderNodeComponent.LCS_LightBaseNode> {

    FwProperty<Integer> F1 = addProperty(123, "F1");

    @Override
    protected LCS_LightBaseNode createUaNode() {
        return new LCS_LightBaseNode(this, this.getName(), "ss");
    }


    protected static class LCS_LightBaseNode extends LCS_ComponentFastObjectNode {
        FolderNodeComponent comp;

        protected LCS_LightBaseNode(FolderNodeComponent comp, String qualifiedName, String objLocalizedText) {
            super(comp, qualifiedName, objLocalizedText);
            this.comp = comp;
            addProperty(comp.F1);
            addAdditionalDeclares(methods.values());
        }

        public void ownerChanged(FwProperty property) {
            super.ownerChanged(property);
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.blink) {
//                comp.blink(BBoolean.make(input[0].booleanValue()));
                return null;
            }


//            else if (declared == methods.removeThisLight) {
//                comp.removeThisLight();
//                return null;
//            }
            return super.onMethodCall(declared, input);
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            blink(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("enable", Identifiers.Boolean)}, null);
//            removeThisLight();

            private UaHelperUtil.MethodArgument[] in = null;
            private UaHelperUtil.MethodArgument[] out = null;

            methods(UaHelperUtil.MethodArgument[] inputArguments, UaHelperUtil.MethodArgument[] outputArguments) {
                this();
                this.in = inputArguments;
                this.out = outputArguments;
            }

            methods() {
            }

            @Override
            public UaHelperUtil.MethodArgument[] getInputArguments() {
                return this.in;
            }

            @Override
            public UaHelperUtil.MethodArgument[] getOutputArguments() {
                return this.out;
            }
        }
    }
}
