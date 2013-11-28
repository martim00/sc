package statecoverage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class StateCoverageSolver {
	
	private TestRegistry registry = null;

	public StateCoverageSolver(TestRegistry registry) {
		this.registry = registry;
	}
	
	private Set<String> getTotalModifiedState(TestRecord testRecord) {
		
		InfluenceMap testInfluence = testRecord.getInfluenceMap();
		Set<String> totalModifiedState = testInfluence.getAllTargets();
		return totalModifiedState;
	}
	
	private Set<String> getTotalCoveredState(TestRecord testRecord) {
		
		List<String> asserts = testRecord.getAsserts();
		
		
		Set<String> totalCoveredState = new HashSet<String>();
		
		for (String assertPred : asserts) {
			
			Set<String> influences = testRecord.getInfluenceMap().getInfluencesOf(assertPred);
			totalCoveredState.addAll(influences);
			
			// adicionamos o próprio assert se ele não for um dos ignorados
			if (!testRecord.getInfluenceMap().ignores(assertPred))
				totalCoveredState.add(assertPred);
		}
		
		return totalCoveredState;
	}

	public StateCoverageResult computeStateCoverageFor(String testName) {
		
		TestRecord testRecord = registry.getRecordFor(testName);
		
		Set<String> totalModifiedState = getTotalModifiedState(testRecord);
		Set<String> totalCoveredState = getTotalCoveredState(testRecord);
		
		return new StateCoverageResult(testName, totalCoveredState, totalModifiedState);
	}
	
	public StateCoverageResult computeStateCoverageFor(String testName, boolean attributesOnly) {
		
		TestRecord testRecord = registry.getRecordFor(testName);
		
		Set<String> totalModifiedState = getTotalModifiedState(testRecord);
		Set<String> totalCoveredState = getTotalCoveredState(testRecord);
		
		return new StateCoverageResult(testName, totalCoveredState, totalModifiedState, true);
	}

}
