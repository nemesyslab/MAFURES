Function Name = add:0,sub:1;
Latency = 16;
Module Name = iter_v2_faddfsub_32ns_32ns_32_14_no_dsp;
Passed Parameters =
    .ID( 1 ),
    .NUM_STAGE( 14 ),
    .din0_WIDTH( 32 ),
    .din1_WIDTH( 32 ),
    .dout_WIDTH( 32 );
Port Names =
    .clk( clk ),
    .reset( rst ),
    .din0( input : 0 ),
    .din1( input : 1 ),
    .opcode( opcode: ),
    .ce( 1'b1 ),
    .dout( output: );
	
Function Name = mult;
Latency = 12;
Module Name = iter_v2_fmul_32ns_32ns_32_10_full_dsp;
Passed Parameters =
    .ID( 1 ),
    .NUM_STAGE( 10 ),
    .din0_WIDTH( 32 ),
    .din1_WIDTH( 32 ),
    .dout_WIDTH( 32 );
Port Names =
    .clk( clk ),
    .reset( rst ),
    .din0( input : 0 ),
    .din1( input : 1 ),
    .ce( 1'b1 ),
    .dout( output: );
	
Function Name = div;
Latency = 32;
Module Name = iter_v2_fdiv_32ns_32ns_32_30;
Passed Parameters =
    .ID( 1 ),
    .NUM_STAGE( 30 ),
    .din0_WIDTH( 32 ),
    .din1_WIDTH( 32 ),
    .dout_WIDTH( 32 );
Port Names =
    .clk( clk ),
    .reset( rst ),
    .din0( input : 0 ),
    .din1( input : 1 ),
    .ce( 1'b1 ),
    .dout( output: );

Function Name = sqrt;
Latency = 32;
Module Name = iter_v2_fsqrt_32ns_32ns_32_30;
Passed Parameters =
	.ID( 1 ),
    .NUM_STAGE( 30 ),
    .din0_WIDTH( 32 ),
    .din1_WIDTH( 32 ),
    .dout_WIDTH( 32 );
Port Names =
    .clk( clk ),
    .reset( rst ),
    .din0( 0 ),
    .din1( input : 0 ),
    .ce( 1'b1 ),
    .dout( output: );
	
Function Name = eq:1,gt:2,ge:3,lt:4,le:5,ne:6;
Latency = 6;
Module Name = iter_v2_fcmp_32ns_32ns_1_4;
Passed Parameters =
    .ID( 1 ),
    .NUM_STAGE( 4 ),
    .din0_WIDTH( 32 ),
    .din1_WIDTH( 32 ),
    .dout_WIDTH( 1 );
Port Names =
    .clk( clk ),
    .reset( rst ),
    .din0( input : 0 ),
    .din1( input : 1 ),
    .ce( 1'b1 ),
    .opcode( opcode: ),
    .dout( output: );

