package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import statecoverage.TestRecord;

public class TestRecordTest {

	@Test
	public void testAddAssertToTest() {
		
		TestRecord testRecord = new TestRecord();
		testRecord.addAssert("HumanName.IsCelebrity()");
		
		List<String> asserts = testRecord.getAsserts();
		assertEquals(1, asserts.size());
		assertEquals("HumanName.IsCelebrity()", asserts.get(0));
	}
	
//	@Test
//	public void testAddStateModifications() {
//		TestRecord testRecord = new TestRecord();
//		testRecord.addModificationOf("");
//	}

}
