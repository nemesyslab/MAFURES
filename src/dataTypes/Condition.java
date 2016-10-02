package dataTypes;

public class Condition {
	public FlowElement[] ais;
	String ops[];
	String opList[] = {"&&","||","<","<=",">",">=","==","!="};
	public Condition (FlowElement[] ais,String[] ops) {
		this.ais = ais;
		this.ops = ops;
	}
	String conditionProducer (int startLatency) {
		String con = ais[0].getTrack(startLatency);
		for(int i = 0;i<ais.length-1;i++){
			con = con + " " + ops[i] + " " + ais[i+1].getTrack(startLatency);;
		}
		return con;
	}
}
