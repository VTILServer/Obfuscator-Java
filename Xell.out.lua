-- ROT13 ciphering algorithm implementation
-- See: http://en.wikipedia.org/wiki/ROT13

-- Returns the ASCII bytecode of either 'a' or 'A'
local function ascii_base(s)
return s:lower() == s and ('a'):byte() or ('A'):byte()
end

-- ROT13 is based on Caesar ciphering algorithm, using 13 as a key
local function caesar_cipher(str, key)
return (str:gsub('%a', function(s)
local base = ascii_base(s)
return string.char(((s:byte() - base + key) % 26) + base)
end))
end

-- str     : a string to be ciphered
-- returns : the ciphered string
local function rot13_cipher(str)
return caesar_cipher(str, 13)
end

-- str     : a string to be deciphered
-- returns : the deciphered string
local function rot13_decipher(str)
return caesar_cipher(str, -13)
end


local XellStringsCache = {}
local BitWise = {}

local instrDebug = -1;


local aaa = string.sub
local aaabbb = string.byte
local ulololo = string.find
local epic1 = setmetatable
local s = "luraph bad"
local BitShiftLeft = function(integer, count)
return integer * (2 ^ count);
end

local ShiftRight = function  (integer, count)
return math.floor(integer / (2 ^ count))
end

local GetBits = function  (integer, index, count)
local bits = ShiftRight(integer, index)
return bits % (2 ^ count)
end

local GetBitCount= function (integer)
local count = 1
while integer > 1 do
integer = ShiftRight(integer, 1)
count = count + 1
end
return count
end

local abcdefg = {[1]=828;[2]=164;[3]=618;[4]=303;[5]=79;[6]=675;[7]=443;[8]=436;[9]=156;[10]=132;[11]=326;[12]=503;[13]=732;[14]=260;[15]=855;[16]=758;[17]=1044;[18]=396;[19]=535;[20]=1150;[21]=542;[22]=507;[23]=1109;[24]=1221;[25]=862;[26]=357;[27]=315;[28]=809;[29]=1018;[30]=904;[31]=102;[32]=195;[33]=1128;[34]=526;[35]=827;[36]=419;[37]=466;[38]=972;[39]=156;[40]=1;[41]=163;[42]=942;[43]=530;[44]=32;[45]=805;[46]=578;[47]=417;[48]=799;[49]=1182;[50]=1074;[51]=996;[52]=9;[53]=1097;[54]=935;[55]=1049;[56]=745;[57]=702;[58]=830;[59]=256;[60]=301;[61]=934;[62]=1094;[63]=433;[64]=143;[65]=168;[66]=286;[67]=350;[68]=244;[69]=538;[70]=501;[71]=189;[72]=674;[73]=1136;[74]=708;[75]=543;[76]=454;[77]=909;[78]=226;[79]=731;[80]=418;[81]=996;[82]=945;[83]=783;[84]=649;[85]=381;[86]=1073;[87]=397;[88]=260;[89]=953;[90]=87;[91]=842;[92]=853;[93]=303;[94]=819;[95]=790;[96]=98;[97]=571;[98]=738;[99]=359;[100]=784;[101]=824;[102]=800;[103]=116;[104]=227;[105]=224;[106]=413;[107]=1160;[108]=215;[109]=1047;[110]=259;[111]=217;[112]=467;[113]=902;[114]=741;[115]=1126;[116]=285;[117]=847;[118]=304;[119]=1163;[120]=1037;[121]=253;[122]=1104;[123]=1097;[124]=1056;[125]=333;[126]=465;[127]=736;[128]=512;[129]=1117;[130]=694;[131]=170;[132]=1047;[133]=975;[134]=1018;[135]=408;[136]=196;[137]=848;[138]=805;[139]=942;[140]=124;[141]=211;[142]=58;[143]=298;[144]=271;[145]=514;[146]=636;[147]=526;[148]=256;[149]=693;[150]=68;[151]=931;[152]=603;[153]=351;[154]=447;[155]=981;[156]=649;[157]=178;[158]=209;[159]=162;[160]=175;[161]=394;[162]=1192;[163]=221;[164]=65;[165]=73;[166]=75;[167]=473;[168]=982;[169]=2;[170]=677;[171]=585;[172]=174;[173]=206;[174]=560;[175]=899;[176]=1139;[177]=1215;[178]=430;[179]=30;[180]=487;[181]=297;[182]=1033;[183]=1000;[184]=961;[185]=543;[186]=488;[187]=65;[188]=263;[189]=1066;[190]=850;[191]=89;[192]=963;[193]=501;[194]=390;[195]=337;[196]=177;[197]=76;[198]=1001;[199]=2;[200]=746;[201]=369;[202]=799;[203]=727;[204]=716;[205]=1162;[206]=784;[207]=498;[208]=957;[209]=1098;[210]=367;[211]=302;[212]=308;}
local XOR = 1
XOR = function (integerA, integerB)
local mb = math.max(GetBitCount(integerA), GetBitCount(integerB))
local arr = {}
for n = 0, mb-1 do
arr[mb - n] = (GetBits(integerA, n, 1) ~= GetBits(integerB, n, 1)) and 1 or 0
end
return tonumber(table.concat(arr, ""), 2)
end
if bit and bit.bxor then
XOR = bit.bxor
end
local epic2 = epic1
local dshdsuysdjds = XOR
local dshjuydisjkdjdjksdjskjdjs = XOR
local dsjdsksjdjjkdsjshusi = dshdsuysdjds
local dsjdsksjdjjkdsjshusi = nil
local kdslksdsoipso = {dsjdsksjdjjkdsjshusi,dshdsuysdjds}
local dshdsyudshjdss = {kdslksdsoipso,dsjdsksjdjjkdsjshusi,{dshjuydisjkdjdjksdjskjdjs}}
local dskdsdsjderd = XOR
local ckjdsdsui = dshdsuysdjds

local Select	= select;
local Byte		= string.byte;
local Sub		= string.sub;
local dsuydsdslkdsldkl = string.char

local jsddshsuidsjkds = table.concat
local function gBit(Bit, Start, End) -- No tail-calls, yay.
if End then -- Thanks to cntkillme for giving input on this shorter, better approach.
local Res	= (Bit / 2 ^ (Start - 1)) % 2 ^ ((End - 1) - (Start - 1) + 1);

return Res - Res % 1;
else
local Plc = 2 ^ (Start - 1);

if (Bit % (Plc + Plc) >= Plc) then
return 1;
else
return 0;
end;
end;
end;

local dddddddd = function(char,xorval)
return dsuydsdslkdsldkl(kdslksdsoipso[2](abcdefg[xorval],char))
end

local function gsplit(text, pattern, plain)
plain = (jsddshsuidsjkds({dddddddd(965,54);dddddddd(1148,55);dddddddd(669,56);dddddddd(714,57);dddddddd(859,58);dddddddd(370,59);dddddddd(269,60);dddddddd(978,61);dddddddd(1070,62);dddddddd(464,63);dddddddd(225,64);dddddddd(136,65);dddddddd(334,66);dddddddd(269,67);dddddddd(161,68);}) == jsddshsuidsjkds({dddddddd(965,54);dddddddd(1148,55);dddddddd(669,56);dddddddd(714,57);dddddddd(859,58);dddddddd(370,59);dddddddd(269,60);dddddddd(978,61);dddddddd(1070,62);dddddddd(464,63);dddddddd(225,64);dddddddd(136,65);dddddddd(334,66);dddddddd(269,67);dddddddd(161,68);}))
local splitStart, length = 1, #text
return function ()
if splitStart then
local sepStart, sepEnd = ulololo(text, pattern, splitStart, plain)
local ret
if not sepStart then
ret = string.sub(text, splitStart)
splitStart = nil
elseif sepEnd < sepStart then
-- Empty separator!
ret = string.sub(text, splitStart, sepStart)
if sepStart < length then
splitStart = sepStart + 1
else
splitStart = nil
end
else
ret = sepStart > splitStart and string.sub(text, splitStart, sepStart - 1) or ''
splitStart = sepEnd + 1
end
return ret
end
end
end
local epic3 = epic2
local function split(text, pattern, plain)
plain = (jsddshsuidsjkds({dddddddd(439,70);dddddddd(220,71);dddddddd(710,72);dddddddd(1104,73);dddddddd(692,74);dddddddd(620,75);dddddddd(435,76);}) == jsddshsuidsjkds({dddddddd(439,70);dddddddd(220,71);dddddddd(710,72);dddddddd(1104,73);dddddddd(692,74);dddddddd(620,75);dddddddd(435,76);}))
local ret = {}
for match in gsplit(text, pattern, plain) do
table.insert(ret, match)
end
return ret
end
local s1 = "bad = luraph"
local s2 = "luraph = bad"
local odsoldssd = "stop looking at this secret codes plz"
local sddhjsddhhjsdjh = epic3({}, {
__index = function(a, b)
return "wowowow roblox!!";
end;

__newindex = function(a,b,c)
dddd[b] = c
end
})

local function GetMeaning(ByteString)
ByteString = ByteString:gsub("..", function(x)
if(x:sub(1,1):byte() == 69 and x:sub(2,2):byte()==83)then
--print(x)
return string.char(0)
elseif(x:sub(2):byte()==33)then
--print(x)
return string.char(tonumber(x:sub(1,1),16))
elseif(x:sub(1,1):byte()==71)then
--print(x)
return string.char(0)
else
--print(x)
return string.char(tonumber(x,16))
end
end)
local Pos	= 1;
local gSizet;
local gInt;

local function gBits8() -- Get the next byte in the stream.
local F	= Byte(ByteString, Pos, Pos);

Pos	= Pos + 1;

return F;
end;

local function gBits32()
local W, X, Y, Z	= Byte(ByteString, Pos, Pos + 3);

Pos	= Pos + 4;

return (Z * 16777216) + (Y * 65536) + (X * 256) + W;
end;

local function gBits64()
return gBits32() * 4294967296 + gBits32();
end;

local function gFloat()
-- thanks @Eternal for giving me this so I could mangle it in here and have it work
local Left = gBits32();
local Right = gBits32();
local IsNormal = 1
local Mantissa = (gBit(Right, 1, 20) * (2 ^ 32))
+ Left;

local Exponent = gBit(Right, 21, 31);
local Sign = ((-1) ^ gBit(Right, 32));

if (Exponent == 0) then
if (Mantissa == 0) then
return Sign * 0 -- +-0
else
Exponent = 1
IsNormal = 0
end
elseif (Exponent == 2047) then
if (Mantissa == 0) then
return Sign * (1 / 0) -- +-Inf
else
return Sign * (0 / 0) -- +-Q/Nan
end
end

-- sign * 2**e-1023 * isNormal.mantissa
return math.ldexp(Sign, Exponent - 1023) * (IsNormal + (Mantissa / (2 ^ 52)))
end;

local function gString(Len, deob)
local Str;

if Len then
Str	= Sub(ByteString, Pos, Pos + Len - 1);

Pos = Pos + Len;
else
Len = gSizet();

if (Len == 0) then return; end;

Str	= Sub(ByteString, Pos, Pos + Len - 1);

Pos = Pos + Len;
end;
if deob then

return rot13_decipher(Str);
else
return Str;
end
end;

local Opcode = {[25] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[8] = jsddshsuidsjkds({dddddddd(843,1);dddddddd(203,2);dddddddd(541,3);dddddddd(271,4);dddddddd(58,5);dddddddd(721,6);dddddddd(411,7);dddddddd(470,8);dddddddd(253,9);dddddddd(224,10);}),[7] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[2] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[15] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[33] = jsddshsuidsjkds({dddddddd(843,1);dddddddd(203,2);dddddddd(541,3);dddddddd(271,4);dddddddd(58,5);dddddddd(721,6);dddddddd(411,7);dddddddd(470,8);dddddddd(253,9);dddddddd(224,10);}),[10] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[35] = jsddshsuidsjkds({dddddddd(843,1);dddddddd(203,2);dddddddd(541,3);dddddddd(271,4);dddddddd(58,5);dddddddd(721,6);dddddddd(411,7);dddddddd(470,8);dddddddd(253,9);dddddddd(224,10);}),[22] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[30] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[29] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[19] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[11] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[20] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[37] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[3] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[4] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[28] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[23] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[14] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[24] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[36] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[32] = jsddshsuidsjkds({dddddddd(286,11);dddddddd(402,12);dddddddd(688,13);dddddddd(360,14);dddddddd(887,15);dddddddd(712,16);dddddddd(1076,17);dddddddd(493,18);dddddddd(635,19);dddddddd(1042,20);}),[1] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[34] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[6] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[27] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[12] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[9] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[13] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[17] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[0] = jsddshsuidsjkds({dddddddd(286,11);dddddddd(402,12);dddddddd(688,13);dddddddd(360,14);dddddddd(887,15);dddddddd(712,16);dddddddd(1076,17);dddddddd(493,18);dddddddd(635,19);dddddddd(1042,20);}),[26] = jsddshsuidsjkds({dddddddd(286,11);dddddddd(402,12);dddddddd(688,13);dddddddd(360,14);dddddddd(887,15);dddddddd(712,16);dddddddd(1076,17);dddddddd(493,18);dddddddd(635,19);dddddddd(1042,20);}),[31] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[16] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[18] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),[5] = jsddshsuidsjkds({dddddddd(843,1);dddddddd(203,2);dddddddd(541,3);dddddddd(271,4);dddddddd(58,5);dddddddd(721,6);dddddddd(411,7);dddddddd(470,8);dddddddd(253,9);dddddddd(224,10);}),[21] = jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);}),}

local function ChunkDecode()
local Instr	= {};
local Const	= {};
local Proto	= {};
local Chunk	= {
Instr	= Instr; -- Instructions
Const	= Const; -- Constants
Proto	= Proto; -- Prototypes
Lines	= {}; -- Lines
LastL = gInt();
Upvals = gBits8();
FirstL = gInt();
Vargs = gBits8();
Stack    = gBits8();
Args = gBits8();
Name = gString();

};

if Chunk.Name then
Chunk.Name	= Sub(Chunk.Name, 1, -2);
end;

for Idx = 1, gInt() do -- Loading instructions to the chunk.
local Data	= gBits32();
local Opco	= gBit(Data, 1, 6);
local Type	= Opcode[Opco];
local Inst	= {
Value	= Data;
li1iIilIlI1IilIil1lI	= Opco;
gBit(Data, 7, 14); -- Register A.
};

if (Type == jsddshsuidsjkds({dddddddd(639,21);dddddddd(393,22);dddddddd(1072,23);dddddddd(1253,24);dddddddd(811,25);dddddddd(325,26);dddddddd(349,27);dddddddd(838,28);dddddddd(913,29);dddddddd(1005,30);dddddddd(8,31);dddddddd(227,32);dddddddd(1034,33);dddddddd(610,34);dddddddd(850,35);dddddddd(461,36);dddddddd(438,37);dddddddd(1004,38);dddddddd(233,39);dddddddd(33,40);dddddddd(192,41);dddddddd(975,42);dddddddd(636,43);dddddddd(84,44);dddddddd(773,45);dddddddd(561,46);dddddddd(452,47);dddddddd(890,48);dddddddd(1214,49);dddddddd(1119,50);dddddddd(897,51);dddddddd(54,52);dddddddd(1128,53);})) then -- Most common, basic instruction type.
				Inst[2]	= gBit(Data, 24, 32);
				Inst[3]	= gBit(Data, 15, 23);
			elseif (Type == jsddshsuidsjkds({dddddddd(843,1);dddddddd(203,2);dddddddd(541,3);dddddddd(271,4);dddddddd(58,5);dddddddd(721,6);dddddddd(411,7);dddddddd(470,8);dddddddd(253,9);dddddddd(224,10);})) then
				Inst[2]	= gBit(Data, 15, 32);
			elseif (Type == jsddshsuidsjkds({dddddddd(286,11);dddddddd(402,12);dddddddd(688,13);dddddddd(360,14);dddddddd(887,15);dddddddd(712,16);dddddddd(1076,17);dddddddd(493,18);dddddddd(635,19);dddddddd(1042,20);})) then
				Inst[2]	= gBit(Data, 15, 32) - 131071;
			end;

Instr[Idx]	= Inst;
end;

local hasExtraShit = gFloat()
if (hasExtraShit == 1) then
for Idx = 1, 5 do
gBits8();
local debug = Sub(gString(nil, false), 1, -2);
end
end
for Idx = 1, gInt()-3 do -- Load constants.

local Type	= gBits8();
local Cons;

if (Type == 1) then -- Boolean
Cons	= (gBits8() == 0);
elseif (Type == 3) then -- Float/Double
Cons	= gFloat();
elseif (Type == 4) then
local strXd = Sub(gString(nil, false), 1, -2);
local parts = split(strXd, '|XELL|')
Cons	= rot13_decipher(parts[1] .. parts[2]);
end;
Const[Idx - 1]	= Cons;
end;


for Idx = 1, gInt() do -- Nested function prototypes.
Proto[Idx - 1]	= ChunkDecode();
end;

do -- Debugging
local Lines	= Chunk.Lines;

for Idx = 1, gInt() do
Lines[Idx]	= gBits32();
end;

for _ = 1, gInt() do -- Locals in stack.
gString(); -- Name of local.
gBits32(); -- Starting point.
gBits32(); -- End point.
end;

for _ = 1, gInt() do -- Upvalues.
gString(); -- Name of upvalue.
end;
end;

return Chunk; -- Finished chunk.
end;

do -- Most of this chunk I was too lazy to reformat or change
assert(gString(4) == "\27XEL", jsddshsuidsjkds({dddddddd(98,142);dddddddd(335,143);dddddddd(355,144);dddddddd(622,145);dddddddd(604,146);dddddddd(620,147);dddddddd(377,148);dddddddd(705,149);dddddddd(33,150);dddddddd(960,151);dddddddd(564,152);dddddddd(315,153);dddddddd(474,154);dddddddd(1013,155);dddddddd(748,156);dddddddd(202,157);dddddddd(161,158);dddddddd(199,159);dddddddd(204,160);dddddddd(510,161);dddddddd(1229,162);dddddddd(185,163);dddddddd(111,164);}));

gBits8();--lua version

gBits8(); -- Probably version control.
gBits8(); -- Is small endians.

local IntSize	= gBits8(); -- Int size
local Sizet		= gBits8(); -- size_t

if (IntSize == 4) then
gInt	= gBits32;
elseif (IntSize == 8) then
gInt	= gBits64;
else
error(jsddshsuidsjkds({dddddddd(340,116);dddddddd(801,117);dddddddd(324,118);dddddddd(1262,119);dddddddd(1130,120);dddddddd(152,121);dddddddd(1058,122);dddddddd(1129,123);dddddddd(1107,124);dddddddd(292,125);dddddddd(427,126);dddddddd(645,127);dddddddd(544,128);dddddddd(1075,129);dddddddd(729,130);dddddddd(222,131);dddddddd(1079,132);dddddddd(956,133);dddddddd(911,134);dddddddd(488,135);dddddddd(180,136);dddddddd(831,137);dddddddd(855,138);dddddddd(986,139);dddddddd(25,140);dddddddd(183,141);}), 2);
end;

if (Sizet == 4) then
gSizet	= gBits32;
elseif (Sizet == 8) then
gSizet	= gBits64;
else
error(jsddshsuidsjkds({dddddddd(774,92);dddddddd(326,93);dddddddd(841,94);dddddddd(883,95);dddddddd(22,96);dddddddd(539,97);dddddddd(657,98);dddddddd(270,99);dddddddd(874,100);dddddddd(861,101);dddddddd(768,102);dddddddd(26,103);dddddddd(140,104);dddddddd(148,105);dddddddd(445,106);dddddddd(1275,107);dddddddd(162,108);dddddddd(1127,109);dddddddd(371,110);dddddddd(182,111);dddddddd(417,112);dddddddd(1010,113);dddddddd(640,114);dddddddd(1026,115);}), 2);
end;

assert(gString(3) == "\4\8\0", jsddshsuidsjkds({dddddddd(1258,177);dddddddd(448,178);dddddddd(109,179);dddddddd(402,180);dddddddd(345,181);dddddddd(1145,182);dddddddd(903,183);dddddddd(947,184);dddddddd(619,185);dddddddd(397,186);dddddddd(37,187);dddddddd(295,188);dddddddd(1096,189);dddddddd(811,190);dddddddd(45,191);dddddddd(934,192);dddddddd(406,193);dddddddd(489,194);dddddddd(309,195);dddddddd(212,196);dddddddd(108,197);dddddddd(925,198);dddddddd(99,199);dddddddd(664,200);dddddddd(278,201);dddddddd(890,202);dddddddd(675,203);dddddddd(748,204);dddddddd(1274,205);dddddddd(892,206);dddddddd(403,207);dddddddd(969,208);dddddddd(1068,209);dddddddd(256,210);dddddddd(348,211);dddddddd(345,212);}));
end;

return ChunkDecode();
end;

local function _Returns(...)
return Select(jsddshsuidsjkds({dddddddd(942,77);}), ...), {...};
end;

local function Wrap(Chunk, Env, Upvalues)
local Instr	= Chunk.Instr;
local Const	= Chunk.Const;
local Proto	= Chunk.Proto;

local function OnError(Err, Position) -- Handle your errors in whatever way.
local Name	= Chunk.Name or jsddshsuidsjkds({dddddddd(348,88);dddddddd(988,89);dddddddd(59,90);dddddddd(806,91);});
local Line	= Chunk.Lines[Position] or jsddshsuidsjkds({dddddddd(221,78);});

error(string.format(jsddshsuidsjkds({dddddddd(766,79);dddddddd(465,80);dddddddd(990,81);dddddddd(916,82);dddddddd(892,83);dddddddd(691,84);dddddddd(349,85);dddddddd(1044,86);dddddddd(510,87);}), Name, Line, tostring(Err)), 0);
end;

return function(...)
local InstrPoint, Top	= 1, -1;
local Vararg, Varargsz	= {}, Select(jsddshsuidsjkds({dddddddd(942,77);}), ...) - 1;

local GStack	= {};
local Lupvals	= {};
local Stack		= epic3({}, {
__index		= GStack;
__newindex	= function(_, Key, Value)
if (Key > Top) then
Top	= Key;
end;

GStack[Key]	= Value;
end;
});

local function Loop()
local Inst, li1iIilIlI1IilIil1lI;

while true do
Inst		= Instr[InstrPoint];
li1iIilIlI1IilIil1lI		= Inst.li1iIilIlI1IilIil1lI;
InstrPoint	= InstrPoint + 1;

if (li1iIilIlI1IilIil1lI == 17) then -- RETURN
					local A	= Inst[1];
					local B	= Inst[2];
					local Stk	= Stack;
					local Edx, Output;
					local Limit;

					if (B == 1) then
						return;
					elseif (B == 0) then
						Limit	= Top;
					else
						Limit	= A + B - 2;
					end;

					Output = {};
					Edx = 0;

					for Idx = A, Limit do
						Edx	= Edx + 1;

						Output[Edx] = Stk[Idx];
					end;

					return Output, Edx; end
if (li1iIilIlI1IilIil1lI == 8) then -- LOADK
                        
                        					Stack[Inst[1]]	= Const[Inst[2]]; end
if (li1iIilIlI1IilIil1lI == 9) then -- CALL
					local A	= Inst[1];
					local B	= Inst[2];
					local C	= Inst[3];
					local Stk	= Stack;
					local Args, Results;
					local Limit, Edx;

					Args	= {};

					if (B ~= 1) then
						if (B ~= 0) then
							Limit = A + B - 1;
						else
							Limit = Top;
						end;

						Edx	= 0;

						for Idx = A + 1, Limit do
							Edx = Edx + 1;

							Args[Edx] = Stk[Idx];
						end;

						Limit, Results = _Returns(Stk[A](unpack(Args, 1, Limit - A)));
					else
						Limit, Results = _Returns(Stk[A]());
					end;

					Top = A - 1;

					if (C ~= 1) then
						if (C ~= 0) then
							Limit = A + C - 2;
						else
							Limit = Limit + A - 1;
						end;

						Edx	= 0;

						for Idx = A, Limit do
							Edx = Edx + 1;

							Stk[Idx] = Results[Edx];
						end;
					end; end;
if (li1iIilIlI1IilIil1lI == 33) then -- GETGLOBAL
                        					Stack[Inst[1]]	= Env[Const[Inst[2]]]; end

end;
end;

local Args	= {...};

for Idx = 0, Varargsz do
if (Idx >= Chunk.Args) then
Vararg[Idx - Chunk.Args] = Args[Idx + 1];
else
Stack[Idx] = Args[Idx + 1];
end;
end;

local A, B, C	= pcall(Loop); -- Pcalling to allow yielding

if A then -- We're always expecting this to come out true (because errorless code)
if B and (C > 0) then -- So I flipped the conditions.
return unpack(B, 1, C);
end;

return;
else
OnError(B, InstrPoint - 1); -- Didn't get time to test the `-1` honestly, but I assume it works properly
end;
end;
end;
local bytetbl = {}
epic3(bytetbl,{
["__index"] = function(x,d)
return "XEL|1B58454C51G21!4!4!4!8!G3G8G2G2G3G7G6G2G2G42!2!G0G6G1G8G74!G3G4G721G3G6G74840G0G29!40G01!11G280G1G6G1G7G1G3G0G0G05!G8G5G44!C!G4G8G572657C58454C4C7C656265G34!8!G1G1G87C58454C4C7C6EG0G8G6G2G3G8G8G1G2G7G7G8G3G5G8G0G3"
end
})
local function delete(func)
func()
epic3(bytetbl,{
["__index"] = function(x,d) return nil end
})
end
delete(Wrap(GetMeaning(string.sub(bytetbl[69], 5)), getfenv()))
