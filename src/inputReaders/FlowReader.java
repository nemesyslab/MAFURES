package aeg.inputReaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aeg.dataTypes.Condition;
import aeg.dataTypes.FlowElement;
import aeg.dataTypes.FlowInput;
import aeg.dataTypes.FlowOperation;
import aeg.dataTypes.NumberVar;
import aeg.main.DataSet;

public class FlowReader {

	BufferedReader br = null;
	String[] expArray;
	String output;
	String func;
	FlowElement[] inputs;
	String cond = "none";
	String status; // bunu kulanmayi unutma
	int lineNumber = 0;
	boolean isOutPin = false;
	private String inputPath;
	String sCurrentLine;
	DataSet data;

	public FlowReader(String inputPath, DataSet data) {
		this.data = data;
		this.inputPath = inputPath;
		execute();
	}

	public void execute() {
		try {

			br = new BufferedReader(new FileReader(inputPath));
			String expr = new String("");
			status = "idle";
			while ((sCurrentLine = br.readLine()) != null) {
				lineNumber++;
				sCurrentLine = sCurrentLine.replace(" ", "");
				sCurrentLine = sCurrentLine.replace("\t", "");
				if (sCurrentLine.length() > 1) {
					if (sCurrentLine.substring(0, 2).contains("//")) {
						continue;
					}
				}
				if (sCurrentLine.contains("initiationinterval")) {
					data.setII(Integer.parseInt(sCurrentLine.split(",")[0].split(":")[1]));
					continue;
				}
				expr += sCurrentLine;
				if (expr.contains("else")) {
					cond = "~(" + cond + ")";
					expr = "";
				} else if (expr.indexOf("}") != -1) {
					status = "idle";
					cond = "none";
					expr = "";
				}
				if (expr.contains("if")) {
					expr = expr.replace("if", "");
					expr = expr.replace("(", "");
					expr = expr.replace(")", "");
					expr = expr.replace("{", "");
					cond = expr;
					status = "cond";
					expr = "";
				}
				if (expr.indexOf("else") != -1) {
					expr = "";
				}
				if (expr.indexOf(";") != -1) {
					expr = expr.replace(";", "");
					expArray = expr.split("=");
					output = expArray[0];
					if (expArray[1].contains("(")) {
						func = expArray[1].split("\\(")[0];
						inputs = names2inputs(
								expArray[1].split("\\(")[1].replace(")", "").split(","));
						flushFunction();
						if (output.contains("out") || output.contains("Out")) {
							makeOutPin(expArray[0], expArray[0]);
						}
					} else if (expArray[1].contains("0x") || expArray[1].contains("32'h")) {
						flushHex(expArray[0], expArray[1]);
					} else if (isNumeric(expArray[1])) {
						flushNumeric(expArray[0], expArray[1]);
					} else if (output.contains("out") || output.contains("Out")) {
						makeOutPin(expArray[1], expArray[0]);
					} else {
						flushEquality(expArray[0], expArray[1], cond);
					} // else {
						// System.err.println("unexpected line!!! : line
						// "+lineNumber);
						// }
					expr = "";
				}
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

	private FlowElement[] names2inputs(String[] inputNames) {
		FlowElement[] fis = new FlowElement[inputNames.length];
		for (int i = 0; i < inputNames.length; i++) {
			if (flowHash.containsKey(inputNames[i])) {
				fis[i] = flowHash.get(inputNames[i]);
			} else if (inputPortHash.containsKey(inputNames[i])) {
				fis[i] = inputPortHash.get(inputNames[i]);
			} else if (numberHash.containsKey(inputNames[i])) {
				fis[i] = numberHash.get(inputNames[i]);
			} else if (isNumeric(inputNames[i]) || inputNames[i].contains("0x")
					|| inputNames[i].contains("32'h")) {
				fis[i] = new NumberVar(inputNames[i], inputNames[i]);
			} else {
				fis[i] = new FlowInput(inputNames[i]);
				inputPortHash.put(inputNames[i], (FlowInput) fis[i]);
				data.inputPortList.add((FlowInput) fis[i]);
			}
		}
		return fis;
	}

	private void flushEquality(String target, String var, String cond) {
		// TODO
		// if (flowHash.containsKey(target)) {
		// assigns.add(new FlowOperation(target, var, cond, lineNumber));
		// } else {
		// assigns.add(new FlowOperation(target, var, cond, lineNumber));
		// }
		// numberHash
		System.out.println("bbbbbb " + target);
	}

	private void flushNumeric(String name, String num) {
		numberHash.put(name, new NumberVar(name, num));
		data.numberVars.add(numberHash.get(name));
	}

	private void flushHex(String name, String num) {
		numberHash.put(name, new NumberVar(name, num));
		data.numberVars.add(numberHash.get(name));
	}

	private void makeOutPin(String name, String name2) {
		flowHash.get(name).makeOutPort(name2);
	}

	ArrayList<FlowOperation> assigns = new ArrayList<FlowOperation>();

	Hashtable<String, FlowOperation> flowHash = new Hashtable<String, FlowOperation>();
	Hashtable<String, FlowInput> inputPortHash = new Hashtable<String, FlowInput>();
	Hashtable<String, NumberVar> numberHash = new Hashtable<String, NumberVar>();

	private void flushFunction() {
		if (flowHash.containsKey(output)) {
			if (cond.equals("none")) {
				System.err.println("error here, we need condition");
			} else {
				String[] condIns = cond.split("&&|==|<=|>=|<|>|!=|(\\|\\|)");
				// TODO ||
				FlowElement[] condInsF = names2inputs(condIns);
				String[] ops = new String[condIns.length - 1];
				Pattern pattern = Pattern.compile("&&|==|<=|>=|<|>|!=|(\\|\\|)");
				Matcher matcher = pattern.matcher(cond);
				int jj = 0;
				while (matcher.find()) {
					ops[jj++] = matcher.group(0);
				}
				flowHash.get(output).setAlterStatus(true);
				flowHash.get(output).addAlter(new Condition(condInsF, ops), new FlowOperation(
						data.getFunctionHash().get(func), cond, inputs, output, lineNumber, func));
			}
		} else {
			flowHash.put(output, new FlowOperation(data.getFunctionHash().get(func), cond, inputs,
					output, lineNumber, func));
			data.flowOperations.add(flowHash.get(output));
			// System.out.println(sCurrentLine);
			// System.out.println(data.getFunctionHash().get(func).moduleName);
			// System.out.println();
		}

	}

	public boolean isNumeric(String str) {
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
	}

}
