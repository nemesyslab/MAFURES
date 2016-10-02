initiation interval : 10
m = 588;
n = 384;
carpan1 = 32'h3f000000;
carpan2 = 32'h3f000000;
cons0 = 32'h3e4ccccd;
cons5 = 32'h3f000000;
TAU = 32'h3ecccccd;
BETA = 32'h3dcccccd;
ZERO = 0;
ONE = 32'h3F800000;
TWO = 32'h40000000;
MINUSONE = 32'hBF800000;

s000 = u_S1 - u_S0;
s100 = v_S1 - v_S0;
s200 = w_S1 - w_S0;
m010 = carpan1 * s000;
m110 = carpan1 * s100;
m210 = carpan2 * s200;

a062 = p0 + m010;
a161 = p2 + m110;
a261 = p4 + m210;
if (jj == n ) {
	a062 = ZERO + ZERO;
	a161 = ZERO + ZERO;
	a261 = ZERO + ZERO;
}

s300 = u_S2 - u_S0;
s400 = v_S2 - v_S0;
s011 = w_S2 - w_S0;
m310 = carpan1 * s300;
m410 = carpan1 * s400;
m021 = carpan2 * s011;

a361 = p1 + m310;
a461 = p3 + m410;
a073 = p5 + m021;
if (ii == m ) {
	a361 = ZERO + ZERO;
	a461 = ZERO + ZERO;
	a073 = ZERO + ZERO;
}

m072 = a062 * a062;
m171 = a161 * a161;
m271 = a261 * a261;
m371 = a361 * a361;
m471 = a461 * a461;
m083 = a073 * a073;
a024 = m171 + m072;
a122 = m471 + m371;
a133 = a024 + a122;


a035 = m271 + m083;
bool0 = a035 < ONE;

ct0 = sqrt(a035);
if (bool0){
	ct0 = sqrt(ONE);
}

bool1 = a133 < ONE;
ct1 = sqrt(a133);
if (bool1){
	ct1 = sqrt(ONE);
}
	

d030 = a062 / ct1;   
d041 = a161 / ct1;
d052 = a261 / ct0;
d063 = a361 / ct1;
d074 = a461 / ct1;
d085 = a073 / ct0;

p0Out = d030;
p2Out = d041;
p4Out = d052;
p1Out = d063;
p3Out = d074;
p5Out = d085;


//u_S0 = uS;
//v_S0 = vS;
//w_S0 = wS;

s171 = p0Prev - d030;
s096 = p2Prev - d041;
s115 = p4Prev - d052;
//if(valid == 1) {
//	p0Prev = d030;
//	p2Prev = d041;
//	p4Prev = d052;
//}

s087 = p1S_1 - d063;
s212 = p3S_1 - d074;
s223 = p5S_1 - d085;
if (ii == 0 && jj != 0) {
	s087 = ZERO - d063;
	s212 = ZERO - d074;
	s223 = ZERO - d085;
}
if (ii != 0 && jj == 0) {
	s171 = ZERO - d030;
	s096 = ZERO - d041;
	s115 = ZERO - d052;
}

a196 = s171 + s087;
a322 = s096 + s212;
a234 = s115 + s223;
m004 = TAU * a196;
m035 = TAU * a322;
m046 = TAU * a234;


s058 = uS - m004;
s187 = vS - m035;
s295 = wS - m046;
if (ii == 0 && jj == 0) {
	s058 = uS - ZERO;
	s187 = vS - ZERO;
	s295 = wS - ZERO;
}

s276 = s058 - u0S;
s393 = s187 - v0S;
m202 = BETA * s295;
m182 = s276 * I_xS;
m103 = s393 * I_yS;
a158 = m103 + m182;
a257 = I_tS + m202;
a374 = a158 + a257;

cons1 = cons0 * I_grad_sqrS;
cons2 = MINUSONE * cons1;

c080 = a374 < cons2;
c091 = a374 > cons1;

cons6 = I_xS / I_grad_sqrS;
cons7 = I_yS / I_grad_sqrS;
cons8 = beta / I_grad_sqrS;
cons3 = cons0 * I_xS;
cons4 = cons0 * I_yS;

m283 = a374 * cons6;
m382 = a374 * cons7;
m482 = a374 * cons8;

as335 = s058 - m283;
as432 = s187 - m382;
as049 = s295 - m482;
if (c080) {
	as335 = s058 + cons3;
	as432 = s187 + cons4;
	as049 = s295 + cons5;
} 
if (c091) {
	as335 = s058 - cons3;
	as432 = s187 - cons4;
	as049 = s295 - cons5;
}

m144 = as335 * TWO;
m244 = as432 * TWO;
m057 = as049 * TWO;
s316 = m144 - uS;
s413 = m244 - vS;
s424 = m057 - wS;

u_Out = s316;
v_Out = s413;
w_Out = s424;

uOut = as335;
vOut = as432;
wOut = as049;