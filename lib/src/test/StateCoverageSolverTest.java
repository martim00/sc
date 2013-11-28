package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import statecoverage.InfluenceMap;
import statecoverage.StateCoverageResult;
import statecoverage.StateCoverageSolver;
import statecoverage.TestRecord;
import statecoverage.TestRegistry;

public class StateCoverageSolverTest {
	
	TestRegistry registry = new TestRegistry();
	InfluenceMap influenceMap = null;
	TestRecord testRecord = null;
	StateCoverageSolver solver = null;
	
	@Before
	public void setUp() {
		testRecord = registry.getRecordFor("HumanNameTest.test");
		influenceMap = testRecord.getInfluenceMap();
		solver = new StateCoverageSolver(registry);
	}

	@Test
	public void testFullCoverageShouldResultIn1()
	{
		influenceMap.addDependency("first", "second");
		testRecord.addAssert("first");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test");
		assertEquals(1, result.getStateCoverageValue(), 0.001);
		
	}
	
	@Test
	public void test() {
		
		influenceMap.addDependency("HumanName.last", "HumanName.HumanName().oneNameCelebrity");
		influenceMap.clearDependenciesOf("HumanName.first");
		influenceMap.addDependency("HumanName.IsCelebrity()", "HumanName.first");
		
		testRecord.addAssert("HumanName.IsCelebrity()");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test");
		
		// TODO: criar implementações para os três tipos de state coverage
		assertEquals(3, result.getTotalModified());
		assertTrue(result.getModifiedStates().contains("HumanName.last"));
		assertTrue(result.getModifiedStates().contains("HumanName.first"));
		assertTrue(result.getModifiedStates().contains("HumanName.IsCelebrity()"));
		
		assertEquals(2, result.getTotalCovered());
		assertTrue(result.getCoveredStates().contains("HumanName.first"));
		assertTrue(result.getCoveredStates().contains("HumanName.IsCelebrity()"));
		
		// TODO: ver se eh certo 0.5 ou 0.333 ou 0.66
		assertEquals(0.666, result.getStateCoverageValue(), 0.001);
	}
	
	@Test
	public void testShouldCountCoveredOnlyTheExactMatches()
	{
		influenceMap.addDependency("first", "second");
		testRecord.addAssert("assert");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test");
		assertEquals(0, result.getStateCoverageValue(), 0.001);
	}
	
	
	@Test
	public void testShouldBeAbleToComputeOnlyAttributes()
	{
		influenceMap.addDependency("IsDependency()", "other");
		influenceMap.addDependency("last", "first");
		
		testRecord.addAssert("last");
		
		StateCoverageResult result = solver.computeStateCoverageFor("HumanNameTest.test", true);
		assertEquals(1, result.getStateCoverageValue(), 0.001);
	}

}
