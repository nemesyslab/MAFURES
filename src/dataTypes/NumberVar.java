package dataTypes;

public class NumberVar extends FlowElement {

	public String num;

	public NumberVar(String name, String num) {
		this.name = name;
		this.num = num;
	}

	@Override
	String getTrack(int cycle) {
		return name;
	}

	@Override
	String getEQ() {
		return name;
	}
}
