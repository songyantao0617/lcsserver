package com.pxccn.PxcDali2.server.util.annotation;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Scope(value = "prototype")
public @interface FwComponentAnnotation {
}
