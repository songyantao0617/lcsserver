package com.pxccn.PxcDali2.server.space.cockpit;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.asyncResp.AsyncActionFeedbackWrapper;
import com.pxccn.PxcDali2.Util;
import com.pxccn.PxcDali2.server.service.db.DatabaseService;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.service.ota.OtaService;
import com.pxccn.PxcDali2.server.space.cabinets.Cabinet;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.lights.LightsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Cockpit extends FwUaComponent<Cockpit.LcsCockpitNode> {
    @Autowired
    CabinetsManager cabinetsManager;
    @Autowired
    LightsManager lightsManager;
    @Autowired
    DatabaseService databaseService;

    @Autowired
    OtaService otaService;

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

    public void syncAllOnlineCabinetV3Room() {
        log.trace(logStr("syncAllOnlineCabinetV3Room"));
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(Cabinet::executeV3Sync);
    }

    public void updateAllOnlineCabinetLightsAndRoomsInfo() {
        log.trace(logStr("syncAllOnlineCabinetV3Room"));
        cabinetsManager.updateAllOnlineCabinetLightsAndRoomsInfo();
    }

    public void autoRepairAllOnlineCabinets() {
        log.trace(logStr("autoRepairAllOnlineCabinets"));
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(Cabinet::AutoRepair);
    }

    public void sendAllOnlineLightsGroupAddress() {
        log.trace(logStr("sendAllOnlineLightsGroupAddress"));
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(Cabinet::sendAllLightsGroupAddress);
    }



    /**
     * 批量修改灯具名称（基于JSON方式）
     * {
     * "UUID1":"newName1",
     * "UUID2":"newName2"
     * }
     *
     * @param json
     */
    public void batchChangeLightName(JsonObject json) {
        Map<UUID, String> parameters = new HashMap<>();
        json.entrySet().forEach(entry -> {
            try {
                UUID lightUuid = UUID.fromString(entry.getKey());
                String name = entry.getValue().getAsString();
                parameters.put(lightUuid, name);
            } catch (Exception ignore) {

            }
        });
        if (!parameters.isEmpty()) {
            this.batchChangeLightName(parameters);
        }
    }

    /**
     * 批量修改灯具名称
     *
     * @param changeMap
     */
    public void batchChangeLightName(Map<UUID, String> changeMap) {
        Assert.notNull(changeMap, "changeMap");
        log.trace(logStr("batchChangeLightName:{}", changeMap));
        Map<Cabinet, Map<UUID, String>> sortByCabinet = new HashMap<>();
        List<UUID> unknownLights = new ArrayList<>();
        changeMap.forEach((key, value) -> {
            var light = lightsManager.getLightByUUID(key);
            if (light == null) {
                unknownLights.add(key);
                return;
            }
            var cabinet = light.getCabinet();
            var thisCabinetMap = sortByCabinet.computeIfAbsent(cabinet, i -> new HashMap<>());
            thisCabinetMap.put(key, value);
        });

        sortByCabinet.forEach((cab, param) -> {
            cab.changeLightName(param);
        });

    }

    public void writeAllOnlineCabinetNiagaraPlatformPropertyBySlotOrd(String slotPath, String value) {
        log.trace(logStr("writeAllOnlineCabinetNiagaraPlatformPropertyBySlotOrd: slotPath={},value={}", slotPath, value));
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(i -> {
            i.WriteNiagaraPlatformProperty(slotPath, value);
        });
    }

    public void invokeAllOnlineCabinetNiagaraPlatformMethod(String methodOrd, String arg) {
        log.trace(logStr("invokeAllOnlineCabinetNiagaraPlatformMethod: methodOrd={},arg={}",methodOrd,arg));
        Arrays.stream(cabinetsManager.getAllOnlineCabinet()).forEach(i -> {
            i.InvokeCabinetNiagaraPlatformMethod(methodOrd, arg);
        });
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
            } else if (declared == methods._innerTest) {
//                this.comp.v3RoomUpdateService.update(14598041);
                this.comp.databaseService.clearV3RoomUpdateFlag(UUID.fromString("1713114F-0000-46A9-B2D2-58A79204AAAA"));
                return null;
            } else if (declared == methods.syncAllOnlineCabinetV3Room) {
                this.comp.syncAllOnlineCabinetV3Room();
                return null;
            } else if (declared == methods.updateAllOnlineCabinetLightsAndRoomsInfo) {
                this.comp.updateAllOnlineCabinetLightsAndRoomsInfo();
                return null;
            } else if (declared == methods._changeLightsName) {
                this.comp.batchChangeLightName(new JsonParser().parse(input[0].toString()).getAsJsonObject());
                return null;
            } else if (declared == methods.writeAllOnlineCabinetNiagaraPlatformPropertyBySlotOrd) {
                this.comp.writeAllOnlineCabinetNiagaraPlatformPropertyBySlotOrd(input[0].toString(), input[1].toString());
                return null;
            } else if (declared == methods._ota) {
                return Variant.asVariantArray(this.comp.otaService.OTA_Update(
                        input[0].toString(),
                        StringUtils.commaDelimitedListToSet(input[1].toString()).stream().map(Integer::valueOf).collect(Collectors.toSet()),
                        input[2].booleanValue(),
                        input[3].booleanValue()
                ));
            } else if (declared == methods.autoRepairAllOnlineCabinets) {
                this.comp.autoRepairAllOnlineCabinets();
                return null;
            } else if (declared == methods.sendAllOnlineLightsGroupAddress) {
                this.comp.sendAllOnlineLightsGroupAddress();
                return null;
            } else if (declared == methods.invokeAllOnlineCabinetNiagaraPlatformMethod) {
                this.comp.invokeAllOnlineCabinetNiagaraPlatformMethod(input[0].toString(),input[1].booleanValue()?input[2].toString():null);
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
            sendAllOnlineLightsGroupAddress,
            _ota(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("oldVersion", Identifiers.String),
                    new UaHelperUtil.MethodArgument("selectCommaList", Identifiers.String),
                    new UaHelperUtil.MethodArgument("restart", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("test", Identifiers.Boolean),
            }, new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("count", Identifiers.Integer),}),
            autoRepairAllOnlineCabinets(),
            _changeLightsName(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("json", Identifiers.String)}, null),
            _innerTest,
            updateAllOnlineCabinetLightsAndRoomsInfo,
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
            }, null),
            writeAllOnlineCabinetNiagaraPlatformPropertyBySlotOrd(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("slotOrd", Identifiers.String),
                    new UaHelperUtil.MethodArgument("value", Identifiers.String),
            }, null),
            invokeAllOnlineCabinetNiagaraPlatformMethod(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("methodOrd", Identifiers.String),
                    new UaHelperUtil.MethodArgument("haveArg", Identifiers.Boolean),
                    new UaHelperUtil.MethodArgument("arg", Identifiers.String),
            }, null),
            ;
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
