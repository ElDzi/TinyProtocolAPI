package com.gmail.zahusek.tinyprotocolapi.asm;

import com.gmail.zahusek.tinyprotocolapi.asm.Attribute;

public class CustomAttr extends Attribute {

	public CustomAttr(final String type, final byte[] value) {
		super(type);
		super.value = value;
	}

}
