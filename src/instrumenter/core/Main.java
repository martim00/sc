package instrumenter.core;
import java.io.IOException;


public class Main {
	
	public static void main(String[] args) {
		
		System.out.println("Usage: sc [input_folder] [output_folder]");
		
//		String inputFolder = "C:/Users/Martim/workspace/table/java";
		String inputFolder = "C:/Users/Aniceto/workspace/junit_original";
//		String inputFolder = "C:/Users/Aniceto/workspace/statecoverage/externals/tablelize_it/java";
//		String inputFolder = "C:/Users/Aniceto/workspace/tablelize_it/java";
		
		
		String outputFolder = "C:/sc_junit";		
		
		inputFolder = args[0];
		System.out.println("Input folder: " + inputFolder);
		outputFolder = args[1];
		System.out.println("Output folder: " + outputFolder);	
		
		
		StateCoverageAsm instrumenter = new StateCoverageAsm();
		try {
			instrumenter.instrumentFolder(inputFolder, outputFolder);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
