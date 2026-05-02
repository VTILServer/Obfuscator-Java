local clock = os.clock

local rounds = tonumber((arg and arg[1]) or "6000") or 6000
local started = clock()
local acc = 0.25

for i = 1, rounds do
	local a = (i % 113) + 1
	local b = (i % 47) + 3
	acc = acc + (a * b) / (b + 1)
	acc = acc - (a % b)
	acc = acc + (a ^ 2 % 97)
	if acc > 1000000 then
		acc = acc / 3.14159
	end
end

print("BENCH_RESULT arithmetic " .. string.format("%.6f", clock() - started) .. " " .. string.format("%.3f", acc))
