package com.pxccn.PxcDali2.server.space.cockpit;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.prosysopc.ua.stack.core.StatusCodes;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
//import com.pxccn.PxcDali2.server.service.opcua.type.enums.UaLightCommandEnum;
import com.pxccn.PxcDali2.server.space.lights.Dali2Light;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Cockpit extends FwUaComponent<Cockpit.LcsCockpitNode> {

    @Override
    protected LcsCockpitNode createUaNode() {
        return new LcsCockpitNode(this,this.getName());
    }

    protected static class LcsCockpitNode extends LCS_ComponentFastObjectNode {

        protected LcsCockpitNode(Cockpit uaComponent, String qname) {
            super(uaComponent, qname);
            addAdditionalDeclares(methods.values());
        }
        private enum methods implements UaHelperUtil.UaMethodDeclare {
            syncDb(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("newAddress", Identifiers.Int32)}, null),
            testEnum(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("dsds", Dali2LightCommandModel.Instructions.class)},null);
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
            if (declared == methods.syncDb) {
                int newAddress = input[0].intValue();
                //
                return null;
            }else if(declared == methods.testEnum){
                var s = UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class,input[0].intValue());
                log.info(s.toString());
                return null;
            }
            return super.onMethodCall(declared, input);
        }

    }
}
