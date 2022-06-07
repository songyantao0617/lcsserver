package com.pxccn.PxcDali2.server.framework;

import com.pxccn.PxcDali2.server.framework.Exception.FwRuntimeException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

//@Service
@Slf4j
public class TestComp extends FwComponent {
    FwProperty<Integer> Field1 = addProperty(1, "Field1");
    FwProperty<Integer> Field2 = addProperty(1, "Field2");
    FwProperty<Integer> Field3 = addProperty(1, "Field3");

    public TestComp() throws FwRuntimeException {
    }

    @PostConstruct
    public void started() {
        this.start();
    }


    public void onChanged(FwProperty property, FwContext context) {
        if (property == Field1) {
            log.error("Field1 - > " + this.Field1.get());
        }
    }


}
