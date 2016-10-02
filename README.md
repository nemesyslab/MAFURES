# mafures_almarvi
This IP developed for the ALMARVI project

Java build path : JRE System Library  [JavaSE-1.8]

Import source files into your Java project. Codes read input data from data folder and write out files into outputs folder.
main method is in aeg/main/executer.java .

Sample inputs and outputs are added.


FPU module and other pipelined custom modules that will be used in flow should be introduced to tool. This introduction should include name of operation, name of different modes, its latency, module name, inputs/output port names and parameter settings in the module declarations file.
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

a374 = mult( a374 , cons0 );
if(ii == m) {
  a374 = mult( a374 , cons1 );
}

c080 = lt( a374 , cons0 );

as335 = sub( s058 , m283 );
if (c080) {
	as335 = add( s058 , cons3 );
}

u_Out = a374;
v_Out = as335;
///////////////////////////////////////////////////////////////////////////

Above code sample represents operation flow feeding way of our tool. Introduced modules can be used with their function name. 'a', 'b' and 'e' keywords interpreted as an input port of target module. Numbers like "32'h3E4CCCCD" be saved as parameter and will be described as local parameter in RTL code. Branches can be written as if statements. Variable names that include “out” interpreted as output automatically and wired outputs of Verilog module. There are two modes about discard times of outputs. First mode gives you data ready signal for each signal and don’t register them. In second output mode, outputs saved in registers until last output produced and sets only one data ready signal for all outputs.

