package com.pxccn.PxcDali2.server.framework;

import com.pxccn.PxcDali2.common.ArrayUtil;
import com.pxccn.PxcDali2.server.framework.Exception.FwAlreadyStoppedException;
import com.pxccn.PxcDali2.server.framework.Exception.FwRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class FwComponent extends FwObject implements IFwCompLifecycle {

    @Autowired
    protected ConfigurableApplicationContext context;

    WeakReference<FwComponent> _parent;
    private Map<String, FwProperty> _props = new ConcurrentHashMap<>();
    private boolean _stopping = false;
    private boolean _running = false;

    public FwComponent getParentComponent() {
        if (this._parent == null) {
            return null;
        }
        return this._parent.get();
    }

    public boolean isRunning() {
        return this._running;
    }

    protected void checkRunning() throws FwAlreadyStoppedException {
        if (!this._running) {
            throw new FwAlreadyStoppedException("Component " + this.getName() + " already stopped!");
        }
    }

    protected <T> FwProperty<T> addProperty(T defaultValue, String name) {
        return this.addProperty(defaultValue, name, null);
    }

    protected <T> FwProperty<T> addProperty(T defaultValue, String name, FwPropFlag flag) {
        if (name.endsWith("?")) {
            name = this.generateUniqueSlotName(name);
        } else {
            if (this._props.containsKey(name)) {
                var msg = MessageFormatter.arrayFormat("property name '{}' already exist in component '{}'", new Object[]{
                        name,
                        this.getName()
                }).getMessage();
                throw new FwRuntimeException(msg);
            }
        }

        var ret = FwProperty.Declear(this, defaultValue, name, flag);
        this._props.put(name, ret);
        if (this._running) {
            ret.start();
        }
        return ret;
    }

    public FwProperty getProperty(String name) {
        return _props.getOrDefault(name, null);
    }

    public FwProperty[] getAllProperty() {
        return this._props.values().toArray(new FwProperty[0]);
    }

    public FwProperty[] getAllProperty(Class<?> childCls) {
        return this.getAllProperty(childCls, true);
    }

    public FwProperty[] getAllProperty(Class<?> childCls, boolean strict) {
        if (strict) {
            return Arrays.stream(this.getAllProperty()).filter(c -> c.valueClass == childCls).toArray(FwProperty[]::new);
        } else {
            return Arrays.stream(this.getAllProperty()).filter(i ->
                    childCls.isInstance(i.get())
            ).toArray(FwProperty[]::new);
        }
    }

    public void onChanged(FwProperty property, FwContext context) {

    }

    protected void beforePropStart() {

    }

    public void start() {
        synchronized (this) {
            if (_running) {
                return;
            }
            _running = true;
        }
        beforePropStart();

        this._props.values().forEach(FwProperty::start);
        this.started();
        log.debug("Component '" + this.getName() + "' started.");
    }

    public void started() {

    }

    @Override
    public void stop() {
        synchronized (this) {
            if (this._stopping) {
                return;
            }
            this._stopping = true;
        }

        this._props.values().forEach(FwProperty::stop);
        this._props.clear();
        this._props = null;

        this.stopped();
        this.descendantsStopped();
        log.debug("Component '" + this.getName() + "' stopped.");
    }

    public void stopped() {

    }


    public void removeProperty(String name) {
        var prop = this.getProperty(name);
        if (prop != null) {
            this.removeProperty(prop);
        } else {
            log.error("Can not remove property in component '" + this.getName() + "' : not exist property name : " + name);
        }
    }

    public void removeProperty(FwProperty property) {
        if (this._props.containsValue(property)) {
            property.stop();
            this._props.remove(property.getName());
            log.debug("property '{}' removed", property.getName());
        } else {
            log.error("Can not remove property in component '" + this.getName() + "' : not exist property : " + property.getName());
        }
    }


    private String generateUniqueSlotName(String defName) {
        if (this.getProperty(defName) == null) {
            return defName;
        }
        int divIndex = -1;
        for (int i = defName.length() - 1; i >= 0 && Character.isDigit(defName.charAt(i)); divIndex = i--) {
        }
        String base = defName;
        int num = 1;
        int i;
        char c;
        if (divIndex > 0) {
            base = defName.substring(0, divIndex);
            num = Integer.parseInt(defName.substring(divIndex)) + 1;

            for (i = divIndex; i < defName.length() - 1; ++i) {
                c = defName.charAt(i);
                if (c != '0') {
                    break;
                }

                base = base + '0';
            }
        } else if (divIndex == 0) {
            base = "";
            num = Integer.parseInt(defName) + 1;

            for (i = divIndex; i < defName.length() - 1; ++i) {
                c = defName.charAt(i);
                if (c != '0') {
                    break;
                }

                base = base + '0';
            }
        }

        for (i = num; i - num <= 100000; ++i) {
            String name = base + i;
            if (this.getProperty(defName) == null) {
                return defName;
            }
        }
        throw new IllegalStateException("NameContainer not functioning");
    }

    public void onEvent(Object event) {

    }

    public void routeEvent(String[] key, Object event) {
        if (key == null || key.length == 0) {
            this.onEvent(event);
        } else if (key[0].equals("..")) {
            this.getParentComponent().routeEvent(ArrayUtil.removeOne(key, 0), event);
        } else {
            var p = this.getProperty(key[0]);
            if (p == null) {
                log.error("Fail to route event to '{}' property from component '{}'", key[0], this.getName());
            } else {
                p.routeEvent(ArrayUtil.removeOne(key, 0), event);
            }
        }
    }


    protected String logStr(String msg, Object... arg) {
        String logMsg = MessageFormatter.arrayFormat(msg, arg).getMessage();
        var clsName = this.getClass().getSimpleName();
        return clsName+"<"+this.getLogLocate()+"> "+logMsg;
    }

    protected String getLogLocate() {
        return "";
    }
}
