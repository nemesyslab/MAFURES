package aeg.executers;

import java.util.ArrayList;

import aeg.dataTypes.FlowElement;
import aeg.dataTypes.FlowInput;
import aeg.dataTypes.FlowOperation;
import aeg.main.DataSet;

public class RegisterAssigner {
	ArrayList<FlowOperation> myAUnits;
	ArrayList<FlowInput> iVars;
	int II;
	int regSize = 0;
	DataSet data;
	FlowElement activeInput;

	public RegisterAssigner(DataSet data) {
		this.data = data;
		this.II = data.II;
		this.myAUnits = data.flowOperations;
		this.iVars = data.inputPortList;
		setInputRegisters();
		setFlowRegisters();
		data.regSize = regSize;
	}

	public void setInputRegisters() {
		for (FlowElement unit : iVars) {
			unit.initializeRegLocArr();
			itemRegistration(unit);
		}
	}

	public void setFlowRegisters() {
		for (FlowElement unit : myAUnits) {
			unit.initializeRegLocArr();
			itemRegistration(unit);
		}
	}

	private void itemRegistration(FlowElement unit) {
		activeInput = unit;
		int firstAllocCycle = unit.getResultLatency() + 1;
		int releaseCycle = unit.getDiscardCycle() + 1;
		int counter = firstAllocCycle;
		int sPoint = firstAllocCycle;
		while (true) {
			if (releaseCycle == 0)
				break;
			if (counter == releaseCycle) {
				fillBetween(sPoint % II, releaseCycle % II);
				break;
			}
			if (counter % II == II - 1) {
				fillBetween(sPoint % II, II);
				sPoint = counter + 1;
			}
			counter++;
		}
	}

	private void fillBetween(int startPoint, int endPoint) {
		int regIndex = fisrtEmptyRegBetween(startPoint, endPoint);
		fillReg(regIndex, startPoint, endPoint);
	}

	private void fillReg(int regIndex, int startPoint, int endPoint) {
		int registerIndex = regIndex;
		if (regIndex == -1) {
			registerIndex = regSize;
			boolean[] bl = new boolean[II];
			data.registerAvailability.add(bl);
			FlowElement[] unit = new FlowElement[II];
			data.registerAllocation.add(unit);
			regSize++;
		}
		for (int i = startPoint; i < endPoint; i++) {
			data.registerAllocation.get(registerIndex)[i] = activeInput;
			activeInput.updateRegLocArr(registerIndex);
			data.registerAvailability.get(registerIndex)[i] = false;
		}
	}

	private int fisrtEmptyRegBetween(int a, int b) {
		int reg = 0;
		for (int i = a; i < b; i++) {
			if (reg == regSize)
				return -1;
			if (data.registerAvailability.get(reg)[i] == false) {
				reg++;
				i = a - 1;
				continue;
			}
		}
		return reg;
	}

	public int calcMinInternalRegisters() {
		int[] registers = new int[II];
		for (FlowOperation unit : myAUnits) {
			System.out.print(unit.getEQ() + " : ");
			System.out.println(unit.getDiscardCycle() - unit.getResultLatency());
			if (unit.getDiscardCycle() - unit.getResultLatency() > 0)
				for (int i = unit.getResultLatency() + 1; i < unit.getDiscardCycle() + 1; i++)
					registers[i % II]++;
		}
		int max = 0;
		for (int i = 0; i < registers.length; i++) {
			if (registers[i] > max)
				max = registers[i];
		}
		return max;
	}

	public int getRegSize() {
		return regSize;
	}

	// public void initializeRegisterArrays(int n,int m) {
	// int max = n*m;
	// registerAvailability = new boolean[max][throughput];
	// registerAllocation = new AritUnit[max][throughput];
	// for(int i = 0;i<registerAvailability.length;i++)
	// for(int j = 0;j<registerAvailability[i].length;j++)
	// registerAvailability[i][j] = true;
	// }
}
