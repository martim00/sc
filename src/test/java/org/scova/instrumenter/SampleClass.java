package org.scova.instrumenter;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

public class SampleClass {

	protected Class logConstructorSignature[] = { java.lang.String.class };

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
		// assertEquals(1, list.size()); // TODO: tratar diretamente a asserção
		// da list
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

	@Test
	public void testWhileIteratorSample() {

		List<String> myList = new ArrayList<String>();
		myList.add("ola mundo");

		Iterator<String> it = myList.iterator();
		while (it.hasNext()) {
			String n = it.next();
			assertTrue(true); // only to add an assert here
		}
	}

	public void testDupX() {

		// NEW java/lang/NoClassDefFoundError
		// DUP_X1
		// SWAP
		// INVOKEVIRTUAL java/lang/Throwable.getMessage ()Ljava/lang/String;
		// INVOKESPECIAL java/lang/NoClassDefFoundError.<init>
		// (Ljava/lang/String;)V
		// ATHROW

		// Class c = Abstract.class;

		// return null;
		//
		// boolean test = changeCount == 0;
		// if (test) {
		// throw new NullPointerException("Null values are not allowed");
		// }

		int count = 0;
		if (count++ > 0) {
			count = 0;
		}

		// if (changeCount > MAX_CHANGES_BEFORE_PURGE) {
		// changeCount = 0;
		// }

		// Log log = LogFactory.getLog(CatalogFactory.class);

		// SampleClass c = new SampleClass();
		// c.getA();
		// Class c = SampleClass.class;

	}

	private final Properties getConfigurationFile(
			ClassLoader classLoader, String fileName) {
		Properties props = null;
		double priority = 0.0;

		ArrayList<String> urls = new ArrayList<String>();
		Iterator<String> it = urls.iterator();

		while (it.hasNext()) {

			Properties newProps = new Properties();
			if (newProps != null) {
				if (props == null) {
					priority = 0.0;
				} else {
					String newPriorityStr = "";
					double newPriority = 0.0;
					if (newPriorityStr != null) {
						newPriority = 0;
					}

					props = newProps;
					priority = newPriority;
				}

			}
		}

		return props;
	}

}
