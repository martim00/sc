package statecoverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestRecord {
	
	List<String> asserts = new ArrayList<String>();
	Set<String> modifiedStates = new HashSet<String>();
	InfluenceMap influences = new InfluenceMap();

	public void addAssert(String assertPredicate) {
		asserts.add(assertPredicate);
	}

	public List<String> getAsserts() {
		return asserts;
	}

	public InfluenceMap getInfluenceMap() {
		return influences;
	}

	public void addModification(String modification) {
		modifiedStates.add(modification);
	}

	public Set<String> getModifiedStates() {
		return modifiedStates;
	}

}