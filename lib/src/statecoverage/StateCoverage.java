package statecoverage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StateCoverage {
	
	// TODO: ver se eh necessario manter o registro de influencias global
	static InfluenceMap globalInfluences = new InfluenceMap();
	static TestRegistry testRegistry = new TestRegistry();
	
	static TestRecord actualTest = null;
	
	private static String log = new String();
	
	private static void dump(String str) {
		log += str;
		log += "\n";
	}

	public static void AddDependency(String target, String source) {
		System.out.println("Adding dependency of " + source + " to " + target);
		
		dump(target + " <- " + source);
		
		globalInfluences.addDependency(target, source);
		actualTest.getInfluenceMap().addDependency(target, source);
	}
	
	public static void AddTestDependency(String target, String source) {
		
		dump(target + " <- " + source);
		
		actualTest.getInfluenceMap().addDependency(target, source);		
		actualTest.getInfluenceMap().ignoreState(target);
	}

	public static void ClearDependenciesOf(String target) {
		
		dump(target + " <- empty");
		
		globalInfluences.clearDependenciesOf(target);
		actualTest.getInfluenceMap().clearDependenciesOf(target);
	}
	
	public static void ClearTestDependenciesOf(String target) {
		
		dump(target + " <- empty");
		
		globalInfluences.clearDependenciesOf(target);
		actualTest.getInfluenceMap().clearDependenciesOf(target);
		actualTest.getInfluenceMap().ignoreState(target);
	}
	
	public static void AddAssert(String assertPredicate) {
		
		dump("add assert : " + assertPredicate);
		
		actualTest.addAssert(assertPredicate);
	}

	public static void BeginTestCapture(String testName) {
		dump("Beginning test : " + testName);
		actualTest = testRegistry.addTest(testName);
	}
	
	public static void EndTestCapture(String testName) {
		dump("Ending test : " + testName);
		dumpToFile("c:/sc_output/dump.txt");
		actualTest = null;
		System.out.println(StateCoverage.GetResultFor(testName));
	}

	private static void dumpToFile(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(log);
			writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String GetResultFor(String test) {
		
		StateCoverageSolver solver = new StateCoverageSolver(testRegistry);
		return solver.computeStateCoverageFor(test, true).toString();
//		return solver.computeStateCoverageFor(test).toString();
	}



}

