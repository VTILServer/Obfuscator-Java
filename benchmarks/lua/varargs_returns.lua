local clock = os.clock

local rounds = tonumber((arg and arg[1]) or "4500") or 4500
local started = clock()

local function packsum(...)
	local sum = 0
	for i = 1, select("#", ...) do
		sum = sum + (select(i, ...) or 0)
	end
	return sum, sum % 97, sum % 193
end

local acc = 0
for i = 1, rounds do
	local a, b, c = packsum(i % 13, i % 17, i % 19, i % 23)
	acc = acc + a - b + c
end

print("BENCH_RESULT varargs_returns " .. string.format("%.6f", clock() - started) .. " " .. tostring(acc))
