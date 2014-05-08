package instrumenter.core;

import java.util.HashSet;
import java.util.Set;

public class WhiteList {
	
	public static Set<String> getAsserts() {
		
		final Set<String> registeredAsserts = new HashSet<String>();
		registeredAsserts.add("assertEquals");
		registeredAsserts.add("assertNotNull");
		return registeredAsserts;
		
	}

}
