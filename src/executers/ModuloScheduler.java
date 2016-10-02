package executers;

import java.util.ArrayList;

import aeg.dataTypes.FlowOperation;
import aeg.main.DataSet;

public class ModuloScheduler {

	FlowOperation[][][] timingArray;
	boolean[][][] slotAvailability;
	int[] moduleCounts;

	public ModuloScheduler(DataSet data) {
		// TODO
		data.setModuleCounts(calcFPUReq(data.II, data.flowOperations));
		moduleCounts = data.moduleCounts;
		initializeScheduleArrays(data.II);
		locateUnits(data.II, data.flowOperations);
		data.timingArray = timingArray;
		data.slotAvailability = slotAvailability;

		// TODO bunlar iceriden hallediliyot olmali
		int maxLatency = 0;
		for (FlowOperation unit : data.flowOperations) {
			if (unit.getResultLatency() > maxLatency)
				maxLatency = unit.getResultLatency();
		}
		System.out.println("Total Latency: " + maxLatency);
		for (FlowOperation unit : data.flowOperations) {
			if (unit.getDiscardCycle() - unit.getResultLatency() < 0) {
				if (unit.isOutput == true) {
					unit.calcDiscardCycle(unit.getResultLatency());
				}
			}
			if (data.outputLastCycleMode) {
				if (unit.isOutput == true) {
					unit.calcDiscardCycle(maxLatency + (data.II - maxLatency % data.II - 1));
				}
			}
		}
	}

	public void locateUnits(int II, ArrayList<FlowOperation> flowOperations) {
		int unitUntil = -1;
		int aa = 0;
		for (FlowOperation unit : flowOperations) {
			if (aa == unitUntil)
				break;
			aa++;
			int opNum = unit.getOpNum(); // operations.get(unit.getType());
			for (int i = 0; i < unit.getInputs().length; i++) {
				unit.calcStartLatency(unit.getInputs()[i].getResultLatency());
			}

			if (unit.HaveAlter()) {
				for (int count = 0; count < unit.getAus().length; count++) {
					for (int i = 0; i < unit.getInputs().length; i++) {
						unit.calcStartLatency(
								unit.getAus()[count].getInputs()[i].getResultLatency());
					}
				}
			}
			if (unit.HaveAlter())
				for (int count2 = 0; count2 < unit.cons.length; count2++)
					for (int count = 0; count < unit.cons[count2].ais.length; count++) {
						unit.calcStartLatency(unit.cons[count2].ais[count].getResultLatency());
					}
			int delay = 0;
			int i = 0;
			while (true) {
				if (slotAvailability[opNum][i][(unit.getStartLatency()) % II] == true) {
					slotAvailability[opNum][i][(unit.getStartLatency()) % II] = false;
					unit.setModuleNum(i); // ??
					timingArray[opNum][i][(unit.getStartLatency()) % II] = unit;
					unit.setResultLatency(unit.getStartLatency() + unit.module.getOpLatency());
					unit.setResourceDelay(delay);
					for (int j = 0; j < unit.getInputs().length; j++) {
						unit.getInputs()[j].calcDiscardCycle(unit.getStartLatency());
					}
					if (unit.HaveAlter()) {
						for (int count = 0; count < unit.aus.length; count++) {
							for (int j = 0; j < unit.getInputs().length; j++) {
								unit.getAus()[count].getInputs()[j]
										.calcDiscardCycle(unit.getStartLatency());
							}
						}
					}
					if (unit.HaveAlter())
						for (int count2 = 0; count2 < unit.cons.length; count2++)
							for (int count = 0; count < unit.cons[count2].ais.length; count++) {
								unit.cons[count2].ais[count]
										.calcDiscardCycle(unit.getStartLatency());
							}

					break;
				}
				i++;
				if (i == slotAvailability[opNum].length) {
					i = 0;
					delay++;
					unit.calcStartLatency(unit.getStartLatency() + 1);
					if (delay == II) {
						System.err.println("Failure, lack of resource");
						System.exit(0);
					}
				}
			}
		}
	}

	public int[] calcFPUReq(int II, ArrayList<FlowOperation> myAUnits) {
		int numOfOper = 0;
		for (FlowOperation unit : myAUnits) {
			if (unit.getOpNum() > numOfOper) {
				numOfOper = unit.getOpNum();
			}
		}
		int[] moduleCounts = new int[numOfOper + 1];
		for (FlowOperation unit : myAUnits) {
			moduleCounts[unit.getOpNum()]++;
		}
		for (int i = 0; i < moduleCounts.length; i++)
			moduleCounts[i] = (moduleCounts[i] + II - 1) / II;
		return moduleCounts;
	}

	public void initializeScheduleArrays(int II) {
		timingArray = new FlowOperation[moduleCounts.length][][];
		slotAvailability = new boolean[moduleCounts.length][][];
		for (int k = 0; k < moduleCounts.length; k++) {
			timingArray[k] = new FlowOperation[moduleCounts[k]][II];
			slotAvailability[k] = new boolean[moduleCounts[k]][II];
			for (int i = 0; i < slotAvailability[k].length; i++)
				for (int j = 0; j < slotAvailability[k][i].length; j++)
					slotAvailability[k][i][j] = true;
		}
	}

	public void printLatency(int sP, int eP, ArrayList<FlowOperation> myAUnits) {
		// TODO remove before release
		for (int test = sP; test < eP; test++) {
			System.out.println(myAUnits.get(test).getEQ());
			System.out.println(myAUnits.get(test).getDiscardCycle());
		}
	}

}
