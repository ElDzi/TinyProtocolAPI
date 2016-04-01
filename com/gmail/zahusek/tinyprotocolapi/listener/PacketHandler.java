package com.gmail.zahusek.tinyprotocolapi.listener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
public @interface PacketHandler
{
    PacketPriority priority () default PacketPriority.NORMAL;
    boolean ignoreCancelled () default false;
}