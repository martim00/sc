import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.Opcodes;

public class MethodParameterExtractor extends MethodVisitor {

	private MethodExtractorCallback callback = null;
	
	public MethodParameterExtractor(MethodVisitor mv) {
		super(Opcodes.ASM4, mv);
	}
	
	public MethodParameterExtractor() {
		super(Opcodes.ASM4);
	}
	

	public void extract(MethodInsnNode instr, MethodExtractorCallback callback) {

		this.callback = callback;

		try {
			extractMethodParameters(instr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * abstract class ParameterExtractor { public abstract InsnList
	 * extract(AbstractInsnNode baseInst); }
	 * 
	 * class MethodCallExtractor extends ParameterExtractor {
	 * 
	 * @Override public InsnList extract(AbstractInsnNode baseInst) {
	 * 
	 * if (baseInst.getPrevious().getOpcode() == INVOKESPECIAL) { MethodInsnNode
	 * method = (MethodInsnNode)baseInst.getPrevious(); String className =
	 * method.owner; String methodName = method.name;
	 * 
	 * InsnList il =
	 * CodeGeneration.generateAssertCode(prepareFullyQualifiedName(className,
	 * methodName)); //
	 * instructions.insert(baseInst.getPrevious().getPrevious(), il);
	 * 
	 * baseInst = baseInst.getPrevious().getPrevious(); return il;
	 * 
	 * }
	 * 
	 * return null; } }
	 */

	// class IntegerToLongExtractor extends ParameterExtractor {
	//
	// @Override
	// public InsnList extract(AbstractInsnNode baseInst) {
	//
	// // conversão de int para long.. @see
	// StateCoverageAsmTest.testShouldInstrumentTests_int()
	// if (baseInst.getPrevious().getOpcode() == I2L) {
	// if (baseInst.getPrevious().getPrevious().getOpcode() == ILOAD) {
	// VarInsnNode node = (VarInsnNode)baseInst.getPrevious().getPrevious();
	// InsnList il =
	// this.generateAssertCode(prepareFullyQualifiedName(this.className,
	// this.name, new Integer(node.var).toString()));
	// //
	// this.instructions.insert(baseInst.getPrevious().getPrevious().getPrevious(),
	// il);
	// baseInst =
	// return il;
	// }
	// }
	// }
	// }

	private AbstractInsnNode resolveMethodCallParameter(
			MethodInsnNode methodCall) throws Exception {

		Type[] argumentTypes = Type.getArgumentTypes(methodCall.desc);

		for (Type type : argumentTypes) {

			if (methodCall.getPrevious().getOpcode() == Opcodes.INVOKESPECIAL) {
				return resolveMethodCallParameter((MethodInsnNode) methodCall
						.getPrevious());
			} else if (methodCall.getPrevious().getOpcode() >= Opcodes.ILOAD
					&& methodCall.getPrevious().getOpcode() <= Opcodes.ALOAD) {
				VarInsnNode loadInsn = (VarInsnNode) methodCall.getPrevious();
				System.out.println("Method depends on " + loadInsn.var);
				return loadInsn.getPrevious();
			} else if (methodCall.getPrevious().getOpcode() == Opcodes.I2L) {
				return methodCall.getPrevious();
			}
		}
		throw new Exception("cant handle this parameter");

	}

	private void extractMethodParameters(MethodInsnNode methodCall) throws Exception {
		
		Type[] argumentTypes = Type.getArgumentTypes(methodCall.desc);
		
		for (Type type : argumentTypes) {
			
			if (methodCall.getPrevious().getOpcode() == Opcodes.INVOKESPECIAL) {
				resolveMethodCallParameter((MethodInsnNode)methodCall.getPrevious());
			}

//			List<MethodParameterExtractor> extractors = new ArrayList<MethodParameterExtractor>();
//			extractors.add(new MethodCallExtractor());

//			for (ParameterExtractor extractor : extractors) {
//				extractor.extract(baseInstr);
//				if (firstParameterCode != null) {
//					baseInstr = instr;
//					break;
//				}
//			}


		}
		
	}
}
