package aeg.inputReaders;

public class IdentifiedModule {
	int opNum;
	String[] funcNames;
	int opLatency;
	String moduleName;
	String[] parameters;
	String[] ports;
	public String moduleAbb = "";
	int inputCount = 0;
	public boolean isThereOpcode = false;

	String inputPortNames[];
	String outputPortName;

	boolean outputReg = true;
	boolean inputReg = true;
	boolean pipelinedMux = false;

	// TODO remove opNum
	public IdentifiedModule(String[] funcNames, String moduleName, String[] parameters, int latency,
			String[] ports, int opNum) {
		this.opNum = opNum;
		this.funcNames = funcNames;
		this.opLatency = latency;
		this.moduleName = moduleName;
		this.parameters = parameters;
		this.ports = ports;
		if (funcNames.length == 1)
			moduleAbb = funcNames[0];
		else
			for (int i = 0; i < funcNames.length; i++) {
				moduleAbb += funcNames[i].substring(0, funcNames[i].indexOf(":"));
			}
		for (int i = 0; i < ports.length; i++) {
			if (ports[i].contains("input:"))
				inputCount++;
		}
		inputPortNames = new String[inputCount];
		int index = 0;
		for (int i = 0; i < ports.length; i++) {
			if (ports[i].contains("input:")) {
				inputPortNames[index++] = moduleAbb + "In" + "MUZLUKEK" + (char) (Character
						.getNumericValue(ports[i].charAt(ports[i].indexOf(":") + 1)) + 97);
				String temp = "MUZLUKEK" + (char) (Character
						.getNumericValue(ports[i].charAt(ports[i].indexOf(":") + 1)) + 97);
				ports[i] = ports[i].replace(":" + ports[i].charAt(ports[i].indexOf(":") + 1), temp);
				ports[i] = ports[i].replace("input", moduleAbb + "In");
			}
			if (ports[i].contains("opcode:")) {
				ports[i] = ports[i].replace("opcode:", moduleAbb + "Mod" + "MUZLUKEK");
				isThereOpcode = true;
			}
			if (ports[i].contains("output:")) {
				if (outputReg) {
					ports[i] = ports[i].replace("output:", moduleAbb + "Out" + "MUZLUKEKNext");
				} else {
					ports[i] = ports[i].replace("output:", moduleAbb + "Out" + "MUZLUKEK");
				}
				outputPortName = moduleAbb + "Out";
			}
		}
	}

	public int getOpLatency() {
		return opLatency;
	}

	public int getOpNum() {
		return opNum;
	}

	public String getFuncName() {
		String name = "";
		for (int i = 0; i < funcNames.length; i++) {
			if (funcNames[i].contains(":"))
				name += funcNames[i].substring(0, funcNames[i].indexOf(":"));
			else
				name += funcNames[i];
		}
		return name;
	}

	public String[] moduleInstantiationText(int moduleNum) {
		int stringSize = ports.length + parameters.length + 3 + 2;
		if (inputReg)
			stringSize++;
		if (isThereOpcode)
			stringSize++;
		String ss[] = new String[stringSize];
		int index = 0;
		String s;
		if (pipelinedMux) {
			s = "// comment";
		} else {
			s = "reg [BitWidth-1:0] ";
			for (int i = 0; i < inputCount; i++) {
				if (i != inputCount - 1)
					s += moduleAbb + "In" + Integer.toString(moduleNum) + (char) ('a' + i) + ", ";
				else
					s += moduleAbb + "In" + Integer.toString(moduleNum) + (char) ('a' + i) + ";";
			}
		}
		ss[index++] = s;
		if (pipelinedMux) {
			if (isThereOpcode)
				if (inputReg)
					ss[index++] = "// comment";
		} else {
			if (isThereOpcode)
				if (inputReg)
					ss[index++] = "reg [BitWidth-1:0] " + moduleAbb + "Mod"
							+ Integer.toString(moduleNum) + "Next; reg [BitWidth-1:0] " + moduleAbb
							+ "Mod" + Integer.toString(moduleNum) + ";";
				else
					ss[index++] = "reg [BitWidth-1:0] " + moduleAbb + "Mod"
							+ Integer.toString(moduleNum) + ";";
		}
		if (inputReg) {
			if (pipelinedMux) {
				s = "// comment";
				ss[index++] = s;
			} else {
				s = "reg [BitWidth-1:0] ";
				for (int i = 0; i < inputCount; i++) {
					if (i != inputCount - 1)
						s += moduleAbb + "In" + Integer.toString(moduleNum) + (char) ('a' + i)
								+ "Next, ";
					else
						s += moduleAbb + "In" + Integer.toString(moduleNum) + (char) ('a' + i)
								+ "Next;";
				}
				ss[index++] = s;
			}
		}
		if (outputReg) {
			ss[index++] = "wire [BitWidth-1:0] " + moduleAbb + "Out" + Integer.toString(moduleNum)
					+ "Next; reg [BitWidth-1:0] " + moduleAbb + "Out" + Integer.toString(moduleNum)
					+ ";";
		} else {
			ss[index++] = "wire [BitWidth-1:0] " + moduleAbb + "Out" + Integer.toString(moduleNum)
					+ ";";
		}
		if (parameters == null) {
			ss[index++] = moduleName;
			ss[index++] = moduleName + "_U" + moduleNum + "(";
		} else {
			ss[index++] = moduleName + " #(";
			for (int i = 0; i < parameters.length - 1; i++) {
				ss[index++] = "\t" + parameters[i] + ",";
			}
			ss[index++] = "\t" + parameters[parameters.length - 1] + ")";
		}
		ss[index++] = moduleName + "_U" + moduleNum + "(";
		for (int i = 0; i < ports.length - 1; i++) {
			ss[index++] = "\t" + ports[i].replace("MUZLUKEK", Integer.toString(moduleNum)) + ",";
		}
		ss[index++] = "\t"
				+ ports[ports.length - 1].replace("MUZLUKEK", Integer.toString(moduleNum));
		ss[index++] = ");";
		return ss;
	}

	public String[] getInputNames(int moduleNum) {
		String[] ss = new String[inputCount];
		for (int i = 0; i < inputCount; i++) {
			if (inputReg)
				ss[i] = inputPortNames[i].replace("MUZLUKEK", Integer.toString(moduleNum)) + "Next";
			else
				ss[i] = inputPortNames[i].replace("MUZLUKEK", Integer.toString(moduleNum));
		}
		return ss;
	}

	public String getOutputName(int moduleNum) {
		return moduleAbb + "Out" + Integer.toString(moduleNum);
	}

	public String[] printDefaultAssignment(int moduleNum) {
		String[] ss;
		if (isThereOpcode)
			ss = new String[inputPortNames.length + 1];
		else
			ss = new String[inputPortNames.length];
		for (int i = 0; i < inputPortNames.length; i++) {
			if (inputReg)
				ss[i] = inputPortNames[i].replace("MUZLUKEK", Integer.toString(moduleNum))
						+ "Next = 'bx;";
			else
				ss[i] = inputPortNames[i].replace("MUZLUKEK", Integer.toString(moduleNum))
						+ " = 'bx;";
		}
		if (isThereOpcode)
			if (inputReg)
				ss[inputPortNames.length] = moduleAbb + "Mod" + Integer.toString(moduleNum)
						+ "Next = 'bx;";
			else
				ss[inputPortNames.length] = moduleAbb + "Mod" + Integer.toString(moduleNum)
						+ " = 'bx;";
		return ss;
	}

	public String[] printRegAssing(int moduleNum) {
		String ss[];
		int size = 0;
		if (outputReg) {
			size += 1;
		}
		if (pipelinedMux) // TODO
			inputReg = false;
		if (inputReg) {
			size += inputCount;
		}
		if (inputReg)
			if (isThereOpcode)
				size++;
		ss = new String[size];
		int index = 0;
		if (inputReg) {
			for (int i = 0; i < inputPortNames.length; i++) {
				ss[index++] = inputPortNames[i].replace("MUZLUKEK", Integer.toString(moduleNum))
						+ " <= "
						+ inputPortNames[i].replace("MUZLUKEK", Integer.toString(moduleNum))
						+ "Next;";
			}
			if (isThereOpcode)
				ss[index++] = moduleAbb + "Mod" + moduleNum + " <= " + moduleAbb + "Mod" + moduleNum
						+ "Next;";
		}
		if (outputReg) {
			ss[index++] = outputPortName + Integer.toString(moduleNum) + " <= " + outputPortName
					+ Integer.toString(moduleNum) + "Next;";
		}
		return ss;
	}

	public String getModName(int moduleNum) {
		if (inputReg) {
			return moduleAbb + "Mod" + moduleNum + "Next";
		} else {
			return moduleAbb + "Mod" + moduleNum;
		}
	}

}
