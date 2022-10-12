package com.pxccn.PxcDali2.server.space.cabinets;

import com.pxccn.PxcDali2.common.annotation.FwComponentAnnotation;
import com.pxccn.PxcDali2.server.service.opcua.type.LCS_ComponentFastObjectNode;
import com.pxccn.PxcDali2.server.space.ua.FwUaComponent;

import java.util.Arrays;
import java.util.Map;

/**
 * 控制柜遥测参数
 */
@FwComponentAnnotation
public class Props extends FwUaComponent<Props.CabinetStatusNode> {
    private static final String[] resourceItems = {
            "heap.used",
            "cpu.usage",
            "engine.scan.timeOfPeakInterscan",
            "engine.scan.timeOfPeak",
            "engine.queue.longTimers",
            "time.uptime",
            "engine.queue.shortTimers",
            "time.current",
            "engine.queue.mediumTimers",
            "engine.scan.peak",
            "engine.scan.lifetime",
            "time.start",
            "engine.queue.actions",
            "engine.scan.usage",
            "engine.scan.recent",
            "engine.scan.peakInterscan"
    };

    public String getCpuUsage() {
        var n = this.getProperty("cpu.usage");
        if (n != null) {
            return (String) n.get();
        } else {
            return "";
        }
    }

    public String getUptime() {
        var n = this.getProperty("time.uptime");
        if (n != null) {
            return (String) n.get();
        } else {
            return "";
        }
    }

    public void accept(Map<String, String> propMap) {
        propMap.forEach((k, v) -> {
            var p = this.getProperty(k);
            if (p != null) {
                p.set(v);
            }
        });
    }

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
