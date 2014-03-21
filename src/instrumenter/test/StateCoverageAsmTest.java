package instrumenter.test;
import static org.junit.Assert.*;

import instrumenter.core.DebugUtils;
import instrumenter.core.StateCoverageAsm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;


public class StateCoverageAsmTest {
	
	private void instrumentClass(String inputClass) {
		
		StateCoverageAsm stateCoverage = new StateCoverageAsm();
		try {
			stateCoverage.instrumentClass(inputClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ClassNode readClass(String className) {
        ClassReader cr = null;
		try {
			FileInputStream file = new FileInputStream(className + ".class.adapted");
			cr = new ClassReader(file);
			ClassNode cn = new ClassNode();
			cr.accept(cn, ClassReader.SKIP_DEBUG);
			return cn;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	void assertCode(MethodNode method, String expectedCode) {
		assertEquals(expectedCode, DebugUtils.codeToString(method));
	}
	
	private ClassNode result = null;
	@Before
	public void setUp() {
		instrumentClass("bin/instrumenter/test/SampleClass.class");
		result = readClass("bin/instrumenter/test/SampleClass.class");
	}
	
	@Test
	public void testLocalVarToLocalVarAssignment() {
		
		// setA
		MethodNode setter = result.methods.get(2);
		assertEquals("localAssignment", setter.name);
		assertEquals(6, setter.instructions.size());
		
		assertCode(setter, 
			    		   "    LDC \"instrumenter/test/SampleClass.localAssignment(I)V.c\"\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.localAssignment(I)V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
						   "    ILOAD 1\n" +
						   "    ISTORE 2\n" +
						   "    RETURN\n");
	}
	
	@Test
	public void testFieldToLocalVarAssignment() {
				
		MethodNode setter = result.methods.get(3);
		assertEquals("fieldTolocalVarAssignment", setter.name);
		assertEquals(7, setter.instructions.size());
		
		assertCode(setter, 
			    		   "    LDC \"instrumenter/test/SampleClass.fieldTolocalVarAssignment()V.b\"\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD instrumenter/test/SampleClass.a : I\n" +
			    		   "    ISTORE 1\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testLocalVarToFieldAssignment() {
				
		MethodNode method = result.methods.get(4);
		assertEquals("localVarToFieldAssignment", method.name);
		assertEquals(11, method.instructions.size());
		
		assertCode(method, 
			    		   "    ICONST_0\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.a\"\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.localVarToFieldAssignment()V.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    PUTFIELD instrumenter/test/SampleClass.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testFieldToFieldAssignment() {
				
		MethodNode method = result.methods.get(5);
		assertEquals("fieldToFieldAssignement", method.name);
		assertEquals(10, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/SampleClass.a\"\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.other\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD instrumenter/test/SampleClass.other : I\n" +
			    		   "    PUTFIELD instrumenter/test/SampleClass.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testReturnField() {
		
		MethodNode method = result.methods.get(6);
		assertEquals("getA", method.name);
		assertEquals(6, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/SampleClass.getA()I\"\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD instrumenter/test/SampleClass.a : I\n" +
			    		   "    IRETURN\n");
	}
	
	@Test
	public void testReturnLocal() {
		
		MethodNode method = result.methods.get(7);
		assertEquals("getLocal", method.name);
		assertEquals(7, method.instructions.size());
		assertCode(method, 
			    		   "    BIPUSH 9\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.getLocal()I\"\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.getLocal()I.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ILOAD 1\n" +
			    		   "    IRETURN\n");
	}
	
	@Test
	public void testShouldInstrumentTests_int() {
		
		MethodNode method = result.methods.get(8);
		assertEquals("testInt", method.name);
		assertEquals(13, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/SampleClass.testInt()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    ICONST_0\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.testInt()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LCONST_0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    I2L\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.testInt()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentTests_String() {
		
		MethodNode method = result.methods.get(9);
		assertEquals("testString", method.name);
		assertEquals(12, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/SampleClass.testString()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ASTORE 1\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.testString()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ALOAD 1\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (Ljava/lang/Object;Ljava/lang/Object;)V\n" +
			    		   "    LDC \"instrumenter/test/SampleClass.testString()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentListModifications() {
		
		MethodNode method = result.methods.get(10);
		assertEquals("testListModification", method.name);
		assertEquals(8, method.instructions.size());
		assertCode(method, 
						   "    LDC \"instrumenter/test/SampleClass.list\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
						   "    GETFIELD instrumenter/test/SampleClass.list : Ljava/util/List;\n" + 
			    		   "    LDC \"aaa\"\n" +
			    		   "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    		   "    POP\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testInstrumentAssertWithCalls() {
		
		MethodNode method = result.methods.get(11);
		assertEquals("testAssertWithMethodCall", method.name);
		assertEquals(12, method.instructions.size());
		assertCode(method, 
						   "    LDC \"instrumenter/test/SampleClass.testAssertWithMethodCall()V\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
						   "    LDC \"instrumenter/test/SampleClass.getA()I\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
						   "    LCONST_1\n" +
						   "    ALOAD 0\n" +
						   "    INVOKEVIRTUAL instrumenter/test/SampleClass.getA ()I\n" +
						   "    I2L\n" +
						   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
						   "    LDC \"instrumenter/test/SampleClass.testAssertWithMethodCall()V\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
						   "    RETURN\n");
	}
	
	private void assertInstrumentation(int methodIndex, String methodName, int instructionsCount, String code) {
		
		MethodNode method = result.methods.get(methodIndex);
		assertEquals(methodName, method.name);
		assertEquals(instructionsCount, method.instructions.size());
		assertCode(method, code);
	}
	
	@Test
	public void testShouldAddDependencyOnListProperties() {
		assertInstrumentation(13, "getListCount", 7, 
				
			    "    LDC \"instrumenter/test/SampleClass.getListCount()I\"\n" +
			    "    LDC \"instrumenter/test/SampleClass.list\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD instrumenter/test/SampleClass.list : Ljava/util/List;\n" +
			    "    INVOKEINTERFACE java/util/List.size ()I\n" +
			    "    IRETURN\n");
	}
	
	@Test
	public void testAssertListProperties() {
		assertInstrumentation(12, "testAssertListProperties", 19, 

			    "    LDC \"instrumenter/test/SampleClass.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    "    LDC \"instrumenter/test/SampleClass.list\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    "    LDC \"instrumenter/test/SampleClass.getListCount()I\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD instrumenter/test/SampleClass.list : Ljava/util/List;\n" +
			    "    LDC \"aaa\"\n" +
			    "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    "    POP\n" +
			    "    LCONST_1\n" +
			    "    ALOAD 0\n" +
			    "    INVOKESPECIAL instrumenter/test/SampleClass.getListCount ()I\n" +
			    "    I2L\n" +
			    "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    "    LDC \"instrumenter/test/SampleClass.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
			    "    RETURN\n"); 
	}
	
	@Test
	public void testInstrumentPop() {
		
		assertInstrumentation(14, "testPop", 8, 
			    "    LDC \"instrumenter/test/SampleClass.testPop()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
				"    ALOAD 0\n" +
				"    INVOKESPECIAL instrumenter/test/SampleClass.getListCount ()I\n" +
				"    POP\n" +
				"    LDC \"instrumenter/test/SampleClass.testPop()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
				"    RETURN\n");
	}
	
	@Test
	public void testAddModificationOnStaticList() {
		
		assertInstrumentation(15, "modifyStaticList", 8, 
			    "    LDC \"staticList\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    "    GETSTATIC instrumenter/test/SampleClass.staticList : Ljava/util/List;\n" +
			    "    ICONST_1\n" +
			    "    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;\n" +
			    "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    "    POP\n" +
			    "    RETURN\n");
		
	}
	

}
