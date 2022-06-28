package com.pxccn.PxcDali2.server.space.rooms;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.Variant;
import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.MqSharePack.model.Dali2LightCommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.Dt8CommandModel;
import com.pxccn.PxcDali2.MqSharePack.model.RoomDetailModel;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.UaHelperUtil;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.cabinets.Cabinet;
import com.pxccn.PxcDali2.server.space.cabinets.CabinetsManager;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.UUID;

@FwComponentAnnotation
@Slf4j
public class Room extends FwUaComponent<Room.RoomNode> {
    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    RoomsManager roomsManager;

    @Autowired
    CabinetsManager cabinetsManager;

    FwProperty<String> roomName;
    FwProperty<Integer> cabinetId;
    FwProperty<String> description;
    FwProperty<Integer> axis_x;
    FwProperty<Integer> axis_y;
    FwProperty<Integer> axis_z;
    FwProperty<Long> lastInfosUploadedTimestamp;
    UUID roomUuid;

    @PostConstruct
    public void post() {
        roomName = addProperty("name..", "roomName");
        cabinetId = addProperty(0, "cabinetId");
        description = addProperty("description", "description");
        axis_x = addProperty(0, "axis_x");
        axis_y = addProperty(0, "axis_y");
        axis_z = addProperty(0, "axis_z");
        lastInfosUploadedTimestamp = addProperty(0L, "lastInfosUploadedTimestamp");
    }

    public UUID getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(UUID roomUuid) {
        this.roomUuid = roomUuid;
    }

    public int getCabinetId() {
        return this.cabinetId.get();
    }

    public void setCabinetId(int cabinetId) {
        this.cabinetId.set(cabinetId);
    }

    public RoomsManager getRoomsManager() {
        return (RoomsManager) this.getParentComponent();
    }

    @Override
    public UaNode getParentNode() {
        return this.getRoomsManager().getRoomsFolderNode();
    }

    public void onDetailUpload(RoomDetailModel model) {
        this.roomName.set(model.roomName);
        this.description.set(model.description);
        this.axis_x.set(model.axis_x);
        this.axis_y.set(model.axis_y);
        this.axis_z.set(model.axis_z);
        this.lastInfosUploadedTimestamp.set(System.currentTimeMillis());

    }

    public Cabinet getCabinet() {
        return cabinetsManager.GetOrCreateCabinet(this.cabinetId.get());
    }

    private void onSendCtlCommand(Dali2LightCommandModel dali2LightCommandModel, Dt8CommandModel dt8CommandModel) {
        log.trace("<{}> onSendCtlCommand: dali2LightCommandModel={},dt8CommandModel={}", roomName.get(), dali2LightCommandModel, dt8CommandModel);
        this.getCabinet().onSendCtlCommand(new UUID[]{this.getRoomUuid()}, dali2LightCommandModel, dt8CommandModel);
    }

    @Override
    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
        if (property == this.roomName) {
            this.getNode().setDisplayName(new LocalizedText(this.roomName.get()));
        }
    }

    public void onFetchDetailsNow() {
        log.info("房间<{}>数据上传执行", this.roomUuid);
        this.getRoomsManager().AskToUpdateRoomsInfo(Collections.singletonList(this.roomUuid), cabinetId.get());
    }

    @Override
    protected RoomNode createUaNode() {
        return new RoomNode(this, this.getName());
    }

    protected static class RoomNode extends LCS_ComponentFastObjectNode {
        Room comp;

        protected RoomNode(Room uaComponent, String qname) {
            super(uaComponent, qname);
            this.comp = uaComponent;
            addProperty(uaComponent.roomName);
            addProperty(uaComponent.cabinetId);
            addProperty(uaComponent.description);
            addProperty(uaComponent.axis_x);
            addProperty(uaComponent.axis_y);
            addProperty(uaComponent.axis_z);
            addProperty(uaComponent.lastInfosUploadedTimestamp);
            addAdditionalDeclares(methods.values());
        }

        protected Variant[] onMethodCall(UaHelperUtil.UaMethodDeclare declared, Variant[] input) throws StatusException {
            if (declared == methods.blink) {
//                comp.blink(BBoolean.make(input[0].booleanValue()));
                return null;
            } else if (declared == methods.fetchDetailsNow) {
                this.comp.onFetchDetailsNow();
                return null;
            }
            if (declared == methods.sendCtlCommand) {
                var Cmd103 = new Dali2LightCommandModel(UaHelperUtil.getEnum(Dali2LightCommandModel.Instructions.class, input[0].intValue()), input[1].intValue());
                var CmdDt8 = new Dt8CommandModel(UaHelperUtil.getEnum(Dt8CommandModel.Instructions.class, input[2].intValue()), input[3].intValue(), input[4].intValue());
                this.comp.onSendCtlCommand(Cmd103, CmdDt8);
                return null;
            }
            return super.onMethodCall(declared, input);
        }

        private enum methods implements UaHelperUtil.UaMethodDeclare {
            sendCtlCommand(new UaHelperUtil.MethodArgument[]{
                    new UaHelperUtil.MethodArgument("action", Dali2LightCommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("parameter", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8Action", Dt8CommandModel.Instructions.class),
                    new UaHelperUtil.MethodArgument("dt8ActionParam", Identifiers.Int32),
                    new UaHelperUtil.MethodArgument("dt8ActionParam2", Identifiers.Int32),
            }, null),
            blink(new UaHelperUtil.MethodArgument[]{new UaHelperUtil.MethodArgument("enable", Identifiers.Boolean)}, null),
            fetchDetailsNow();
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
