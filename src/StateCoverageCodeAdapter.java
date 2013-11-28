import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class StateCoverageCodeAdapter extends MethodVisitor {

	private String className;
	private List<String> stack = new ArrayList<String>();
	
	public StateCoverageCodeAdapter(MethodVisitor mv, String className) {

		super(Opcodes.ASM4, mv);
		this.className = className;
	}

	@Override
	public void visitVarInsn(final int opcode, final int var) {
		super.visitVarInsn(opcode, var);
		
		if (opcode >= Opcodes.ILOAD && opcode <= Opcodes.ALOAD) {
			
			stack.add(new Integer(var).toString());
						
		}
		else if (opcode >= Opcodes.ISTORE && opcode <= Opcodes.ASTORE) {
			
//			mv.visitLdcInsn("target");
//			mv.visitLdcInsn("source");
//			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "statecoverage/StateCoverage", "AddDependency", "(Ljava/lang/String;Ljava/lang/String;)V");
			
			stack.remove(stack.size() - 1);
		}
	}

}
