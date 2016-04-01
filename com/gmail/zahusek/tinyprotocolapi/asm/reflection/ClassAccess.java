package com.gmail.zahusek.tinyprotocolapi.asm.reflection;

public class ClassAccess
{
	public final Accessor accessor;
	public final Access access;
	
	public ClassAccess(String path)
	{accessor = Accessor.get(path);
	access = accessor.access;}
	
	public ClassAccess(Class<?> path) 
	{accessor = Accessor.get(path);
	access = accessor.access;}

	//constructor
	@SuppressWarnings("unchecked")
	public <T> T newInstance() 
	{ return (T) access.newInstance(); }
	
	@SuppressWarnings("unchecked")
	public <T> T newInstance(int index, Object... args) 
	{ return (T) access.newInstance(index, args); }
	
	public Class<?>[][] getConstructortParameterTypes() 
	{ return accessor.getConstructorParameterTypes(); }
	
	//field
	public void set(Object instance, int index, Object value) 
	{ access.set(instance, index, value); }
	
	public void set(Object instance, String fieldName, Object value)
	{ set(instance, getIndexOfField(fieldName), value);}
	
	public void set(Object instance, Class<?> fieldType, int index, Object value) 
	{ set(instance, getIndexOfField(fieldType, index), value); }
	
	public void set(Object instance, String fieldType, int index, Object value)
	{ set(instance, getCanonicalClass(fieldType), index, value);}

	public void setBoolean(Object instance, int index, boolean value) 
	{ access.setBoolean(instance, index, value); }

	public void setByte(Object instance, int index, byte value) 
	{ access.setByte(instance, index, value); }

	public void setShort(Object instance, int index, short value) 
	{ access.setShort(instance, index, value); }

	public void setInt(Object instance, int index, int value) 
	{ access.setInt(instance, index, value); }

	public void setLong(Object instance, int index, long value) 
	{ access.setLong(instance, index, value); }

	public void setDouble(Object instance, int index, double value) 
	{ access.setDouble(instance, index, value); }

	public void setFloat(Object instance, int index, float value)
	{ access.setFloat(instance, index, value);}

	public void setChar(Object instance, int index, char value)
	{ access.setChar(instance, index, value); }
		
	@SuppressWarnings("unchecked")
	public <T> T get(Object instance, int index) 
	{ return (T) access.get(instance, index); }
	
	public <T> T get(Object instance, String fieldName)
	{ return get(instance, getIndexOfField(fieldName)); }
	
	public <T> T get(Object instance, Class<?> fieldType, int index) 
	{ return get(instance, getIndexOfField(fieldType, index)); }
	
	@SuppressWarnings("unchecked")
	public <T> T get(Object instance, String fieldType, int index)
	{ return (T) get(instance, getCanonicalClass(fieldType), index); }

	public boolean getBoolean(Object instance, int index) 
	{ return access.getBoolean(instance, index); }

	public byte getByte(Object instance, int index) 
	{ return access.getByte(instance, index); }

	public short getShort(Object instance, int index)
	{ return access.getShort(instance, index); }

	public int getInt(Object instance, int index)
	{ return access.getInt(instance, index); }

	public long getLong(Object instance, int index)
	{ return access.getLong(instance, index); }

	public double getDouble(Object instance, int index) 
	{ return access.getDouble(instance, index); }

	public float getFloat(Object instance, int index)
	{ return access.getFloat(instance, index); }
	
	public char getChar(Object instance, int index)
	{ return access.getChar(instance, index); }

	public String getString(Object instance, int index)
	{ return get(instance, index); }
	
	public Enum<?> getEnum(int index)
	{ return get(null, index); }
	
	public Class<?>[] getFieldTypes() 
	{ return accessor.getFieldTypes(); }
	
	public String[] getFieldNames()
	{ return accessor.getFieldNames(); }

	public String[] getEnumNames()
	{ return accessor.getEnumNames(); }

	public int getFieldCount() 
	{ return accessor.getFieldCount(); }

	public int getEnumCount() 
	{ return accessor.getEnumCount();}
	
	//method
    @SuppressWarnings("unchecked")
	public <T> T invoke(Object object, int index, Object... args)
    { return (T) access.invoke(object, index, args); }
    
	@SuppressWarnings("unchecked")
	public <T> T invoke (Object object, String methodName, int index, Object... args) 
	{ return (T) invoke(object, getIndexOfMethod(methodName, index), args); }
	
	@SuppressWarnings("unchecked")
	public <T> T invoke (Object object, String methodName, Object... args) 
	{ return (T) invoke(object, getIndexOfMethod(methodName, args == null ? 0 : args.length), args); }
	
	public <T> T invoke (Object object, Class<?> type, Class<?>[] paramsTypes, Object... args) 
	{ return invoke(object, getIndexOfMethod(type, paramsTypes), args);}
	
	public <T> T invoke (Object object, Class<?> type, String[] paramsTypes, Object... args)
	{ return invoke(object, getIndexOfMethod(type, getCanonicalClasses(paramsTypes)), args); }
	
	public <T> T invoke (Object object, String type, Class<?>[] paramsTypes, Object... args) 
	{ return invoke(object, getIndexOfMethod(getCanonicalClass(type), paramsTypes), args); }
	
	public <T> T invoke (Object object, String type, String[] paramsTypes, Object... args)
	{ return invoke(object, getIndexOfMethod(getCanonicalClass(type), getCanonicalClasses(paramsTypes)), args); }
	
	public String[] getMethodNames () 
	{ return accessor.getMethodNames(); }

	public Class<?>[][] getMethodParameterTypes () 
	{ return accessor.getParameterTypes();  }

	public Class<?>[] getMethodTypes ()
	{ return accessor.getMethodTypes(); }
	
	//index
	int getIndexOfField(String fieldName) 
	{ return accessor.indexOfField(fieldName); }
	
	int getIndexOfField(Class<?> fieldType, int index)
	{ return accessor.indexOfField(fieldType, index); }
	
	int getIndexOfMethod (String methodName, int index)
	{ return accessor.indexOfMethod(methodName, index); }
	
	int getIndexOfMethod (Class<?> type, Class<?>... paramTypes) 
	{ return accessor.indexOfMethod(type, paramTypes); }
	
	//others
	public Class<?> getUsedClass()
	{ return accessor.getClassType(); }
	
	public Class<?> getCanonicalClass(String path) 
	{ return accessor.getCanonicalClass(path); }
	
	public Class<?>[] getCanonicalClasses(String... paths) 
	{
		int size = paths.length;
		Class<?>[] classes = new Class<?>[size];
		for(int i = 0; i < size; i++)
			classes[i] = getCanonicalClass(paths[i]);
		return classes;
	}
	
	@Override
	public String toString()
	{ return accessor.toString(); }

}
