package com.pxccn.PxcDali2.server.space.ua;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.nodes.UaNode;
import com.pxccn.PxcDali2.server.framework.FwComponent;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.opcua.LcsNodeManager;
import com.pxccn.PxcDali2.server.opcua.OpcuaServer;
import com.pxccn.PxcDali2.server.opcua.type.LCS_ComponentFastObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class FwBaseUaComponent<T extends UaNode> extends FwComponent {

    @Autowired
    OpcuaServer opcuaServer;

    public OpcuaServer getOpcuaServer(){
        return this.opcuaServer;
    }

    public void descendantsStopped() {
        try {
            opcuaServer.getLcsNodeManager().deleteNode(this.getNode(), true, true);
            log.debug("node of component '{}' deleted", this.getName());
        } catch (StatusException e) {
            log.error("Error to delete node of component '{}'", this.getName(), e);
        }
    }

    protected void beforePropStart(){
        var node = this.createUaNode();
        this.getParentNode().addComponent(node);
        this._thisNode = node;
    }


    public void onChanged(FwProperty property, FwContext context) {
        super.onChanged(property,context);
    }

    T _thisNode;

    protected abstract T createUaNode();

    public T getNode() {
        return _thisNode;
    }

    public UaNode getParentNode() {
        FwComponent p = this.getParentComponent();

        if (p instanceof FwBaseUaComponent) {
            return ((FwBaseUaComponent<?>) p).getNode();
        }

        while ((p = p.getParentComponent()) != null) {
            if (p instanceof FwBaseUaComponent) {
                return ((FwBaseUaComponent<?>) p).getNode();
            }
        }

        return LcsNodeManager.objectsFolder;
    }

}
