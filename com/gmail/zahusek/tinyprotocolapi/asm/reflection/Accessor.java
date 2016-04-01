package com.gmail.zahusek.tinyprotocolapi.asm.reflection;

import com.gmail.zahusek.tinyprotocolapi.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import static com.gmail.zahusek.tinyprotocolapi.asm.Opcodes.*;
import static java.lang.reflect.Modifier.isStatic;

class Accessor {
    
    private static final String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static final String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
	
    public final Access access;
    private final ClassHolder holder;

    protected Accessor(ClassHolder holder, Access access) 
    {
        this.holder = holder;
        this.access = access;
    }

    static <M> Map<Integer, M> dump(List<M> members) 
    {
        HashMap<Integer, M> map = new HashMap<>();
        int i = 0;
        for (M member : members) 
        	map.put(i++, member);
        return map;
    }
    public static Accessor get(String path) 
    {
    	path = StringUtils.replace(path, "{nms}", NMS_PREFIX);
    	path = StringUtils.replace(path, "{obc}", OBC_PREFIX);
    	
    	Class<?> clazz = null;
		try { clazz = Class.forName(path);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class '" + path + "' is not found!");
		}
    	return get(clazz);
    }

    protected static Accessor get(Class<?> type) 
    {
        ClassHolder holder = new ClassHolder();
        
        ArrayList<Method> methods = new ArrayList<>();
        ArrayList<Field> fields = new ArrayList<>();
        ArrayList<Constructor<?>> constructors = new ArrayList<>();
        collectMembers(type, methods, fields, constructors);
        
        int n = methods.size();
        holder.methods = dump(methods);
        holder.methodModifiers = new int[n];
        holder.parameterTypes = new Class[n][];
        holder.methodTypes = new Class[n];
        holder.methodNames = new String[n];
        for (int i = 0; i < n; i++) {
            Method m = methods.get(i);
            holder.methodModifiers[i] = m.getModifiers();
            holder.parameterTypes[i] = m.getParameterTypes();
            holder.methodTypes[i] = m.getReturnType();
            holder.methodNames[i] = m.getName();
        }
        
        n = fields.size();
        holder.fields = dump(fields);
        holder.enums = new HashMap<Integer, String>();
        holder.fieldModifiers = new int[n];
        holder.fieldTypes = new Class[n];
        holder.fieldNames = new String[n];
        for (int i = 0; i < n; i++) {
            Field f = fields.get(i);
            holder.fieldModifiers[i] = f.getModifiers();
            holder.fieldTypes[i] = f.getType();
            holder.fieldNames[i] = f.getName();
            if(f.isEnumConstant()) holder.enums.put(i, f.getName());
            	
        }
        
        n = constructors.size();
        holder.constructors = dump(constructors);
        holder.constructorModifiers = new int[n];
        holder.constructorParameterTypes = new Class[n][];
        for (int i = 0; i < n; i++) {
            Constructor<?> c = constructors.get(i);
            holder.constructorModifiers[i] = c.getModifiers();
            holder.constructorParameterTypes[i] = c.getParameterTypes();
        }
        
        holder.isNonStaticMemberClass = type.getEnclosingClass() != null
                && type.isMemberClass()
                && !isStatic(type.getModifiers());

        holder.type = type;
        String className = type.getName();
        String accessClassName = "ReflectASM.ClassAccess." + className;
        
        Class<?> accessClass;
        ClassManager loader = ClassManager.get(type);
        synchronized (loader) {
            try {
                accessClass = loader.loadClass(accessClassName);
            } catch (ClassNotFoundException ignored) {
                String accessClassNameInternal = accessClassName.replace('.', '/');
                String classNameInternal = className.replace('.', '/');
                final byte[] bytes = byteCode(holder, methods, fields, accessClassNameInternal, classNameInternal);
                try {
                    accessClass = ClassUnsafe.theUnsafe.defineClass(accessClassName, bytes, 0, bytes.length, loader, type.getProtectionDomain());
                } catch (Throwable ignored1){
                    accessClass = loader.defineClass(accessClassName, bytes);
                }
            }
        }
        try {
            Access access = (Access) accessClass.newInstance();
            holder.access = access;
            return new Accessor(holder, access);
        } catch (Exception ex) {
            throw new RuntimeException("Error constructing method access class: " + accessClassName, ex);
        }
    }

    static <E extends Member> void addNonPrivate(List<E> list, E[] arr) {
        Collections.addAll(list, arr);
    }

    static void recursiveAddInterfaceMethodsToList(Class<?> interfaceType, List<Method> methods) {
        addNonPrivate(methods, interfaceType.getDeclaredMethods());
        for (Class<?> nextInterface : interfaceType.getInterfaces()) 
            recursiveAddInterfaceMethodsToList(nextInterface, methods);
    }

    static void collectMembers(Class<?> type, List<Method> methods, List<Field> fields, List<Constructor<?>> constructors) {
    	if (type.isInterface()) {
            recursiveAddInterfaceMethodsToList(type, methods);
            return;
        }
        
        boolean search = true;
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            int length = constructor.getParameterTypes().length;
            if (search) {
                switch (length) {
                    case 0:
                        constructors.add(0, constructor);
                        search = false;
                        break;
                    case 1:
                        constructors.add(0, constructor);
                        break;
                    default:
                        constructors.add(constructor);
                        break;
                }
            }
        }
        Class<?> nextClass = type;
        while (nextClass != Object.class) {
            addNonPrivate(fields, nextClass.getDeclaredFields());
            addNonPrivate(methods, nextClass.getDeclaredMethods());
            nextClass = nextClass.getSuperclass();
        }
    }

    static byte[] byteCode(ClassHolder info, List<Method> methods, List<Field> fields,
                                   String accessClassNameInternal, String classNameInternal) {

        final String baseName = "sun/reflect/MagicAccessorImpl";

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal, null, baseName,
                new String[]{Access.class.getName().replace('.', '/')});

        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, baseName, "<init>", "()V", true);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        insertNewInstance(cw, classNameInternal, info);
        insertNewRawInstance(cw, classNameInternal);

        insertInvoke(cw, classNameInternal, methods);

        insertGetObject(cw, classNameInternal, fields);
        insertSetObject(cw, classNameInternal, fields);
        insertGetPrimitive(cw, classNameInternal, fields, Type.BOOLEAN_TYPE, "getBoolean", IRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.BOOLEAN_TYPE, "setBoolean", ILOAD);
        insertGetPrimitive(cw, classNameInternal, fields, Type.BYTE_TYPE, "getByte", IRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.BYTE_TYPE, "setByte", ILOAD);
        insertGetPrimitive(cw, classNameInternal, fields, Type.SHORT_TYPE, "getShort", IRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.SHORT_TYPE, "setShort", ILOAD);
        insertGetPrimitive(cw, classNameInternal, fields, Type.INT_TYPE, "getInt", IRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.INT_TYPE, "setInt", ILOAD);
        insertGetPrimitive(cw, classNameInternal, fields, Type.LONG_TYPE, "getLong", LRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.LONG_TYPE, "setLong", LLOAD);
        insertGetPrimitive(cw, classNameInternal, fields, Type.DOUBLE_TYPE, "getDouble", DRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.DOUBLE_TYPE, "setDouble", DLOAD);
        insertGetPrimitive(cw, classNameInternal, fields, Type.FLOAT_TYPE, "getFloat", FRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.FLOAT_TYPE, "setFloat", FLOAD);
        insertGetPrimitive(cw, classNameInternal, fields, Type.CHAR_TYPE, "getChar", IRETURN);
        insertSetPrimitive(cw, classNameInternal, fields, Type.CHAR_TYPE, "setChar", ILOAD);
        cw.visitEnd();
        
        return cw.toByteArray();

    }

    static void insertNewRawInstance(ClassWriter cw, String classNameInternal) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "newInstance", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, classNameInternal);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", "()V", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    static void insertNewInstance(ClassWriter cw, String classNameInternal, ClassHolder info) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "newInstance",
                "(I[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        int n = info.constructorModifiers.length;

        if (n != 0) {
            mv.visitVarInsn(ILOAD, 1);
            Label[] labels = new Label[n];
            for (int i = 0; i < n; i++)
                labels[i] = new Label();
            Label defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

            StringBuilder buffer = new StringBuilder(128);
            for (int i = 0; i < n; i++) {
                mv.visitLabel(labels[i]);
                if (i == 0)
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{classNameInternal}, 0, null);
                else
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                mv.visitTypeInsn(NEW, classNameInternal);
                mv.visitInsn(DUP);

                buffer.setLength(0);
                buffer.append('(');

                Class<?>[] paramTypes = info.constructorParameterTypes[i];
                for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitIntInsn(BIPUSH, paramIndex);
                    mv.visitInsn(AALOAD);
                    Type paramType = Type.getType(paramTypes[paramIndex]);
                    unbox(mv, paramType);
                    buffer.append(paramType.getDescriptor());
                }
                buffer.append(")V");
                mv.visitMethodInsn(INVOKESPECIAL, classNameInternal, "<init>", buffer.toString(), true);
                mv.visitInsn(ARETURN);
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Constructor not found: ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", true);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", true);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    static void insertInvoke(ClassWriter cw, String classNameInternal, List<Method> methods) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC + ACC_VARARGS, "invoke",
                "(Ljava/lang/Object;I[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();

        int n = methods.size();

        if (n != 0) {
            mv.visitVarInsn(ILOAD, 2);
            Label[] labels = new Label[n];
            for (int i = 0; i < n; i++)
                labels[i] = new Label();
            Label defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

            StringBuilder buffer = new StringBuilder(128);
            for (int i = 0; i < n; i++) {
                Method method = methods.get(i);
                boolean isInterface = method.getDeclaringClass().isInterface();
                boolean isStatic = isStatic(method.getModifiers());

                mv.visitLabel(labels[i]);
                if (i == 0)
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{classNameInternal}, 0, null);
                else
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                if (!isStatic) {
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitTypeInsn(CHECKCAST, classNameInternal);
                }

                buffer.setLength(0);
                buffer.append('(');

                String methodName = method.getName();
                Class<?>[] paramTypes = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
                    mv.visitVarInsn(ALOAD, 3);
                    mv.visitIntInsn(BIPUSH, paramIndex);
                    mv.visitInsn(AALOAD);
                    Type paramType = Type.getType(paramTypes[paramIndex]);
                    unbox(mv, paramType);
                    buffer.append(paramType.getDescriptor());
                }

                buffer.append(')');
                buffer.append(Type.getDescriptor(returnType));
                final int inv = isInterface ? INVOKEINTERFACE : (isStatic ? INVOKESTATIC : INVOKEVIRTUAL);
                mv.visitMethodInsn(inv, classNameInternal, methodName, buffer.toString(), true);

                final Type retType = Type.getType(returnType);
                box(mv, retType);
                mv.visitInsn(ARETURN);
            }

            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Method not found: ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", true);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", true);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    static void insertSetObject(ClassWriter cw, String classNameInternal, List<Field> fields) {
        int maxStack = 6;
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 2);

        if (!fields.isEmpty()) {
            maxStack--;
            Label[] labels = new Label[fields.size()];
            for (int i = 0, n = labels.length; i < n; i++)
                labels[i] = new Label();
            Label defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

            for (int i = 0, n = labels.length; i < n; i++) {
                Field field = fields.get(i);
                Type fieldType = Type.getType(field.getType());
                boolean st = isStatic(field.getModifiers());

                mv.visitLabel(labels[i]);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                if (!st) {
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitTypeInsn(CHECKCAST, classNameInternal);
                }
                mv.visitVarInsn(ALOAD, 3);

                unbox(mv, fieldType);

                mv.visitFieldInsn(st ? PUTSTATIC : PUTFIELD, classNameInternal, field.getName(), fieldType.getDescriptor());
                mv.visitInsn(RETURN);
            }

            mv.visitLabel(defaultLabel);
            mv.visitFrame(F_SAME, 0, null, 0, null);
        }
        mv = insertThrowExceptionForFieldNotFound(mv);
        mv.visitMaxs(maxStack, 4);
        mv.visitEnd();
    }

    static void insertGetObject(ClassWriter cw, String classNameInternal, List<Field> fields) {
        int maxStack = 6;
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 2);

        if (!fields.isEmpty()) {
            maxStack--;
            Label[] labels = new Label[fields.size()];
            for (int i = 0, n = labels.length; i < n; i++)
                labels[i] = new Label();
            Label defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

            for (int i = 0, n = labels.length; i < n; i++) {
                Field field = fields.get(i);
                mv.visitLabel(labels[i]);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                if (isStatic(field.getModifiers())) {
                    mv.visitFieldInsn(GETSTATIC, classNameInternal, field.getName(), Type.getDescriptor(field.getType()));
                } else {
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitTypeInsn(CHECKCAST, classNameInternal);
                    mv.visitFieldInsn(GETFIELD, classNameInternal, field.getName(), Type.getDescriptor(field.getType()));
                }
                Type fieldType = Type.getType(field.getType());
                box(mv, fieldType);
                mv.visitInsn(ARETURN);
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(F_SAME, 0, null, 0, null);
        }
        insertThrowExceptionForFieldNotFound(mv);
        mv.visitMaxs(maxStack, 3);
        mv.visitEnd();
    }
    

    static void insertSetPrimitive(ClassWriter cw, String classNameInternal, List<Field> fields, Type primitiveType, String setterMethodName, int loadValueInstruction) {
        int maxStack = 6;
        int maxLocals = 5;
        final String typeNameInternal = primitiveType.getDescriptor();
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, setterMethodName, "(Ljava/lang/Object;I" + typeNameInternal + ")V", null,
                null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 2);

        if (!fields.isEmpty()) {
            maxStack--;
            Label[] labels = new Label[fields.size()];
            Label labelForInvalidTypes = new Label();
            boolean hasAnyBadTypeLabel = true;
            for (int i = 0, n = labels.length; i < n; i++) {
                if (Type.getType(fields.get(i).getType()).equals(primitiveType)){
                    labels[i] = new Label();
                }
                else {
                    labels[i] = labelForInvalidTypes;
                    hasAnyBadTypeLabel = true;
                }
            }
            Label defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

            for (int i = 0, n = labels.length; i < n; i++) {
                if (!labels[i].equals(labelForInvalidTypes)) {
                    Field field = fields.get(i);
                    mv.visitLabel(labels[i]);
                    mv.visitFrame(F_SAME, 0, null, 0, null);
                    if (isStatic(field.getModifiers())) {
                        mv.visitVarInsn(loadValueInstruction, 3);
                        mv.visitFieldInsn(PUTSTATIC, classNameInternal, field.getName(), typeNameInternal);
                    } else {
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitTypeInsn(CHECKCAST, classNameInternal);
                        mv.visitVarInsn(loadValueInstruction, 3);
                        mv.visitFieldInsn(PUTFIELD, classNameInternal, field.getName(), typeNameInternal);
                    }
                    mv.visitInsn(RETURN);
                }
            }
            if (hasAnyBadTypeLabel) {
                mv.visitLabel(labelForInvalidTypes);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                insertThrowExceptionForFieldType(mv, primitiveType.getClassName());
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(F_SAME, 0, null, 0, null);
        }
        mv = insertThrowExceptionForFieldNotFound(mv);
        mv.visitMaxs(maxStack, maxLocals);
        mv.visitEnd();
    }

    static void insertGetPrimitive(ClassWriter cw, String classNameInternal, List<Field> fields, Type primitiveType, String getterMethodName, int returnValueInstruction) {
        int maxStack = 6;
        final String typeNameInternal = primitiveType.getDescriptor();
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, getterMethodName, "(Ljava/lang/Object;I)" + typeNameInternal, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 2);

        if (!fields.isEmpty()) {
            maxStack--;
            Label[] labels = new Label[fields.size()];
            Label labelForInvalidTypes = new Label();
            boolean hasAnyBadTypeLabel = false;
            for (int i = 0, n = labels.length; i < n; i++) {
                if (Type.getType(fields.get(i).getType()).equals(primitiveType))
                    labels[i] = new Label();
                else {
                    labels[i] = labelForInvalidTypes;
                    hasAnyBadTypeLabel = true;
                }
            }
            Label defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);

            for (int i = 0, n = labels.length; i < n; i++) {
                Field field = fields.get(i);
                if (!labels[i].equals(labelForInvalidTypes)) {
                    mv.visitLabel(labels[i]);
                    mv.visitFrame(F_SAME, 0, null, 0, null);
                    if (isStatic(field.getModifiers())) {
                        mv.visitFieldInsn(GETSTATIC, classNameInternal, field.getName(), typeNameInternal);
                    } else {
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitTypeInsn(CHECKCAST, classNameInternal);
                        mv.visitFieldInsn(GETFIELD, classNameInternal, field.getName(), typeNameInternal);
                    }
                    mv.visitInsn(returnValueInstruction);
                }
            }
            if (hasAnyBadTypeLabel) {
                mv.visitLabel(labelForInvalidTypes);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                insertThrowExceptionForFieldType(mv, primitiveType.getClassName());
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(F_SAME, 0, null, 0, null);
        }
        mv = insertThrowExceptionForFieldNotFound(mv);
        mv.visitMaxs(maxStack, 3);
        mv.visitEnd();

    }

    static MethodVisitor insertThrowExceptionForFieldNotFound(MethodVisitor mv) {
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Field not found: ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", true);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", true);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitInsn(ATHROW);
        return mv;
    }

    static MethodVisitor insertThrowExceptionForFieldType(MethodVisitor mv, String fieldType) {
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Field not declared as " + fieldType + ": ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", true);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", true);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", true);
        mv.visitInsn(ATHROW);
        return mv;
    }

    static void box(MethodVisitor mv, Type type) {
        switch (type.getSort()) {
            case Type.VOID:
                mv.visitInsn(ACONST_NULL);
                break;
            case Type.BOOLEAN:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", true);
                break;
            case Type.BYTE:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", true);
                break;
            case Type.CHAR:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", true);
                break;
            case Type.SHORT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", true);
                break;
            case Type.INT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", true);
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", true);
                break;
            case Type.LONG:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", true);
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", true);
                break;
        }
    }

    static void unbox(MethodVisitor mv, Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", true);
                break;
            case Type.BYTE:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Number");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "byteValue", "()B", true);
                break;
            case Type.CHAR:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", true);
                break;
            case Type.SHORT:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Number");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "shortValue", "()S", true);
                break;
            case Type.INT:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Number");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", true);
                break;
            case Type.FLOAT:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Number");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", true);
                break;
            case Type.LONG:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Number");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "longValue", "()J", true);
                break;
            case Type.DOUBLE:
                mv.visitTypeInsn(CHECKCAST, "java/lang/Number");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "doubleValue", "()D", true);
                break;
            case Type.ARRAY:
                mv.visitTypeInsn(CHECKCAST, type.getDescriptor());
                break;
            case Type.OBJECT:
                mv.visitTypeInsn(CHECKCAST, type.getInternalName());
                break;
        }
    }

    @Override
    public String toString() {
        return access.toString();
    }

    public boolean isNonStaticMemberClass() {
        return holder.isNonStaticMemberClass;
    }

    public int[] getConstructorModifiers() {
        return holder.constructorModifiers;
    }
    
    public Class<?>[][] getConstructorParameterTypes() {
        return holder.constructorParameterTypes;
    }

    public int[] getMethodModifiers() {
        return holder.methodModifiers;
    }

    public int[] getFieldModifiers() {
        return holder.fieldModifiers;
    }


    public int indexOfMethod(String methodName) {
        for (int i = 0, n = holder.methodNames.length; i < n; i++)
            if (holder.methodNames[i].equals(methodName)) return i;
        throw new IllegalArgumentException("Unable to find public method: " + methodName);
    }

    public int indexOfMethod(String methodName, Class<?>... paramTypes) {
        final String[] methodNames = holder.methodNames;
        for (int i = 0, n = methodNames.length; i < n; i++)
            if (methodNames[i].equals(methodName) && Arrays.equals(paramTypes, holder.parameterTypes[i])) return i;
        throw new IllegalArgumentException("Unable to find public method: " + methodName + " " + Arrays.toString(paramTypes));
    }

    public int indexOfMethod(String methodName, int paramsCount) {
        final String[] methodNames = holder.methodNames;
        final Class<?>[][] parameterTypes = holder.parameterTypes;
        for (int i = 0, n = methodNames.length; i < n; i++) {
            if (methodNames[i].equals(methodName) && parameterTypes[i].length == paramsCount) return i;
        }
        throw new IllegalArgumentException("Unable to find public method: " + methodName + " with " + paramsCount + " params.");
    }
    
    public int indexOfMethod(Class<?> type, Class<?>... paramTypes) {
        for (int i = 0, n = holder.methodNames.length; i < n; i++) {
            if (holder.methodTypes[i].equals(type) && Arrays.equals(paramTypes, holder.parameterTypes[i])) return i;
        }
        throw new IllegalArgumentException("Unable to find public method type: " + type + " with " + Arrays.toString(paramTypes) + " params.");
    }

    public String[] getMethodNames() {
        return holder.methodNames;
    }

    public Class<?>[][] getParameterTypes() {
        return holder.parameterTypes;
    }

    public Class<?>[] getMethodTypes() {
        return holder.methodTypes;
    }

    public String[] getFieldNames() {
        return holder.fieldNames;
    }

    public Class<?>[] getFieldTypes() {
        return holder.fieldTypes;
    }

    public int getFieldCount() {
        return holder.fieldTypes.length;
    }

    public int indexOfField(String fieldName) {
        String[] fieldNames = holder.fieldNames;
        for (int i = 0, n = fieldNames.length; i < n; i++)
            if (fieldNames[i].equals(fieldName)) return i;
        throw new IllegalArgumentException("Unable to find public field: " + fieldName);
    }
    
    public int indexOfField(Class<?> fieldReturn, int index) {
        Class<?>[] fieldNames = holder.fieldTypes;
        for (int i = index, n = fieldNames.length; i < n; ++i)
            if (fieldNames[i].isAssignableFrom(fieldReturn)) return i;
        throw new IllegalArgumentException("Unable to find public field: " + fieldReturn);
    }

    public Object newInstance(int constructorIndex, Object... args) {
        return access.newInstance(constructorIndex, args);
    }

    public Object newInstance() {
        return access.newInstance();
    }

    public Object invoke(Object instance, int methodIndex, Object... args) {
        return access.invoke(instance, methodIndex, args);
    }

    public void set(Object instance, int fieldIndex, Object value) {
        access.set(instance, fieldIndex, value);
    }

    public void setBoolean(Object instance, int fieldIndex, boolean value) {
        access.setBoolean(instance, fieldIndex, value);
    }

    public void setByte(Object instance, int fieldIndex, byte value) {
        access.setByte(instance, fieldIndex, value);
    }

    public void setShort(Object instance, int fieldIndex, short value) {
        access.setShort(instance, fieldIndex, value);
    }

    public void setInt(Object instance, int fieldIndex, int value) {
        access.setInt(instance, fieldIndex, value);
    }

    public void setLong(Object instance, int fieldIndex, long value) {
        access.setLong(instance, fieldIndex, value);
    }

    public void setDouble(Object instance, int fieldIndex, double value) {
        access.setDouble(instance, fieldIndex, value);
    }

    public void setFloat(Object instance, int fieldIndex, float value) {
        access.setFloat(instance, fieldIndex, value);
    }

    public void setChar(Object instance, int fieldIndex, char value) {
        access.setChar(instance, fieldIndex, value);
    }

    public Object get(Object instance, int fieldIndex) {
        return access.get(instance, fieldIndex);
    }

    public char getChar(Object instance, int fieldIndex) {
        return access.getChar(instance, fieldIndex);
    }

    public boolean getBoolean(Object instance, int fieldIndex) {
        return access.getBoolean(instance, fieldIndex);
    }

    public byte getByte(Object instance, int fieldIndex) {
        return access.getByte(instance, fieldIndex);
    }

    public short getShort(Object instance, int fieldIndex) {
        return access.getShort(instance, fieldIndex);
    }

    public int getInt(Object instance, int fieldIndex) {
        return access.getInt(instance, fieldIndex);
    }

    public long getLong(Object instance, int fieldIndex) {
        return access.getLong(instance, fieldIndex);
    }

    public double getDouble(Object instance, int fieldIndex) {
        return access.getDouble(instance, fieldIndex);
    }

    public float getFloat(Object instance, int fieldIndex) {
        return access.getFloat(instance, fieldIndex);
    }
    
    public int indexOfEnum(String enumName) {
        String[] enumNames = holder.fieldNames;
        for (int i = 0, n = enumNames.length; i < n; i++)
            if (enumNames[i].equals(enumName)) return i;
        throw new IllegalArgumentException("Unable to find public field: " + enumName);
    }
    
    public String[] getEnumNames() {
        return (String[]) holder.enums.values().toArray();
    }
    
    public int getEnumCount() {
        return holder.enums.size();
    }
    public Class<?> getCanonicalClass(String path){
    	path = StringUtils.replace(path, "{nms}", NMS_PREFIX);
    	path = StringUtils.replace(path, "{obc}", OBC_PREFIX);
    	
    	Class<?> clazz = null;
		try { clazz = Class.forName(path);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class '" + path + "' is not found!");
		}
		return clazz;
    }
    
    public Class<?> getClassType(){
    	return holder.type;
    }
}