package com.pxccn.PxcDali2.server.framework;

import com.pxccn.PxcDali2.server.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.AmqpHeaders;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class FwProperty<T> extends FwObject implements IFwCompLifecycle {

    Class<?> valueClass;
    Consumer<Object> eventConsumer;

    public void setEventConsumer() {
    }//TODO


    public void routeEvent(String[] key, Object event) {
        if (key == null || key.length == 0) {
            if (this.eventConsumer != null) {
                eventConsumer.accept(event);
                return;
            }
        } else if (key[0].equals("..")) {
            this.getParent().routeEvent(ArrayUtil.removeOne(key, 0), event);
        }
        var cp = get();
        if (cp instanceof FwComponent) {
            ((FwComponent) cp).routeEvent(key, event);
        }

    }

    public static <T> FwProperty<T> Declear(FwComponent parent, T defaultValue, String name, FwPropFlag flag) {

        FwProperty<T> instance = new FwProperty<>();
        instance.valueClass = defaultValue.getClass();
        instance._value = defaultValue;
        instance._wrParent = new WeakReference<>(parent);
        instance._obj_name = name;
        if (flag == null) {
            flag = FwPropFlag.READ_ONLY;
        }
        instance._flag = flag;

        if (defaultValue instanceof FwComponent) {
            ((FwComponent) defaultValue)._parent = new WeakReference<>(parent);
            ((FwComponent) defaultValue)._obj_name = name;
            instance._staticProperty = !parent.isRunning();
        }
        return instance;
    }

    public Class<?> getValueClass() {
        return this.valueClass;
    }

    boolean _staticProperty = false;

    public boolean isStaticProperty() {
        return this._staticProperty;
    }

    public FwProperty() {

    }

    public FwPropFlag getFlag() {
        return this._flag;
    }


    private FwPropFlag _flag;
    private T _value;
    private WeakReference<FwComponent> _wrParent;

    public T get() {
        return this._value;
    }

    public void set(T value, FwContext context) {
        boolean _change = !this._value.equals(value);
        var oldValue = this._value;
        this._value = value;
        if (_change) {
            this.onChange(oldValue, this._value, context);
        }
    }

    public void set(T value) {
        this.set(value, null);
    }

    protected void onChange(T oldValue, T newValue, FwContext context) {
        var parent = getParent();
        if (parent != null && parent.isRunning()) {
            this.getParent().onChanged(this, context);
            log.debug("property '{}' changed, '{}' -> '{}'", this.getName(), oldValue, newValue);
        }
    }

    private FwComponent getParent() {
        var ret = this._wrParent.get();
        if (ret == null) {
            log.error("Parent not exist anymore!");
        }
        return ret;
    }

    protected boolean _started;

    @Override
    public void start() {
        synchronized (this) {
            if (this._started) {
                return;
            }
            _started = true;
        }
        if (this._value instanceof IFwCompLifecycle) {
            try {
                ((IFwCompLifecycle) this._value).start();
            } catch (Exception e) {
                var parent = this.getParent();
                if (parent != null)
                    log.error("Error to start property<{}> in parent<{}>", this.getName(), this.getParent().getName(), e);
                else
                    log.error("Error to start property<{}>", this.getName(), e);

            }
        }
        this.started();
    }

    @Override
    public void started() {

    }

    public void stop() {
        if (this._value instanceof IFwCompLifecycle) {
            try {
                ((IFwCompLifecycle) this._value).stop();
            } catch (Exception e) {
                var parent = this.getParent();
                if (parent != null)
                    log.error("Error to stop property<{}> in parent<{}>", this.getName(), this.getParent().getName(), e);
                else
                    log.error("Error to stop property<{}>", this.getName(), e);

            }
        }
        this.stopped();
//        log.debug("Property '{}' stopped", this.getName());
    }

    @Override
    public void stopped() {
//        AmqpTemplate
    }


}
