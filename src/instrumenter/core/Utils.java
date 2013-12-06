package instrumenter.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

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

	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	// TODO: remove dup
	public static void dumpToFile(String filename, String content) {
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}


}
