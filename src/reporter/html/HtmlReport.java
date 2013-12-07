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
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
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
				return arg1.endsWith(".json");
			}
		});	
		
		for (File file : files) {
			
			try {
				FileReader reader = new FileReader(file);
				
				JSONObject json = (JSONObject)JSONValue.parse(reader);
				
				assert(json.containsKey("test_name"));
				assert(json.containsKey("modified_states"));
				assert(json.containsKey("covered_states"));
				assert(json.containsKey("state_coverage"));
				assert(json.containsKey("modified"));
				assert(json.containsKey("covered"));
				
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
				
				JSONArray modified = (JSONArray) json.get("modified");
				JSONArray covered = (JSONArray) json.get("covered");
				
			
				for (Object state : modified) {
					
					String jsonObject = (String) state;
					
					List<String> column = new ArrayList<String>();
					column.add(jsonObject);
					builder.append(generateRow(column, covered.contains(jsonObject)));
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
		
	}
	
	private String generateRow(List<String> columns, boolean green) {
		String result = new String();
		result += "<tr class=\"";
		if (green)
			result += "covered\">";
		else
			result += "uncovered\">";
		
		for (String column : columns) {
			result += "<td>";
			result += column;
			result += "</td>";
		}
		result += "</tr>";
		return result;
		
	}
	
	public void generateHtml() {
		
		StringBuilder builder = new StringBuilder();
		builder
			.append("<!DOCTYPE html>\n")
			.append("<head>\n")
			.append("<title>State Coverage Report</title>\n")
			.append("<!DOCTYPE html>\n")
			.append("<style type=\"text/css\">\n")
			.append(".covered {background-color:#c0ffc0;}\n")
			.append(".uncovered {background-color:#ffa0a0;}\n")
			.append("</style>")

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
		
		System.out.println("Html report generated at " +reportFolder + "/report.html");
		
	}
	

}
