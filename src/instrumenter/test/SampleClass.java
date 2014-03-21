package instrumenter.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SampleClass {
	
	private int a = 0;
	private int other = 1;
	
	public void localAssignment(int a) {
		int c = a;
	}
	
	public void fieldTolocalVarAssignment() {
		int b = this.a;		
	}
	
	public void localVarToFieldAssignment() {
		int local = 0; 
		this.a = local;		
	}
	
	public void fieldToFieldAssignement() {
		this.a = this.other;
	}
	
	public int getA() {
		return this.a;
	}
	
	public int getLocal() {
		int local = 9;
		return local;
	}
	
	@Test
	public void testInt() {
		
		int a = 0;
		assertEquals(0, a);
	}
	
	@Test
	public void testString() {
		String a = "test";
		assertEquals("test", a);
	}
	
	private List<String> list = new ArrayList<String>();
	public void testListModification() {
		list.add("aaa");
	}
	
	@Test
	public void testAssertWithMethodCall() {
		assertEquals(1, getA());
	}
	
	@Test
	public void testAssertListProperties() {
		list.add("aaa");
		assertEquals(1, getListCount());
//		assertEquals(1, list.size());  // TODO: tratar diretamente a asserção da list
	}
	
	private int getListCount() {
		return list.size();
	}
	
	@Test
	public void testPop() {
		// isso deve gerar um pop, pois ninguém armazena o resultado da função
		getListCount();
	}
	
	private static List<Integer> staticList = new ArrayList<Integer>();
	public static void modifyStaticList() {
		staticList.add(1);
	}
	
	
}

