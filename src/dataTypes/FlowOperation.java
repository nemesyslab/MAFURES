package dataTypes;

import aeg.inputReaders.IdentifiedModule;

public class FlowOperation extends FlowElement {
	public IdentifiedModule module;
	FlowElement[] inputs;
	String mod;

	public FlowOperation(IdentifiedModule identifiedModule, String cond, FlowElement[] inputs,
			String output, int lineNumber, String mod) {
		module = identifiedModule;
		name = output;
		this.inputs = inputs;
		this.mod = mod;
	}

	public boolean isOutput = false;

	public void makeOutPort(String outName) {
		isOutput = true;
		this.outName = outName;
	}

	public int getOpNum() {
		return module.getOpNum();
	}

	public FlowElement[] getInputs() {
		return inputs;
	}

	int startLatency = 0;

	public void calcStartLatency(int latency) {
		startLatency = Math.max(startLatency, latency);
	}

	public int getStartLatency() {
		return startLatency;
	}

	public void setResultLatency(int i) {
		resultLatency = i;
	}

	int setResourceDelay;

	public void setResourceDelay(int delay) {
		setResourceDelay = delay;
	}

	boolean haveAlter = false;

	public boolean HaveAlter() {
		return haveAlter;
	}

	public void setAlterStatus(boolean b) {
		haveAlter = b;
	}

	public FlowOperation[] aus;

	public FlowOperation[] getAus() {
		return aus;
	}

	int moduleNum;
	public Condition[] cons;

	@Override
	public String getTrack(int n) {
		if (getResultLatency() == n)
			return module.getFuncName() + "Out" + moduleNum;
		return "reg" + regLocNumZeroPadded(n);
	}

	public int getModuleNum() {
		return moduleNum;
	}

	public void setModuleNum(int i) {
		moduleNum = i;
	}

	public String getEQ() {
		String equ = new String(name + "(" + inputs[0].getEQ());
		for (int i = 1; i < inputs.length; i++) {
			equ += "," + inputs[i].getEQ();
		}
		equ += ")";
		return equ;
	}

	boolean branch = false;
	public int branchWidth = 0;

	public void addAlter(Condition con, FlowOperation au) {
		branch = true;
		branchWidth++;
		Condition[] consTemp = new Condition[branchWidth];
		FlowOperation[] ausTemp = new FlowOperation[branchWidth];
		for (int i = 0; i < branchWidth - 1; i++) {
			consTemp[i] = cons[i];
			ausTemp[i] = aus[i];
		}
		consTemp[branchWidth - 1] = con;
		ausTemp[branchWidth - 1] = au;
		cons = consTemp;
		aus = ausTemp;
	}

	public String printAlterCon(int i) {
		return cons[i].conditionProducer(startLatency);
	}

	public String printAlterUnit(int unit, int cycle, int i) {
		return printInSM(aus[i].inputs, unit, cycle);
	}

	public String printInMux(FlowElement[] in0, int unit, int cycle) {
		String s = "";
		for (int i = 0; i < module.getInputNames(unit).length; i++) {
			s += in0[i].getTrack(getStartLatency()) + ", ";
		}
		if (module.isThereOpcode)
			s += mod + ", ";
		return s;
	}

	public String printInMux(int unit, int cycle) {
		return printInMux(inputs, unit, cycle);
	}

	public String printInSM(FlowElement[] in0, int unit, int cycle) {
		String s = "";
		for (int i = 0; i < module.getInputNames(unit).length; i++) {
			s += module.getInputNames(unit)[i] + " = " + in0[i].getTrack(getStartLatency()) + "; ";
		}
		if (module.isThereOpcode)
			s += module.getModName(unit) + " = " + mod + "; ";
		return s;
	}

	public String printInSM(int unit, int cycle) {
		return printInSM(inputs, unit, cycle);
	}

	String outName;

	public String getOutPortName() {
		if (isOutput)
			return outName;
		else {
			System.err.println("This is not an ourput!!!");
			return null;
		}
	}
}
