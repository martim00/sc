import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;


public class MethodParameterExtractorTest {
	
	public ClassNode readClass(String className) {
        ClassReader cr = null;
		try {
//			FileInputStream file = new FileInputStream(className);
			cr = new ClassReader(className);
			ClassNode cn = new ClassNode();
			cr.accept(cn, ClassReader.SKIP_DEBUG);
			return cn;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private MethodNode getMethodOfClass(String className, String methodName) throws Exception {
		
		ClassNode classNode = readClass(className);
		for (MethodNode method : classNode.methods) {
			if (method.name.equals(methodName))
				return method;
		}
		
		throw new Exception("cant find method " + methodName);
	}
	
	class Example {
		public void test() {
			
			int a = 0;
			assertEquals(0, a);
		}
	}

	private MethodInsnNode findAssertNode(InsnList il) throws Exception {
	
		Iterator<AbstractInsnNode> it = il.iterator();
		
		while (it.hasNext()) {
			AbstractInsnNode node = it.next();
			if (node instanceof MethodInsnNode) {
				MethodInsnNode methodCall = (MethodInsnNode)node;
				if (methodCall.name.equals("assertEquals"))
					return methodCall;
			}
		}
		
		throw new Exception("cant find the assert ");
	}
	
	class MockMethodParameterCallback implements MethodExtractorCallback {
		
		public List<MethodInsnNode> methodCalls = new ArrayList<MethodInsnNode>();
		public List<VarInsnNode> localVars = new ArrayList<VarInsnNode>();

		@Override
		public void methodCallFound(MethodInsnNode methodCall) {
			methodCalls.add(methodCall);
		}

		@Override
		public void localVarFound(VarInsnNode insn) {
			localVars.add(insn);
		}
		
		
	}
	
	
	@Test
	public void test() throws Exception {
		MethodNode method = getMethodOfClass("MethodParameterExtractorTest$Example", "test");		
		
		MethodInsnNode assertCall = findAssertNode(method.instructions);
		
		MethodParameterExtractor extractor = new MethodParameterExtractor();
		method.accept(extractor);
		
		MockMethodParameterCallback callback = new MockMethodParameterCallback();
		
		extractor.extract(assertCall, callback);
		
		assertEquals(1, callback.localVars.size());
	}

}
