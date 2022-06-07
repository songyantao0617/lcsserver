package com.pxccn.PxcDali2.server.space.cabinets;

import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;

import java.util.Arrays;
import java.util.Map;

@FwComponentAnnotation
public class Props extends FwUaComponent<Props.CabinetStatusNode> {
    public void accept(Map<String, String> propMap) {
        propMap.forEach((k, v) -> {
            var p = this.getProperty(k);
            if (p != null) {
                p.set(v);
            }
        });
    }

    private static final String[] resourceItems = {
            "resources.category.network",
            "resources.category.history",
            "resources.category.proxyExt",
            "resources.category.alarm",
            "resources.category.component",
            "resources.category.device",
            "resources.total",
            "resources.limit",
            "heap.used",
            "heap.free",
            "heap.max",
            "heap.total",
            "mem.used",
            "mem.total",
            "cpu.usage",
            "engine.scan.lifetime",
            "engine.scan.recent",
            "engine.scan.usage",
            "engine.scan.peak",
            "engine.scan.timeOfPeak",
            "engine.scan.timeOfPeakInterscan",
            "engine.scan.peakInterscan",
            "engine.queue.actions",
            "engine.queue.longTimers",
            "engine.queue.mediumTimers",
            "engine.queue.shortTimers",
            "time.uptime",
            "time.start",
            "time.current",
            "version.java",
            "version.niagara",
            "version.os",
            "component.count",
            "node.count",
            "globalCapacity.devices",
            "globalCapacity.points",
    };

    public void start() {
        Arrays.stream(resourceItems).forEach(tag -> {
            addProperty("", tag);
        });
        super.start();
    }


    @Override
    protected CabinetStatusNode createUaNode() {
        return new CabinetStatusNode(this, this.getName());
    }

    protected static class CabinetStatusNode extends LCS_ComponentFastObjectNode {

        protected CabinetStatusNode(FwUaComponent uaComponent, String qname) {
            super(uaComponent, qname);
            Arrays.stream(resourceItems).forEach(tag -> {
                addProperty(uaComponent.getProperty(tag));
            });
        }
    }

}
