package inputReaders;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Operator2Function {

	BufferedReader br = null;
	int lineNumber = 0;
	String[] expArray;
	String[] operators = { "\\+", "-", "\\*", "/", "==", ">", ">=", "<", "<=", "!=" };
	String[] operatorsX = { "+", "-", "*", "/", "==", ">", ">=", "<", "<=", "!=" };
	String[] functions = { "add", "sub", "mult", "div", "eq", "gt", "ge", "lt", "le", "ne" };

	public Operator2Function(String inputPath, String outputPath) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputPath, "UTF-8");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(inputPath));
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				if (sCurrentLine.replace(" ", "").replace("\t", "").length() > 1) {
					if (sCurrentLine.replace(" ", "").replace("\t", "").substring(0, 2)
							.contains("//")) {
						writer.println(sCurrentLine);
						continue;
					}
				}
				if (sCurrentLine.contains(";")) {
					for (int i = 0; i < operators.length; i++) {
						if (sCurrentLine.contains(operatorsX[i])) {
							sCurrentLine = sCurrentLine.replace(";", " );");
							sCurrentLine = sCurrentLine.replace(operatorsX[i], ",");
							expArray = sCurrentLine.split("=");
							expArray = expArray[1].split(operators[i]);
							sCurrentLine = sCurrentLine.replace(expArray[0],
									" " + functions[i] + "(" + expArray[0]);
							break;
						}
					}
				}
				writer.println(sCurrentLine);
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
