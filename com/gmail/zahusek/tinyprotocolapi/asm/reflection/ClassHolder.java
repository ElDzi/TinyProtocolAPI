package com.gmail.zahusek.tinyprotocolapi.asm.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

final class ClassHolder 
{
    
    Map<Integer, Method> methods;
    Class<?>[] methodTypes;
    String[] methodNames;
    int[] methodModifiers;
    Class<?>[][] parameterTypes;
    
    Map<Integer, Field> fields;
    Class<?>[] fieldTypes;
	String[] fieldNames;
    int[] fieldModifiers;
    
    Map<Integer, String> enums;
	
    int[] constructorModifiers;
    Class<?>[][] constructorParameterTypes;
    Map<Integer, Constructor<?>> constructors;
   
    Access access;
    boolean isNonStaticMemberClass;
    Class<?> type;
}