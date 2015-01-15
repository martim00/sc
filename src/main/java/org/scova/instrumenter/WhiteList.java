package org.scova.instrumenter;

import java.util.HashSet;
import java.util.Set;

public class WhiteList {
	
	public static Set<String> getAsserts() {
		
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
		
		return registeredAsserts;
		
	}

}
