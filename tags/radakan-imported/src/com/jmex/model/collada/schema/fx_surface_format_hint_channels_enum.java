/**
 * fx_surface_format_hint_channels_enum.java
 *
 * This file was generated by XMLSpy 2007sp2 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSpy Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.jmex.model.collada.schema;

import com.jmex.xml.types.SchemaString;

public class fx_surface_format_hint_channels_enum extends SchemaString {
	public static final int ERGB = 0; /* RGB */
	public static final int ERGBA = 1; /* RGBA */
	public static final int EL = 2; /* L */
	public static final int ELA = 3; /* LA */
	public static final int ED = 4; /* D */
	public static final int EXYZ = 5; /* XYZ */
	public static final int EXYZW = 6; /* XYZW */

	public static String[] sEnumValues = {
		"RGB",
		"RGBA",
		"L",
		"LA",
		"D",
		"XYZ",
		"XYZW",
	};

	public fx_surface_format_hint_channels_enum() {
		super();
	}

	public fx_surface_format_hint_channels_enum(String newValue) {
		super(newValue);
		validate();
	}

	public fx_surface_format_hint_channels_enum(SchemaString newValue) {
		super(newValue);
		validate();
	}

	public static int getEnumerationCount() {
		return sEnumValues.length;
	}

	public static String getEnumerationValue(int index) {
		return sEnumValues[index];
	}

	public static boolean isValidEnumerationValue(String val) {
		for (int i = 0; i < sEnumValues.length; i++) {
			if (val.equals(sEnumValues[i]))
				return true;
		}
		return false;
	}

	public void validate() {

		if (!isValidEnumerationValue(toString()))
			throw new com.jmex.xml.xml.XmlException("Value of fx_surface_format_hint_channels_enum is invalid.");
	}
}