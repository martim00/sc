package statecoverage;

public class StateCoverage {
	
	// TODO: ver se eh necessario manter o registro de influencias global
	static InfluenceMap globalInfluences = new InfluenceMap();
	static TestRegistry testRegistry = new TestRegistry();
	
	static TestRecord actualTest = null;

	public static void AddDependency(String target, String source) {
		System.out.println("Adding dependency of " + source + " to " + target);
		globalInfluences.addDependency(target, source);
		actualTest.getInfluenceMap().addDependency(target, source);
	}
	
	public static void AddTestDependency(String target, String source) {
		actualTest.getInfluenceMap().addDependency(target, source);		
		actualTest.getInfluenceMap().ignoreState(target);
	}

	public static void ClearDependenciesOf(String target) {
		globalInfluences.clearDependenciesOf(target);
		actualTest.getInfluenceMap().clearDependenciesOf(target);
	}
	
	public static void ClearTestDependenciesOf(String target) {
		globalInfluences.clearDependenciesOf(target);
		actualTest.getInfluenceMap().clearDependenciesOf(target);
		actualTest.getInfluenceMap().ignoreState(target);
	}
	
	public static void AddAssert(String assertPredicate) {
		actualTest.addAssert(assertPredicate);
	}

	public static void BeginTestCapture(String testName) {
		actualTest = testRegistry.addTest(testName);
	}
	
	public static void EndTestCapture(String testName) {
		actualTest = null;
		System.out.println(StateCoverage.GetResultFor(testName));
	}

	public static String GetResultFor(String test) {
		
		StateCoverageSolver solver = new StateCoverageSolver(testRegistry);
		return solver.computeStateCoverageFor(test, true).toString();
//		return solver.computeStateCoverageFor(test).toString();
	}



}

