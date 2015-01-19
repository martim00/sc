package org.scova.instrumenter;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.InsnList;

public class WhiteList {
	
	public static boolean isAssertMethod(String methodName) {
		
		final Set<String> registeredAsserts = new HashSet<String>();
		registeredAsserts.add("assertEquals");
		registeredAsserts.add("assertNotNull");
		registeredAsserts.add("assertNull");
		registeredAsserts.add("assertTrue");
		registeredAsserts.add("assertFalse");	
		registeredAsserts.add("assertArrayEquals");
		registeredAsserts.add("assertNotSame");
		registeredAsserts.add("assertSame");
		registeredAsserts.add("assertThat");
		
		return registeredAsserts.contains(methodName);
		
	}

	public static boolean isIgnoredField(String field) {
		String escapedString = "this(";
		escapedString += java.util.regex.Pattern.quote("$");
		escapedString += "\\d+)?";
		
		return field.matches(escapedString);
	}

}
