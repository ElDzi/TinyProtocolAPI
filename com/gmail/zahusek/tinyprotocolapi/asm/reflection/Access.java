package com.gmail.zahusek.tinyprotocolapi.asm.reflection;

interface Access 
{
	
	abstract public Object newInstance();
	abstract public Object newInstance(int index, Object... args);
	
	abstract public Object invoke(Object instance, int methodIndex, Object... args);
	
	abstract public void set(Object instance, int index, Object value);
	abstract public void setBoolean(Object instance, int index,boolean value);
	abstract public void setByte(Object instance, int index, byte value);
	abstract public void setShort(Object instance, int index, short value);
	abstract public void setInt(Object instance, int index, int value);
	abstract public void setLong(Object instance, int index, long value);
	abstract public void setDouble(Object instance, int index, double value);
	abstract public void setFloat(Object instance, int index, float value);
	abstract public void setChar(Object instance, int index, char value);
	
	abstract public Object get(Object instance, int index);
	abstract public char getChar(Object instance, int index);
	abstract public boolean getBoolean(Object instance, int index);
	abstract public byte getByte(Object instance, int index);
	abstract public short getShort(Object instance, int index);
	abstract public int getInt(Object instance, int index);
	abstract public long getLong(Object instance, int index);
	abstract public double getDouble(Object instance, int index);
	abstract public float getFloat(Object instance, int index);

}