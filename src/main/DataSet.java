package main;

import java.util.ArrayList;
import java.util.Hashtable;

import aeg.dataTypes.FlowElement;
import aeg.dataTypes.FlowInput;
import aeg.dataTypes.FlowOperation;
import aeg.dataTypes.NumberVar;
import aeg.inputReaders.IdentifiedModule;

public class DataSet {
	public boolean pipelinedMux = false; // TODO
	public int II = 1;
	public int bitWidth = 32;
	public String moduleName = "test_v1";
	Hashtable<String,IdentifiedModule> functionHash;
	public ArrayList<FlowOperation> flowOperations = new ArrayList<FlowOperation>();
	public ArrayList<FlowInput> inputPortList = new ArrayList<FlowInput>();
	public ArrayList<NumberVar> numberVars = new ArrayList<NumberVar>();
	public String[] passedParameterList = {"sub = 1","add = 0","lt = 1","gt = 2"}; // TODO
	
	public ArrayList<FlowElement[]> registerAllocation= new ArrayList<FlowElement[]>();
	public ArrayList<boolean[]> registerAvailability= new ArrayList<boolean[]>();
	public int regSize;
	
	public FlowOperation[][][] timingArray;
	public boolean[][][] slotAvailability;
	public int[] moduleCounts;
	
	public boolean outputLastCycleMode = false;
	
	public DataSet(Hashtable<String,IdentifiedModule> functionHash) {
		this.functionHash = functionHash;
	}

	int getII() {
		return II;
	}

	public void setII(int II) {
		this.II = II;
	}

	public Hashtable<String, IdentifiedModule> getFunctionHash() {
		return functionHash;
	}

	public void setFunctionHash(Hashtable<String, IdentifiedModule> functionHash) {
		this.functionHash = functionHash;
	}

	public int getBitWidth() {
		return bitWidth;
	}

	public void setModuleCounts(int[] calcFPUReq) {
		moduleCounts = calcFPUReq;
	}

}
