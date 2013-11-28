import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;


public class Utils {

	public static boolean hasTestAnnotation(MethodNode methodNode) {
		if (methodNode.visibleAnnotations == null)
			return false;

		for (AnnotationNode annotation : methodNode.visibleAnnotations) {

			if (annotation.desc.equals("Lorg/junit/Test;"))
				return true;

		}
		return false;
	}
}
