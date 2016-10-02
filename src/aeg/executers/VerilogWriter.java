package aeg.executers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import aeg.dataTypes.FlowElement;
import aeg.dataTypes.FlowInput;
import aeg.dataTypes.FlowOperation;
import aeg.dataTypes.NumberVar;
import aeg.main.DataSet;

public class VerilogWriter {
	DataSet data;

	int zipLvl = 20;
	String specialStartText = "Engineer aydin emre guzel";
	String specialEndText = "bye bye";
	String specialStartCode[];
	String specialEndCode[];
	String parameterList[] = new String[1];
	String fileName;

	public VerilogWriter(DataSet data, String fileName) {
		this.data = data;
		this.fileName = fileName;
		parameterList[0] = ("BitWidth = " + data.getBitWidth());
		produceRTLCode();
	}

	public void produceRTLCode() {
		preModule();
		indentedPrint("module " + data.moduleName);
		addParameters();
		moduleIOdeclaration();
		addStateCounter();
		passedParams();
		regDeclarations();
		moduleInstantiations();
		if (data.pipelinedMux)
			multiplexerInstantiation();
		registerAssignments();
		combinationalLogic();
		indentedPrint("endmodule");
		postModule();
		produceTextFile(fileName);
	}

	private void multiplexerInstantiation() {
		// TODO
		String moduleAbb = "Error";
		int muxNum = 0;
		for (int j = 0; j < data.timingArray.length; j++) {
			for (int i = 0; i < data.timingArray[j].length; i++) {
				if (j == 0 || j == 4)
					indentedPrint("mux12_in3 U" + muxNum + "(");
				else if (j == 3)
					indentedPrint("mux12_in1 U" + muxNum + "(");
				else
					indentedPrint("mux12_in2 U" + muxNum + "(");
				muxNum++;
				for (int caseNumber = 0; caseNumber < data.II; caseNumber++) {
					if (data.slotAvailability[j][i][caseNumber] == false) {
						indentedPrint("\t"
								+ data.timingArray[j][i][caseNumber].printInMux(i, caseNumber));
						moduleAbb = data.timingArray[j][i][caseNumber].module.moduleAbb;
						// int count = 0;
						// while (data.timingArray[j][i][caseNumber].branchWidth
						// > count) {
						// indentedPrint("if ( "
						// +
						// data.timingArray[j][i][caseNumber].printAlterCon(count)
						// + " ) begin");
						// indentedPrint(data.timingArray[j][i][caseNumber].printAlterUnit(i,
						// caseNumber, count));
						// indentedPrint("end");
						// count++;
						// }
					} else {
						if (j == 0 || j == 4)
							indentedPrint("\t'bx,'bx,'bx,");
						else if (j == 3)
							indentedPrint("\t'bx,");
						else
							indentedPrint("\t'bx,'bx,");
					}
				}
				for (int k = 0; k < 3; k++) {
					if (j == 0 || j == 4)
						indentedPrint("\t'bx,'bx,'bx,");
					else if (j == 3)
						indentedPrint("\t'bx,");
					else
						indentedPrint("\t'bx,'bx,");
				}
				indentedPrint("\tstate,");
				indentedPrint("\tmux12" + moduleAbb + i + "_out" + 0 + ",");
				if (j != 3)
					indentedPrint("\tmux12" + moduleAbb + i + "_out" + 1 + ",");
				if (j == 0 || j == 4)
					indentedPrint("\tmux12" + moduleAbb + i + "_out" + 2 + ",");
				indentedPrint("\tclk");
				indentedPrint(");");
				indentedPrint("wire [BitWidth-1:0] mux12" + moduleAbb + i + "_out" + 0 + ";");
				if (j != 3)
					indentedPrint("wire [BitWidth-1:0] mux12" + moduleAbb + i + "_out" + 1 + ";");
				if (j == 0 || j == 4)
					indentedPrint("wire [5:0] mux12" + moduleAbb + i + "_out" + 2 + ";");
			}
		}

	}

	private void addStateCounter() {
		indentedPrint("reg [" + log2(data.II) + ":0] state, stateNext;");
		indentedPrint("always@(*) begin");
		indentedPrint("if(rst)begin");
		indentedPrint("stateNext = 0;");
		indentedPrint("end else begin");
		indentedPrint("stateNext = state + 1;");
		indentedPrint("if(state == " + (data.II - 1) + " )begin");
		indentedPrint("stateNext = 0;");
		indentedPrint("end");
		indentedPrint("end");
		indentedPrint("end");
		indentedPrint("always@(posedge clk) begin");
		indentedPrint("state <= stateNext;");
		indentedPrint("end");
	}

	private void preModule() {
		commentPrint(specialStartText);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		commentPrint(dateFormat.format(cal.getTime()));
		specialStartCode = new String[1];
		specialStartCode[0] = "`timescale 1 ns / 1 ps";
		// specialStartCode[1] = "(*
		// CORE_GENERATION_INFO=\"iter_v1,hls_ip_2015_4,{HLS_INPUT_TYPE=c,HLS_INPUT_"
		// +
		// "FLOAT=1,HLS_INPUT_FIXED=0,HLS_INPUT_PART=xc7k160tfbg484-1,HLS_INPUT_CLOCK=1.000000,HLS_"
		// +
		// "INPUT_ARCH=pipeline,HLS_SYN_CLOCK=2.519000,HLS_SYN_LAT=312,HLS_SYN_TPT=10,HLS_SYN_MEM=0"
		// + ",HLS_SYN_DSP=6,HLS_SYN_FF=11451,HLS_SYN_LUT=8011}\" *)";
		indentedPrint(specialStartCode);
	}

	private void addParameters() {
		String parameterS = "";
		for (int i = 0; i < parameterList.length; i++) {
			parameterS += "parameter " + parameterList[i];
			parameterS += i != parameterList.length - 1 ? " , " : "";
		}
		indentedPrint("#( " + parameterS + " )");
	}

	private void passedParams() {
		for (int i = 0; i < data.passedParameterList.length; i++)
			indentedPrint("localparam " + data.passedParameterList[i] + ";");
		for (NumberVar numVar : data.numberVars) {
			indentedPrint("localparam " + numVar.name + " = " + numVar.num + ";");
		}
	}

	private void moduleIOdeclaration() {
		indentedPrint("(");
		indentedPrint("input rst,");
		for (FlowInput IVar : data.inputPortList) {
			indentedPrint("input [BitWidth-1:0] " + IVar.getName() + ",");
		}
		for (FlowOperation unit : data.flowOperations) {
			if (unit.isOutput) {
				indentedPrint("output reg [BitWidth-1:0] " + unit.getOutPortName() + ",");
			}
		}
		indentedPrint("input clk");
		indentedPrint(");");
	}

	private void regDeclarations() {
		for (int i = 0; i < data.regSize; i++)
			matrixTypePrint("reg [BitWidth-1:0] reg" + padZero(i) + ",reg" + padZero(i) + "Next;",
					i, zipLvl, data.regSize - 1 == i);
	}

	private void moduleInstantiations() {
		for (int i = 0; i < data.timingArray.length; i++) {
			for (int j = 0; j < data.timingArray[i].length; j++) {
				int c = 0;
				while (data.slotAvailability[i][j][c] == true) {
					c++;
				}
				indentedPrint(data.timingArray[i][j][c].module.moduleInstantiationText(j));
			}
		}
	}

	private void combinationalLogic() {
		indentedPrint("always@(*) begin");

		for (int i = 0; i < data.regSize; i++)
			matrixTypePrint("reg" + padZero(i) + "Next = reg" + padZero(i) + ";", i, zipLvl,
					i == data.regSize - 1);

		if (data.pipelinedMux) {

		} else {
			for (int i = 0; i < data.timingArray.length; i++) {
				for (int j = 0; j < data.timingArray[i].length; j++) {
					for (int k = 0; k < data.timingArray[i][j].length; k++) {
						if (data.timingArray[i][j][k] != null) {
							indentedPrint(
									data.timingArray[i][j][k].module.printDefaultAssignment(j));
							break;
						}
					}
				}
			}
		}
		for (int i = 0; i < data.timingArray.length; i++) {
			for (int j = 0; j < data.timingArray[i].length; j++) {
				for (int k = 0; k < data.timingArray[i][j].length; k++) {
					if (data.timingArray[i][j][k] != null) {
						if (data.timingArray[i][j][k].isOutput) {
							if (data.outputLastCycleMode) {
								indentedPrint(data.timingArray[i][j][k].name + " = reg"
										+ padZero(
												data.timingArray[i][j][k].registerLocArray[data.timingArray[i][j][k].registerLocArray.length
														- 1])
										+ ";");
							} else {
								indentedPrint(data.timingArray[i][j][k].getOutPortName() + " = "
										+ data.timingArray[i][j][k].module.getOutputName(j) + ";");
								// TODO generalize here
							}
						}
					}
				}
			}
		}
		stateMachinePrinter(sb);

		indentedPrint("end");
	}

	private void registerAssignments() {
		indentedPrint("always@(posedge clk) begin");
		for (int i = 0; i < data.timingArray.length; i++) {
			for (int j = 0; j < data.timingArray[i].length; j++) {
				for (int k = 0; k < data.timingArray[i][j].length; k++) {
					if (data.timingArray[i][j][k] != null) {
						indentedPrint(data.timingArray[i][j][k].module.printRegAssing(j));
						break;
					}
				}
			}
		}

		for (int i = 0; i < data.regSize; i++)
			matrixTypePrint("reg" + padZero(i) + " <= reg" + padZero(i) + "Next;", i, zipLvl,
					i == data.regSize - 1);
		// sb.append("reg" + i + " <= #1 reg" + i + "Next; ");
		// indentedPrint("reg" + i + " <= #1 reg" + i + "Next;");

		indentedPrint("end");
	}

	private void stateMachinePrinter(StringBuilder sb) {
		indentedPrint("case(state)");

		for (int caseNumber = 0; caseNumber < data.II; caseNumber++) {
			indentedPrint(caseNumber + ":begin");

			if (data.pipelinedMux == false) {
				for (int j = 0; j < data.timingArray.length; j++) {
					for (int i = 0; i < data.timingArray[j].length; i++) {
						if (data.slotAvailability[j][i][caseNumber] == false) {
							indentedPrint("// " + data.timingArray[j][i][caseNumber].getName());
							indentedPrint(
									data.timingArray[j][i][caseNumber].printInSM(i, caseNumber));
							int count = 0;
							while (data.timingArray[j][i][caseNumber].branchWidth > count) {
								indentedPrint("if ( "
										+ data.timingArray[j][i][caseNumber].printAlterCon(count)
										+ " ) begin");
								indentedPrint(data.timingArray[j][i][caseNumber].printAlterUnit(i,
										caseNumber, count));
								indentedPrint("end");
								count++;
							}
						}
					}
				}
			}
			for (FlowOperation unit : data.flowOperations) {
				if (caseNumber == unit.getResultLatency() % data.II) {
					if (unit.getDiscardCycle() - unit.getResultLatency() > 0) {
						indentedPrint("// " + unit.getName());
						// TODO output sayýsýna göre basýlmalý
						indentedPrint("reg" + padZero(unit.registerLocArray[0]) + "Next = "
								+ unit.module.getOutputName(unit.getModuleNum()) + ";");
						// TODO regLocArr üzerinden yapýlacak
						// TODO 0 kaldýrýlacak
						// TODO outputun registere veya inputa gireceðini unit
						// classý bilmeli
					}
				}
			}
			if (caseNumber == 0) {
				for (FlowInput unit : data.inputPortList) {
					if (unit.getDiscardCycle() > 0) {
						indentedPrint("reg" + padZero(unit.registerLocArray[0]) + "Next = "
								+ unit.getName() + ";");
					}
				}
			}
			if (caseNumber == data.II - 1) {
				for (FlowElement unit : data.flowOperations) {
					int temp = unit.registerLocArray[0];
					for (int i = 1; i < unit.registerLocArray.length; i++) {
						if (temp != unit.registerLocArray[i]) {
							indentedPrint("reg" + padZero(unit.registerLocArray[i]) + "Next = "
									+ "reg" + padZero(temp) + ";");
						}
						// TODO buradan unit latency + i ile bir kontrol
						// yapýlabilir
						temp = unit.registerLocArray[i];
					}
				}
				for (FlowInput unit : data.inputPortList) {
					int temp = unit.registerLocArray[0];
					for (int i = 1; i < unit.registerLocArray.length; i++) {
						if (temp != unit.registerLocArray[i]) {
							indentedPrint("reg" + padZero(unit.registerLocArray[i]) + "Next = "
									+ "reg" + padZero(temp) + ";");
						}
						temp = unit.registerLocArray[i];
					}
				}
			}
			indentedPrint("end");
		}
		indentedPrint("default: begin");
		indentedPrint("end");
		indentedPrint("endcase");
	}

	StringBuilder sb = new StringBuilder(); // "\t\t"
	int tabSpace = 0;

	private void indentedPrint(String s) {
		if (!s.contains("endcase") && (s.contains("end") || s.contains("endmodule")))
			tabSpace--;
		for (int i = 0; i < tabSpace; i++)
			sb.append("\t");
		if (s.contains("begin") || (s.contains("module") && !s.contains("endmodule")))
			tabSpace++;
		sb.append(s + "\n");
	}

	private void indentedPrint(String[] ss) {
		for (int i = 0; i < ss.length; i++) {
			if (ss[i] == null)
				System.out.println("null" + i + "");
			indentedPrint(ss[i]);
		}
	}

	private void postModule() {
		// TODO read file to wirte
		// specialEndCode = new String[6];
		// specialEndCode[0] = "module fpu_abs(clk,in,out);";
		// specialEndCode[1] = "input clk; input [31:0] in; output reg[31:0]
		// out;";
		// specialEndCode[2] = "always@(posedge clk)";
		// specialEndCode[3] = "begin out <= {1'b0,in[30:0]};";
		// specialEndCode[4] = "end";
		// specialEndCode[5] = "endmodule";
		// indentedPrint(specialEndCode);
		// textPrint(specialEndText);
	}

	private void produceTextFile(String fileName) {
		File f;
		if (fileName.contains(".v"))
			f = new File(fileName);
		else
			f = new File(fileName + ".v");

		try {
			FileWriter fwriter = new FileWriter(f);
			BufferedWriter bwriter = new BufferedWriter(fwriter);
			bwriter.write(sb.toString());
			bwriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String st = "";

	private void matrixTypePrint(String s, int order, int columnSize, boolean lastItem) {
		st += s + " ";
		if (order % columnSize == columnSize - 1 || lastItem) {
			indentedPrint(st);
			st = "";
		}
	}

	private void commentPrint(String s) {
		sb.append("//   " + s + "\n");
	}

	private String padZero(int n) {
		int maxDigit = 3; // TO DO make this parameter
		if (n > Math.pow(10, maxDigit) - 1) {
			System.err.println("exceed problem in zero pad, update maxDigit var");
			System.exit(0);
		}
		String zeros = "";
		final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999,
				Integer.MAX_VALUE };
		int digitNum = 0;
		for (int i = 0;; i++)
			if (n <= sizeTable[i]) {
				digitNum = i + 1;
				break;
			}
		for (int j = 0; j < maxDigit - digitNum; j++)
			zeros += "0";
		return zeros + n;
	}

	private int log2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return 31 - Integer.numberOfLeadingZeros(n);
	}
}
