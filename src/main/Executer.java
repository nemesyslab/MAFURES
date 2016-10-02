package main;

import executers.ModuloScheduler;
import executers.RegisterAssigner;
import executers.VerilogWriter;
import inputReaders.FlowReader;
import inputReaders.GenericUnitGenerator;
//import inputReaders.Operator2Function;

public class Executer {

	public void execute(String s) {
		String projectName = s;
//		new Operator2Function("data/flowOperations.c","data/"+projectName+".mc");
		DataSet data = new DataSet(new GenericUnitGenerator("inputs/"+projectName+".ml").getModulesHash());
		new FlowReader("inputs/"+projectName+".maf",data);
		new ModuloScheduler(data);
		new RegisterAssigner(data);
		new Reporter(data,"outputs/"+projectName+".log");
		new VerilogWriter(data,"outputs/"+projectName+".v");
		
	}

	Executer(String s) {
		execute(s);
	}

	public static void main(String[] args) {
		new Executer(args[0]);
	}

}
