package org.scova.instrumenter;
import static org.junit.Assert.*;

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
		result = instrumentAndReadClass("bin/org/scova/instrumenter/SampleClass.class");
//		instrumentClass("bin/org/scova/instrumenter/SampleClass.class");
//		result = readClass("bin/org/scova/instrumenter/SampleClass.class");
	}
	
	private ClassNode instrumentAndReadClass(String pathToClass) {
		instrumentClass(pathToClass);
		return readClass(pathToClass);
	}
	
	@Test
	public void testLocalVarToLocalVarAssignment() {
		
		// setA
		MethodNode setter = result.methods.get(2);
		assertEquals("localAssignment", setter.name);
		assertEquals(6, setter.instructions.size());
		
		assertCode(setter, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.localAssignment(I)V.c\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.localAssignment(I)V.a\"\n" +
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
			    		   "    LDC \"org/scova/instrumenter/SampleClass.fieldTolocalVarAssignment()V.b\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD org/scova/instrumenter/SampleClass.a : I\n" +
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
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.localVarToFieldAssignment()V.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    PUTFIELD org/scova/instrumenter/SampleClass.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testFieldToFieldAssignment() {
				
		MethodNode method = result.methods.get(5);
		assertEquals("fieldToFieldAssignement", method.name);
		assertEquals(10, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.other\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD org/scova/instrumenter/SampleClass.other : I\n" +
			    		   "    PUTFIELD org/scova/instrumenter/SampleClass.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testReturnField() {
		
		MethodNode method = result.methods.get(6);
		assertEquals("getA", method.name);
		assertEquals(6, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getA()I\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD org/scova/instrumenter/SampleClass.a : I\n" +
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
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getLocal()I\"\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.getLocal()I.local\"\n" +
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
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testInt()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    ICONST_0\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testInt()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LCONST_0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    I2L\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testInt()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentTests_String() {
		
		MethodNode method = result.methods.get(9);
		assertEquals("testString", method.name);
		assertEquals(12, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testString()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ASTORE 1\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testString()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ALOAD 1\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (Ljava/lang/Object;Ljava/lang/Object;)V\n" +
			    		   "    LDC \"org/scova/instrumenter/SampleClass.testString()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentListModifications() {
		
		MethodNode method = result.methods.get(10);
		assertEquals("testListModification", method.name);
		assertEquals(8, method.instructions.size());
		assertCode(method, 
						   "    LDC \"org/scova/instrumenter/SampleClass.list\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
						   "    GETFIELD org/scova/instrumenter/SampleClass.list : Ljava/util/List;\n" + 
			    		   "    LDC \"aaa\"\n" +
			    		   "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    		   "    POP\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testInstrumentAssertWithCalls() {
		
		assertInstrumentation(11, "testAssertWithMethodCall", 14, 
						   "    LDC \"org/scova/instrumenter/SampleClass.testAssertWithMethodCall()V\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
						   "    LDC \"org/scova/instrumenter/SampleClass.getA()I\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
						   "    LCONST_1\n" +
						   "    ALOAD 0\n" +
						   "    INVOKEVIRTUAL org/scova/instrumenter/SampleClass.getA ()I\n" +
						   "    I2L\n" +
						   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
						   "    LDC \"org/scova/instrumenter/SampleClass.testAssertWithMethodCall()V\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
						   "    RETURN\n");
	}
	
	private void assertInstrumentation(int methodIndex, String methodName, int instructionsCount, String code) {
		assertInstrumentationOf(result, methodIndex, methodName, instructionsCount, code);
	}
	
	private void assertInstrumentationOf(ClassNode classNode
			, int methodIndex, String methodName, int instructionsCount, String code) {

		MethodNode method = classNode.methods.get(methodIndex);
		assertEquals(methodName, method.name);
		assertEquals(instructionsCount, method.instructions.size());
		assertCode(method, code);
	}
	
	@Test
	public void testShouldAddDependencyOnListProperties() {
		assertInstrumentation(13, "getListCount", 7, 
				
			    "    LDC \"org/scova/instrumenter/SampleClass.getListCount()I\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.list\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD org/scova/instrumenter/SampleClass.list : Ljava/util/List;\n" +
			    "    INVOKEINTERFACE java/util/List.size ()I\n" +
			    "    IRETURN\n");
	}
	
	@Test
	public void testAssertListProperties() {
		assertInstrumentation(12, "testAssertListProperties", 21, 

			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.list\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD org/scova/instrumenter/SampleClass.list : Ljava/util/List;\n" +
			    "    LDC \"aaa\"\n" +
			    "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    "    POP\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getListCount()I\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    "    LCONST_1\n" +
			    "    ALOAD 0\n" +
			    "    INVOKESPECIAL org/scova/instrumenter/SampleClass.getListCount ()I\n" +
			    "    I2L\n" +
			    "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
			    "    RETURN\n"); 
	}
	
	@Test
	public void testInstrumentPop() {
		
		assertInstrumentation(14, "testPop", 8, 
			    "    LDC \"org/scova/instrumenter/SampleClass.testPop()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
				"    ALOAD 0\n" +
				"    INVOKESPECIAL org/scova/instrumenter/SampleClass.getListCount ()I\n" +
				"    POP\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testPop()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
				"    RETURN\n");
	}
	
	@Test
	public void testAddModificationOnStaticList() {
		
		assertInstrumentation(15, "modifyStaticList", 8, 
			    "    LDC \"staticList\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
			    "    GETSTATIC org/scova/instrumenter/SampleClass.staticList : Ljava/util/List;\n" +
			    "    ICONST_1\n" +
			    "    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;\n" +
			    "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    "    POP\n" +
			    "    RETURN\n");
		
	}
	
	@Test // TODO: provavelmente quando arrumar o pop isso vai funcionar...
	public void testWhileIterator() {
		
		assertInstrumentation(16, "testWhileIteratorSample", 44,
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
				"    NEW java/util/ArrayList\n" +
				"    DUP\n" +
				"    INVOKESPECIAL java/util/ArrayList.<init> ()V\n" +
				"    ASTORE 1\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.myList\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" +
				"    ALOAD 1\n" +
				"    LDC \"ola mundo\"\n" +
				"    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
				"    POP\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.it\"\n" +
				"    LDC \"java/util/List.iterator()Ljava/util/Iterator;\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
				"    ALOAD 1\n" +
				"    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator;\n" +
				"    ASTORE 2\n" +
				"    GOTO L0\n" +
				"   L1\n" +
				"   FRAME APPEND [java/util/ArrayList java/util/Iterator]\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V.n\"\n" +
				"    LDC \"java/util/Iterator.next()Ljava/lang/Object;\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
				"    ALOAD 2\n" +
				"    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object;\n" +
				"    CHECKCAST java/lang/String\n" +
				"    ASTORE 3\n" +
				"    ICONST_1\n" +
				"    INVOKESTATIC org/junit/Assert.assertTrue (Z)V\n" +
				"   L0\n" +
				"   FRAME SAME\n" +
				"    ALOAD 2\n" +
				"    INVOKEINTERFACE java/util/Iterator.hasNext ()Z\n" +
				"    IFNE L1\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.testWhileIteratorSample()V\"\n" +
				"    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
				"    RETURN\n");
	}
	
	//@Test
	public void testDupX() {
		assertInstrumentation(17, "testDupX", 18, 
			   "    LDC \"org/scova/instrumenter/SampleClass.count\"\n" +  
			   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" + 
			   "    LDC \"org/scova/instrumenter/SampleClass.count\"\n" + 
			   "    INVOKESTATIC statecoverage/StateCoverage.AddModification (Ljava/lang/String;)V\n" + 
			   "    ALOAD 0\n" + 
			   "    DUP\n" + 
			   "    GETFIELD org/scova/instrumenter/SampleClass.count : I\n" +
			   "    DUP_X1\n" + 
			   "    ICONST_1\n" +
			   "    IADD\n" +
			   "    PUTFIELD org/scova/instrumenter/SampleClass.count : I\n" +
			   "    IFLE L0\n" +
			   "    ALOAD 0\n" +
			   "    ICONST_0\n" +
			   "    PUTFIELD org/scova/instrumenter/SampleClass.count : I\n" +
			   "   L0\n" +
			   "   FRAME SAME\n" +			    
			   "    RETURN\n");
	}
	
	@Test
	public void testBugInfiniteLoop() {
		
		assertInstrumentation(18, "getConfigurationFile", 63, 
				"    ACONST_NULL\n" +
			    "    ASTORE 3\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 4\n" +
			    "    NEW java/util/ArrayList\n" +
			    "    DUP\n" +
			    "    INVOKESPECIAL java/util/ArrayList.<init> ()V\n" +
			    "    ASTORE 6\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.it\"\n" +
			    "    LDC \"java/util/ArrayList.iterator()Ljava/util/Iterator;\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.it\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.urls\"\n" + 
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 6\n" +
			    "    INVOKEVIRTUAL java/util/ArrayList.iterator ()Ljava/util/Iterator;\n" +
			    "    ASTORE 7\n" +
			    "    GOTO L0\n" +
			    "   L1\n" +
			    "   FRAME FULL [org/scova/instrumenter/SampleClass java/lang/ClassLoader java/lang/String java/util/Properties D java/util/ArrayList java/util/Iterator] []\n" +
				"    NEW java/util/Properties\n" +
			    "    DUP\n" +
			    "    INVOKESPECIAL java/util/Properties.<init> ()V\n" +
			    "    ASTORE 8\n" +
			    "    ALOAD 8\n" +
			    "    IFNULL L0\n" +
			    "    ALOAD 3\n" +
			    "    IFNONNULL L2\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 4\n" +
			    "    GOTO L0\n" +
			    "   L2\n" +
			    "   FRAME APPEND [java/util/Properties]\n" +
			    "    LDC \"\"\n" +
			    "    ASTORE 9\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 10\n" +
			    "    ALOAD 9\n" +
			    "    IFNULL L3\n" +
			    "    DCONST_0\n" +
			    "    DSTORE 10\n" +		
			    "   L3\n" +
			    "   FRAME APPEND [java/lang/String D]\n" +
				"    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.props\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newProps\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 8\n" +
			    "    ASTORE 3\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.priority\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.newPriority\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    DLOAD 10\n" +
			    "    DSTORE 4\n" +
			    "   L0\n" +
			    "   FRAME CHOP 3\n" +
			    "    ALOAD 7\n" +
			    "    INVOKEINTERFACE java/util/Iterator.hasNext ()Z\n" +
			    "    IFNE L1\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;\"\n" +
			    "    LDC \"org/scova/instrumenter/SampleClass.getConfigurationFile(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/util/Properties;.props\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 3\n" +   
			    "    ARETURN\n");
	}
	
	@Test
	public void testBugCommonsMath() {
		
//		assertInstrumentation(19, "testAbs", 56, 
//				"    ACONST_NULL\n" +
//						"");
//		
	    ClassNode classNode = instrumentAndReadClass("bin/org/scova/instrumenter/SampleClass$DerivativeStructure.class");
	    assertInstrumentationOf(classNode, 2, "getPartialDerivative", 8,
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.getPartialDerivative([I)D\"\n" + 
	    	    "    LDC \"org/scova/instrumenter/SampleClass$DerivativeStructure.data\"\n" +
	    	    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
	    		"    ALOAD 0\n" +
	    		"    GETFIELD org/scova/instrumenter/SampleClass$DerivativeStructure.data : [D\n" + 
	    		"    ICONST_0\n" +
	    		"    DALOAD\n" +
	    		"    DRETURN\n");

	}
    
}
