package statecoverage;

import java.util.ArrayList;
import java.util.List;

public class TestRecord {
	
	List<String> asserts = new ArrayList<String>();
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

}