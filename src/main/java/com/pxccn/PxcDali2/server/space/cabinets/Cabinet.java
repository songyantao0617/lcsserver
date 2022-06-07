package com.pxccn.PxcDali2.server.space.cabinets;

import com.prosysopc.ua.nodes.UaNode;
import com.pxccn.PxcDali2.MqSharePack.wrapper.toServer.CabinetStatusWrapper;
import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.events.CabinetAliveChangedEvent;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;

@FwComponentAnnotation
@Slf4j
public class Cabinet extends FwUaComponent<Cabinet.CabinetNode> {

    @Autowired
    ConfigurableApplicationContext context;


    FwProperty<String> Name;
    FwProperty<Integer> CabinetId;
    FwProperty<Props> Props;
    FwProperty<Boolean> IsAlive;
    FwProperty<Long> LastConnectionLostTimeStamp;
    FwProperty<Long> LastConnectionEstablishTimeStamp;

    @PostConstruct
    public void post() {
        Name = addProperty("name..", "CabinetName");
        CabinetId = addProperty(0, "CabinetId");
        Props = addProperty(context.getBean(Props.class), "Props");
        IsAlive = addProperty(false, "IsAlive");
        LastConnectionLostTimeStamp = addProperty(0L, "LastConnectionLostTimeStamp");
        LastConnectionEstablishTimeStamp = addProperty(0L, "LastConnectionEstablishTimeStamp");
    }

    private long _lastMsgTimestamp = System.currentTimeMillis();

    public void checkIsNotAlive() {
        if (System.currentTimeMillis() - this._lastMsgTimestamp > 30000) {
            this.IsAlive.set(false);
        }
    }

    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property, context);
        if (property == IsAlive) {
            var tp = System.currentTimeMillis();
            if (IsAlive.get()) {
                log.info("控制柜<{}>上线！", this.CabinetId.get());
                this.LastConnectionEstablishTimeStamp.set(tp);
            } else {
                log.error("控制柜<{}>离线！", this.CabinetId.get());
                this.LastConnectionLostTimeStamp.set(tp);
            }
            this.context.publishEvent(new CabinetAliveChangedEvent(this, this.IsAlive.get(), tp));
        }
    }


    public void updateStatus(CabinetStatusWrapper status) {
        this.Props.get().accept(status.getPropsMap());
        this._lastMsgTimestamp = System.currentTimeMillis();
        this.IsAlive.set(true);
    }


    public void setCid(int cid) {
        this.CabinetId.set(cid);
    }

    public CabinetsManager getCabinetManager() {
        return (CabinetsManager) this.getParentComponent();
    }

    public UaNode getParentNode() {
        return this.getCabinetManager().getCabinetsFolderNode();
    }

    @Override
    protected CabinetNode createUaNode() {
        return new CabinetNode(this, this.getName());//控制柜节点
    }

    protected static class CabinetNode extends LCS_ComponentFastObjectNode {

        protected CabinetNode(Cabinet uaComponent, String qname) {
            super(uaComponent, qname);
            addProperty(uaComponent.Name);
            addProperty(uaComponent.CabinetId);
            addProperty(uaComponent.IsAlive);
            addProperty(uaComponent.LastConnectionLostTimeStamp);
            addProperty(uaComponent.LastConnectionEstablishTimeStamp);
        }
    }

}
