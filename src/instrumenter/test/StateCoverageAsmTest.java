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
	
	class Dummy {
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
//			assertEquals(1, list.size());  // TODO: tratar diretamente a asserção da list
		}
		
		private int getListCount() {
			return list.size();
		}
		
		
		
	}
	
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
		instrumentClass("bin/instrumenter/test/StateCoverageAsmTest$Dummy.class");
		result = readClass("bin/instrumenter/test/StateCoverageAsmTest$Dummy.class");
	}
	
	@Test
	public void testLocalVarToLocalVarAssignment() {
		
		// setA
		MethodNode setter = result.methods.get(1);
		assertEquals("localAssignment", setter.name);
		assertEquals(6, setter.instructions.size());
		
		assertCode(setter, 
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.localAssignment(I)V.c\"\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.localAssignment(I)V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
						   "    ILOAD 1\n" +
						   "    ISTORE 2\n" +
						   "    RETURN\n");
	}
	
	@Test
	public void testFieldToLocalVarAssignment() {
				
		MethodNode setter = result.methods.get(2);
		assertEquals("fieldTolocalVarAssignment", setter.name);
		assertEquals(7, setter.instructions.size());
		
		assertCode(setter, 
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.fieldTolocalVarAssignment()V.b\"\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD instrumenter/test/StateCoverageAsmTest$Dummy.a : I\n" +
			    		   "    ISTORE 1\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testLocalVarToFieldAssignment() {
				
		MethodNode method = result.methods.get(3);
		assertEquals("localVarToFieldAssignment", method.name);
		assertEquals(9, method.instructions.size());
		
		assertCode(method, 
			    		   "    ICONST_0\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.a\"\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.localVarToFieldAssignment()V.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    PUTFIELD instrumenter/test/StateCoverageAsmTest$Dummy.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testFieldToFieldAssignment() {
				
		MethodNode method = result.methods.get(4);
		assertEquals("fieldToFieldAssignement", method.name);
		assertEquals(8, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.a\"\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.other\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD instrumenter/test/StateCoverageAsmTest$Dummy.other : I\n" +
			    		   "    PUTFIELD instrumenter/test/StateCoverageAsmTest$Dummy.a : I\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testReturnField() {
		
		MethodNode method = result.methods.get(5);
		assertEquals("getA", method.name);
		assertEquals(6, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.getA()I\"\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
			    		   "    GETFIELD instrumenter/test/StateCoverageAsmTest$Dummy.a : I\n" +
			    		   "    IRETURN\n");
	}
	
	@Test
	public void testReturnLocal() {
		
		MethodNode method = result.methods.get(6);
		assertEquals("getLocal", method.name);
		assertEquals(7, method.instructions.size());
		assertCode(method, 
			    		   "    BIPUSH 9\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.getLocal()I\"\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.getLocal()I.local\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ILOAD 1\n" +
			    		   "    IRETURN\n");
	}
	
	@Test
	public void testShouldInstrumentTests_int() {
		
		MethodNode method = result.methods.get(7);
		assertEquals("testInt", method.name);
		assertEquals(13, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testInt()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    ICONST_0\n" +
			    		   "    ISTORE 1\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testInt()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LCONST_0\n" +
			    		   "    ILOAD 1\n" +
			    		   "    I2L\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testInt()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentTests_String() {
		
		MethodNode method = result.methods.get(8);
		assertEquals("testString", method.name);
		assertEquals(12, method.instructions.size());
		assertCode(method, 
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testString()V\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ASTORE 1\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testString()V.a\"\n" +
			    		   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    		   "    LDC \"test\"\n" +
			    		   "    ALOAD 1\n" +
			    		   "    INVOKESTATIC org/junit/Assert.assertEquals (Ljava/lang/Object;Ljava/lang/Object;)V\n" +
			    		   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testString()V\"\n" + 
			    		   "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" + 
			    		   "    RETURN\n");
	}
	
	@Test
	public void testShouldInstrumentListModifications() {
		
		MethodNode method = result.methods.get(9);
		assertEquals("testListModification", method.name);
		assertEquals(9, method.instructions.size());
		assertCode(method, 
						   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.list\"\n" +
						   "    LDC \"\"\n" + 
						   "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    		   "    ALOAD 0\n" +
						   "    GETFIELD instrumenter/test/StateCoverageAsmTest$Dummy.list : Ljava/util/List;\n" + 
			    		   "    LDC \"aaa\"\n" +
			    		   "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    		   "    POP\n" +
			    		   "    RETURN\n");
	}
	
	@Test
	public void testInstrumentAssertWithCalls() {
		
		MethodNode method = result.methods.get(10);
		assertEquals("testAssertWithMethodCall", method.name);
		assertEquals(12, method.instructions.size());
		assertCode(method, 
						   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testAssertWithMethodCall()V\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
						   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.getA()I\"\n" +
						   "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
						   "    LCONST_1\n" +
						   "    ALOAD 0\n" +
						   "    INVOKEVIRTUAL instrumenter/test/StateCoverageAsmTest$Dummy.getA ()I\n" +
						   "    I2L\n" +
						   "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
						   "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testAssertWithMethodCall()V\"\n" +
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
		assertInstrumentation(12, "getListCount", 7, 
				
			    "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.getListCount()I\"\n" +
			    "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.list\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddDependency (Ljava/lang/String;Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD instrumenter/test/StateCoverageAsmTest$Dummy.list : Ljava/util/List;\n" +
			    "    INVOKEINTERFACE java/util/List.size ()I\n" +
			    "    IRETURN\n");
	}
	
	@Test
	public void testAssertListProperties() {
		assertInstrumentation(11, "testAssertListProperties", 17, 

			    "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.BeginTestCapture (Ljava/lang/String;)V\n" +
			    "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.getListCount()I\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.AddAssert (Ljava/lang/String;)V\n" +
			    "    ALOAD 0\n" +
			    "    GETFIELD instrumenter/test/StateCoverageAsmTest$Dummy.list : Ljava/util/List;\n" +
			    "    LDC \"aaa\"\n" +
			    "    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z\n" +
			    "    POP\n" +
			    "    LCONST_1\n" +
			    "    ALOAD 0\n" +
			    "    INVOKESPECIAL instrumenter/test/StateCoverageAsmTest$Dummy.getListCount ()I\n" +
			    "    I2L\n" +
			    "    INVOKESTATIC org/junit/Assert.assertEquals (JJ)V\n" +
			    "    LDC \"instrumenter/test/StateCoverageAsmTest$Dummy.testAssertListProperties()V\"\n" +
			    "    INVOKESTATIC statecoverage/StateCoverage.EndTestCapture (Ljava/lang/String;)V\n" +
			    "    RETURN\n"); 
	}
	
	

}
