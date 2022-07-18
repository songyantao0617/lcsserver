package com.pxccn.PxcDali2.server.space.cockpit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.server.service.V3RoomUpdateService;
import com.pxccn.PxcDali2.server.service.db.CabinetQueryService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.cabinets.Cabinet;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
public class Cockpit extends FwUaComponent<Cockpit.LcsCockpitNode> {

    @Autowired
    V3RoomUpdateService v3RoomUpdateService;
    @Autowired
    CabinetsManager cabinetsManager;

    @Autowired
    CabinetQueryService cabinetQueryService;

    public void onSyncAllOnlineCabinetDb(int flag) {
        log.trace("onSyncAllOnlineCabinetDb: flag={}", flag);
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(c -> {
            Futures.addCallback(c.sendDbSync(flag), new FutureCallback<>() {
                @Override
                public void onSuccess(AsyncActionFeedbackWrapper.DbSync result) {

                }

                @Override
                public void onFailure(Throwable t) {

                }
            }, MoreExecutors.directExecutor());
        });
    }

    public void onSendCtlCommandToAllOnlineCabinet(UUID[] resource,
                                                   Dali2LightCommandModel dali2LightCommandModel,
                                                   Dt8CommandModel dt8CommandModel) {
        log.trace("onSendCtlCommandToAllOnlineCabinet: resource={},dali2LightCommandModel={},dt8CommandModel={}", resource, dali2LightCommandModel, dt8CommandModel);
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(c -> {
            Futures.addCallback(c.sendCtlCommand(resource, dali2LightCommandModel, dt8CommandModel), new FutureCallback<>() {
                @Override
                public void onSuccess(AsyncActionFeedbackWrapper.SendLevelInstruction result) {

                }

                @Override
                public void onFailure(Throwable t) {

                }
            }, MoreExecutors.directExecutor());
        });
    }

    public void onSaveAllOnlineStation() {
        log.trace(logStr("onSaveAllOnlineStation"));
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(c -> {
            Futures.addCallback(c.saveStation(), new FutureCallback<>() {
                @Override
                public void onSuccess(AsyncActionFeedbackWrapper.SaveStation result) {

                }

                @Override
                public void onFailure(Throwable t) {

                }
            }, MoreExecutors.directExecutor());
        });
    }

    public void syncAllOnlineCabinetV3Room(){
        log.trace(logStr("syncAllOnlineCabinetV3Room"));
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(Cabinet::executeV3Sync);
    }

    @Override
    protected LcsCockpitNode createUaNode() {
        return new LcsCockpitNode(this, this.getName());
    }

    protected static class LcsCockpitNode extends LCS_ComponentFastObjectNode {
        Cockpit comp;

        protected LcsCockpitNode(Cockpit uaComponent, String qname) {
            super(uaComponent, qname);
            this.comp = uaComponent;
            addAdditionalDeclares(methods.values());
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.syncAllOnlineCabinetDb) {
                int flag = input[0].intValue();
                this.comp.onSyncAllOnlineCabinetDb(flag);
                return null;
            } else if (declared == methods.saveAllOnlineStation) {
                this.comp.onSaveAllOnlineStation();
                return null;
            } else if (declared == methods.sendCtlCommandToAllOnlineCabinet) {
                var targets = Util.ResolveUUIDArrayFromString(input[0].toString());
                var Cmd103 = new Dali2LightCommandModel(UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class, input[1].intValue()), input[2].intValue());
                var CmdDt8 = new Dt8CommandModel(UaHelperUtil.getEnum(Dt8CommandModel.Instructions.class, input[3].intValue()), input[4].intValue(), input[5].intValue());
                this.comp.onSendCtlCommandToAllOnlineCabinet(targets, Cmd103, CmdDt8);
                return null;
            }else if(declared == methods.innerTest){
//                this.comp.v3RoomUpdateService.update(14598041);
                this.comp.cabinetQueryService.clearV3RoomUpdateFlag(UUID.fromString("1713114F-0000-46A9-B2D2-58A79204AAAA"));
                return null;
            }else if(declared == methods.syncAllOnlineCabinetV3Room){
                this.comp.syncAllOnlineCabinetV3Room();
                return null;
            }

//            else if(declared == methods.testEnum){
//                var s = UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class,input[0].intValue());
//                log.info(s.toString());
//                return null;
//            }
            return super.onMethodCall(declared, input);
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            innerTest,

            syncAllOnlineCabinetV3Room,
            saveAllOnlineStation,
            syncAllOnlineCabinetDb(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("flag", Identifiers.Int32)}, null),
            sendCtlCommandToAllOnlineCabinet(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("uuid", Identifiers.String),
                    new UaHelperUtil.MethodArgument("action", Dali2LightCommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("parameter", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8Action", Dt8CommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("dt8ActionParam", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8ActionParam2", Identifiers.Int32),
            }, null);
            //            testEnum(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("dsds", Dali2LightCommandModel.Instructions.class)},null);
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
