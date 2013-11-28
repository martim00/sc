import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;


interface MethodExtractorCallback 
{
	public void methodCallFound(MethodInsnNode methodCall);
	public void localVarFound(VarInsnNode methodCall);
}