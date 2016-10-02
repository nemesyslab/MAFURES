package aeg.inputReaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class GenericUnitGenerator {

	BufferedReader br = null;
	String[] funcNames;
	String moduleName;
	String[] parameters;
	String[] ports;
	int latency;
	boolean isNamed = false;
	String[] expArray;

	Hashtable<String, IdentifiedModule> modulesHash = new Hashtable<String, IdentifiedModule>();

	public GenericUnitGenerator(String inputPath) {
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(inputPath));
			int lineNumber = 0;
			String expression = new String("");
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				sCurrentLine = sCurrentLine.replace(" ", "");
				sCurrentLine = sCurrentLine.replace("\t", "");
				expression += sCurrentLine;
				if (expression.indexOf(";") != -1) {
					expression = expression.replace(";", "");
					if (expression.contains("FunctionName=")) {
						if (isNamed) {
							flushModule();
						}
						expArray = expression.split("=");
						funcNames = expArray[1].split(",");
						isNamed = true;
					} else if (expression.contains("ModuleName=")) {
						if (isNamed == false) {
							System.err.println(
									"No name declared for module name in line " + lineNumber);
						}
						expArray = expression.split("=");
						moduleName = expArray[1];
					} else if (expression.contains("PassedParameters=")) {
						if (isNamed == false) {
							System.err.println(
									"No name declared for passed parameter in line " + lineNumber);
						}
						expArray = expression.split("=");
						parameters = expArray[1].split(",");
					} else if (expression.contains("PortNames")) {
						if (isNamed == false) {
							System.err.println(
									"No name declared for the in/out ports in line " + lineNumber);
						}
						expArray = expression.split("=");
						ports = expArray[1].split(",");
					} else if (expression.contains("Latency")) {
						if (isNamed == false) {
							System.err
									.println("No name declared for Latency in line " + lineNumber);
						}
						expArray = expression.split("=");
						latency = Integer.parseInt(expArray[1]);
					}
					expression = sCurrentLine.substring(sCurrentLine.indexOf(";"));
					expression = expression.replace(";", "");
				}
			}
			if (isNamed) {
				flushModule();
			}
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

	int opNum = 0;

	private void flushModule() {
		IdentifiedModule iM = new IdentifiedModule(funcNames, moduleName, parameters, latency,
				ports, opNum);
		for (int i = 0; i < funcNames.length; i++) {
			modulesHash.put(funcNames[i].split(":")[0], iM);
		}
		opNum++;
	}

	public Hashtable<String, IdentifiedModule> getModulesHash() {
		return modulesHash;
	}

}
