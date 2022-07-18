package com.pxccn.PxcDali2.server.space.ua;

import com.prosysopc.ua.stack.core.Identifiers;
import com.pxccn.PxcDali2.server.framework.FwContext;
import com.pxccn.PxcDali2.server.framework.FwProperty;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FwUaComponent<T extends LCS_ComponentFastObjectNode> extends FwBaseUaComponent<T> {


    protected void createNodeInternal() {
        synchronized (this) {
            if (this._thisNode == null) {
                var node = this.createUaNode();
                this.getParentNode().addComponent(node.init());
                this.getParentNode().addReference(node, Identifiers.HasNotifier, false);
                this._thisNode = node;
                this.afterUaNodeCreated();
            }
        }
    }

    public void onChanged(FwProperty property, FwContext context) {
        this.getNode().ownerChanged(property);
    }

}
