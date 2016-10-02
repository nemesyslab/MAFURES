package aeg.main;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import aeg.dataTypes.FlowOperation;

public class Reporter {

	StringBuilder sb = new StringBuilder();
	boolean firstTable = true;
	String fileName;

	public Reporter(DataSet data, String fileName) {
		this.fileName = fileName;
		String[] fpu_names = { "ADD-SUB", "MULTIPLIER", "DIVIDER", "SQRT", "ABS", "COMP" };
		for (int i = 0; i < data.moduleCounts.length; i++) {
			if (data.timingArray[i] == null)
				continue;
			unitEfficiency(data.timingArray[i], data.slotAvailability[i],
					fpu_names[i]);
		}
		registerEfficiency(data);
		for (int i = 0; i < data.moduleCounts.length; i++)
			printMatrix(data.timingArray[i], data.slotAvailability[i], fpu_names[i]);
		registerTablePrinter(data);
		PrintReport(fileName);
	}

	public void registerEfficiency(DataSet data) {
		long totalSlot = 0;
		long emptySlot = 0;
		for (int i = 0; i < data.registerAllocation.size(); i++) {
			for (int j = 0; j < data.registerAllocation.get(i).length; j++) {
				totalSlot++;
				if (data.registerAllocation.get(i)[j] == null) {
					emptySlot++;
				}
			}
		}
		sb.append("                        <<<<<<<<<<REGISTERS>>>>>>>>>" + "\n");
		sb.append(
				"Total number of register: " + data.registerAllocation.size() + "\n");
		sb.append("Total register slot: " + totalSlot + "\n");
		sb.append("Total empty slot: " + emptySlot + "\n");
		sb.append("Register utilization: " + (100 - ((double) emptySlot / (double) totalSlot) * 100)
				+ "\n\n");
	}

	public void unitEfficiency(FlowOperation[][] timing, boolean[][] scheduleAvailability, String name) {
		long totalSlot = 0;
		long emptySlot = 0;
		sb.append("                        <<<<<<<<<<" + name + ">>>>>>>>>>" + "\n");
		for (int i = 0; i < timing.length; i++) {
			for (int j = 0; j < timing[0].length; j++) {
				totalSlot++;
				if (scheduleAvailability[i][j] == false) {
				} else {
					emptySlot++;
				}
			}
		}
		sb.append("Total number of " + name + " unit: " + timing.length + "\n");
		sb.append("Total  " + name + " unit slot: " + totalSlot + "\n");
		sb.append("Total empty " + name + " unit slot: " + emptySlot + "\n");
		sb.append("Unit utilization: " + (100 - ((double) emptySlot / (double) totalSlot) * 100)
				+ "\n");
		sb.append("\n");
	}

	public void registerTablePrinter(DataSet data) {
		for (int i = 0; i < data.registerAllocation.size(); i++) {
			sb.append("register: " + i + "\n");
			for (int j = 0; j < data.registerAllocation.get(i).length; j++) {
				sb.append("cycle: " + j + "  ->  ");
				if (data.registerAllocation.get(i)[j] == null) {
					sb.append("idle   ");
				} else {
					sb.append(data.registerAllocation.get(i)[j].getName());
					sb.append("   ");
				}
			}
			sb.append("\n");
		}
	}

	public void printMatrix(FlowOperation[][] timing, boolean[][] scheduleAvailability, String name) {
		if (firstTable) {
			firstTable = false;
			sb.append("                        <<<<<<<<<<The Great Schedule>>>>>>>>>>");
			sb.append("\n");
		}
		if (timing.length == 0) return;
		sb.append("                        <<<<<<<<<<" + name + ">>>>>>>>>>" + "\n");
		sb.append("  ");
		for (int j = 0; j < timing.length; j++)
			sb.append("**************" + j + "***************");
		sb.append("\n");
		for (int j = 0; j < timing[0].length; j++) {
			sb.append(j + "-");
			for (int i = 0; i < timing.length; i++) {
				if (scheduleAvailability[i][j] == false) {
					sb.append(timing[i][j].getName());
					for (int k = 0; k < (30 - timing[i][j].getName().length()); k++)
						sb.append("*");
				} else {
					for (int k = 0; k < 30; k++)
						sb.append("*");
				}
			}
			sb.append("\n");
		}
	}

	public void PrintReport(String ReportName) {

		File f = new File(ReportName);

		try {
			FileWriter fwriter = new FileWriter(f);
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write(sb.toString());
			bwriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
