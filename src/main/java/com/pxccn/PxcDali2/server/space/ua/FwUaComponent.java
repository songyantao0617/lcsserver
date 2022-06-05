package com.pxccn.PxcDali2.server.space.ua;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.prosysopc.ua.server.nodes.BaseNode;
import com.prosysopc.ua.server.nodes.UaObjectNode;
import com.pxccn.PxcDali2.server.framework.FwComponent;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.opcua.OpcuaServer;
import com.pxccn.PxcDali2.server.opcua.type.LCS_ComponentFastObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class FwUaComponent<T extends LCS_ComponentFastObjectNode> extends FwBaseUaComponent<T> {

    protected void beforePropStart(){
        var node = this.createUaNode();
        this.getParentNode().addComponent(node.init());
        this._thisNode = node;
    }

    public void onChanged(FwProperty property, FwContext context) {
        this.getNode().ownerChanged(property);
    }

}
