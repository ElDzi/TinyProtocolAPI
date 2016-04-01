package com.gmail.zahusek.tinyprotocolapi.asm.reflection;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

class ClassUnsafe 
{

    public final static Unsafe theUnsafe;

    static {
        try 
        {
            Field uf = ClassUnsafe.class.getDeclaredField("theUnsafe");
            uf.setAccessible(true);
            theUnsafe = (Unsafe) uf.get(null);
        } 
        catch (Exception e) 
        {
            throw new AssertionError(e);
        }
    }
}