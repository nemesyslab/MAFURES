# mafures_almarvi
This IP developed for the ALMARVI project

Java build path : JRE System Library  [JavaSE-1.8]

Import source files into your Java project. Codes read input data from data folder and write out files into outputs folder.
main method is in aeg/main/executer.java .

Sample inputs and outputs are added.


FPU module and other pipelined custom modules that will be used in flow should be introduced to tool. This introduction should include name of operation, name of different modes, its latency, module name, inputs/output port names and parameter settings in the module declarations file.


Above code sample represents operation flow feeding way of our tool. Introduced modules can be used with their function name. 'a', 'b' and 'e' keywords interpreted as an input port of target module. Numbers like "32'h3E4CCCCD" be saved as parameter and will be described as local parameter in RTL code. Branches can be written as if statements. Variable names that include “out” interpreted as output automatically and wired outputs of Verilog module. There are two modes about discard times of outputs. First mode gives you data ready signal for each signal and don’t register them. In second output mode, outputs saved in registers until last output produced and sets only one data ready signal for all outputs.

