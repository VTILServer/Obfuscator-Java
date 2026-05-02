local clock = os.clock

local rounds = tonumber((arg and arg[1]) or "5000") or 5000
local started = clock()

local function make_step(seed)
	local state = seed
	return function(value)
		state = (state * 1103515245 + 12345) % 65536
		return (value + state) % 1000003
	end
end

local step_a = make_step(17)
local step_b = make_step(29)
local acc = 0

for i = 1, rounds do
	if i % 2 == 0 then
		acc = step_a(acc + i)
	else
		acc = step_b(acc - i)
	end
end

print("BENCH_RESULT calls_closures " .. string.format("%.6f", clock() - started) .. " " .. tostring(acc))
