# mafures_almarvi
This IP developed for the ALMARVI project

Java build path : JRE System Library  [JavaSE-1.8]

Import source files into your Java project. Codes read input data from data folder and write out files into outputs folder.
main method is in aeg/main/executer.java .

Sample inputs and outputs are added.


FPU modules and other pipelined custom modules that will be used in flow should be introduced to tool. This introduction should include name of operation, name of different modes, its latency, module name, inputs/output port names and parameter settings in the module declarations file.

Here is the sample decleration;

//////////////////////////////////////////////////////

Function Name = add:0,sub:1;                           // function names used in the data flow with operation number* 
Latency = 16;                                          // latency of used module
Module Name = iter_v2_faddfsub_32ns_32ns_32_14_no_dsp; // module name of fuction 
Passed Parameters =                                    // passed parameters can be declared if they exist
    .ID( 1 ),
    .NUM_STAGE( 14 ),
    .din0_WIDTH( 32 ),
    .din1_WIDTH( 32 ),
    .dout_WIDTH( 32 );
Port Names =                                           // port names declared here
    .clk( clk ),                                       // inside of the paranthesis is pointed what is it
    .reset( rst ),                                     // keywords are "clk", "rst", "input:", "opcode:", and "output"
    .din0( input : 0 ),                                // If there is more than one input, order of inputs should
    .din1( input : 1 ),                                // be specified as ": 0", ": 1" indide the paranthesis
    .opcode( opcode: ),
    .ce( 1'b1 ),                              
    .dout( output: );
//////////////////////////////////////////////////////

*some modules can be used as different functions (such as adder and subtructer) with different operation code, this situation supported by our tool.
**at this version all ports declared 32 bit by default except clock and reset signals


Here is the data flow sample;
////////////////////////////////////////////////////////////////////////
initiation interval : 10
m = 588;
cons0 = 32'h3e4ccccd;
cons1 = 32'h3f000000;

a374 = mult( inputS , cons0 );
if(ii == m) {
	a374 = mult( C_input , cons1 );
}

c080 = lt( inputK , cons0 );

as335 = sub( inputW , m283 );
if (c080) {
	as335 = add( asd , cons1 );
}
if (c080 || jj == m) {
	as335 = sub( asd , cons3 );
}


u_Out = a374;
v_Out = as335;
///////////////////////////////////////////////////////////////////////////
Above code sample represents an example of operation flow feeding way of our tool. Initiation interval should be declared at the first line. Introduced modules (declared in the module decleration file) can be used with their function names. Undefined names, such as 'inputS', 'C_input' or 'asd' interpreted as an input port of target module. Numbers like "32'h3E4CCCCD" be saved as parameter and will be described as local parameter in RTL code. Branches can be written as if statements. Variable names that include “_Out” (like "u_Out" or "v_Out") interpreted as output automatically and wired outputs of Verilog module.


Output Behaviours
Functional Module
When you run the code with valid inputs, program generate two output files whice are functional Verilog module and a report file about implementation. The module is ready to instantiate into your design. Connecting inputs and output ports is enough.All inputs should be fed at cycle zero (you can start to count after reset). Outputs will be ready after calculated latency whice can be found into the report file and you must collect them at that cycle.
Report File
Report file includes;
	- utilization of functional units
	- folded schedule table of functional units
	- folded schedule table of registers

Contact email (any question and bug report): aydin.guzel@ozu.edu.tr



