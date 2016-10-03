package dataTypes;

public class FlowInput extends FlowElement {

	public FlowInput(String name) {
		this.name = name;
	}

	@Override
	String getTrack(int cycle) {
		if (getResultLatency() == cycle)
			return getName();
		return "reg" + regLocNumZeroPadded(cycle);
	}

	public String getEQ() {
		return name;
	}

}
