package com.pxccn.PxcDali2.server.space.cockpit;

import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
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
        }

    }
}
