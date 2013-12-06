package reporter.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import instrumenter.core.Utils;

public class HtmlReport {
	
	private String folder;
	
	public HtmlReport(String folder) {
		this.folder = folder;
	}
	
	private void appendTests(StringBuilder builder) {
		
		File[] files = new File(folder).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg1.endsWith(".json");
			}
		});	
		
		for (File file : files) {
			
			try {
				String content = Utils.readFile(file.getAbsolutePath(), Charset.defaultCharset());
				
				FileReader reader = new FileReader(file);
				
				JSONObject json = (JSONObject)JSONValue.parse(reader);
				
				assert(json.containsKey("test_name"));
				assert(json.containsKey("modified_states"));
				assert(json.containsKey("covered_states"));
				assert(json.containsKey("state_coverage"));
				
				builder
					.append("<tr>")
					.append("<td>")
					.append(json.get("test_name"))
					.append("</td>")
					.append("<td>")
					.append(json.get("modified_states"))
					.append("</td>")
					.append("<td>")
					.append(json.get("covered_states"))
					.append("</td>")
					.append("<td>")
					.append(json.get("state_coverage"))
					.append("</td>")
					.append("</tr>");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
		
	}
	
	public void generateHtml() {
		
		StringBuilder builder = new StringBuilder();
		builder
			.append("<!DOCTYPE html>\n")
			.append("<head>\n")
			.append("<title>State Coverage Report</title>\n")
			.append("<!DOCTYPE html>\n")
			.append("<!DOCTYPE html>\n")
			.append("</head>\n")
			.append("<body>\n")
			.append("<h2>State Coverage Report for Tablelize-it</h2>\n")
			.append("<table border=\"1\">\n")
			.append("<tr>\n")
			.append("<td>Test name</td>\n")
			.append("<td>Modified States</td>\n")
			.append("<td>Covered States</td>\n")
			.append("<td>State Coverage</td>\n");
		
			appendTests(builder);
			
		builder.append("</body>\n")
			.append("</html>\n");
		
		Utils.dumpToFile(folder + "/report.html", builder.toString());
	
	}
	
	public static void main(String[] args) {
		
		String reportFolder = "c:/sc_output";
		
		HtmlReport report = new HtmlReport(reportFolder);
		report.generateHtml();	
		
	}
	

}
