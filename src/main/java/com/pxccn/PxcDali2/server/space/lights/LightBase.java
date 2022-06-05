package com.pxccn.PxcDali2.server.space.lights;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.MqSharePack.model.LightRealtimeStatusModel;
import com.pxccn.PxcDali2.server.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import com.pxccn.PxcDali2.server.util.annotation.FwComponentAnnotation;

import javax.annotation.PostConstruct;
import java.util.UUID;

@FwComponentAnnotation
public class LightBase extends FwUaComponent<LightBase.LCS_LightBaseNode> {

    FwProperty<String> Name;
    UUID lightUuid;


    public void onNewStatus(LightRealtimeStatusModel lrs) {

    }


    public void setUuid(UUID id) {
        this.lightUuid = id;
    }

    @PostConstruct
    public void post() {
        Name = addProperty("nameee", "Name");
    }

    public LightsManager getLightManager() {
        return (LightsManager) this.getParentComponent();
    }

    public UaNode getParentNode() {
        return this.getLightManager().getLightsFolderNode();
    }


    @Override
    protected LCS_LightBaseNode createUaNode() {
        return new LCS_LightBaseNode(this, this.getName(), this.getName());
    }

    protected static class LCS_LightBaseNode extends LCS_ComponentFastObjectNode {
        LightBase comp;

        protected LCS_LightBaseNode(LightBase comp, String qualifiedName, String objLocalizedText) {
            super(comp, qualifiedName, objLocalizedText);
            this.comp = comp;
            addProperty(comp.Name);
            addAdditionalDeclares(methods.values());
        }

        public void ownerChanged(FwProperty property) {
            super.ownerChanged(property);
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
    }
}
