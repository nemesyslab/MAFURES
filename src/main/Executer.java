package aeg.main;

import aeg.executers.ModuloScheduler;
import aeg.executers.RegisterAssigner;
import aeg.executers.VerilogWriter;
import aeg.inputReaders.FlowReader;
import aeg.inputReaders.GenericUnitGenerator;
import aeg.inputReaders.Operator2Function;

public class Executer {

	public void execute() {
//		new Operator2Function("data/flowOperations.c","data/flowInput.txt");
		DataSet data = new DataSet(new GenericUnitGenerator("data/moduleDeclerations.txt").getModulesHash());
		new FlowReader("data/flowInput.txt",data);
		new ModuloScheduler(data);
		new RegisterAssigner(data);
		new Reporter(data,"outputs/report.txt");
		new VerilogWriter(data,"outputs/deneme.v");
		
	}

	Executer() {
		execute();
	}

	public static void main(String[] args) {
		new Executer();
	}

}
