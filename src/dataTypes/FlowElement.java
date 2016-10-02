package dataTypes;

public abstract class FlowElement {

	int resultLatency;

	public int getResultLatency() {
		return resultLatency;
	}

	int discardCycle;

	public void calcDiscardCycle(int discardLatency) {
		this.discardCycle = Math.max(discardLatency, this.discardCycle);
	}

	abstract String getTrack(int cycle);

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

	public int getDiscardCycle() {
		return discardCycle;
	}

	public int[] registerLocArray;

	public void initializeRegLocArr() {
		if (discardCycle - resultLatency > 0)
			registerLocArray = new int[discardCycle - resultLatency];
		else {
			registerLocArray = new int[1];
			registerLocArray[0] = -1;
		}
	}

	int nextRegArrOrder = 0;

	public String regLocNumZeroPadded(int n) {
		// TODO ?? -1
		return padZero(registerLocArray[n - getResultLatency() - 1]);
	}

	public void updateRegLocArr(int registerIndex) {
		updateRegLocArr(registerIndex, nextRegArrOrder);
		nextRegArrOrder++;
	}

	public void updateRegLocArr(int registerIndex, int regArrOrder) {
		registerLocArray[nextRegArrOrder] = registerIndex;
	}

	public String name;

	public String getName() {
		return name;
	}

	abstract String getEQ();
}
