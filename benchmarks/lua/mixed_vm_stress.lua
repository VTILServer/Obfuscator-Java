local clock = os.clock

local rounds = tonumber((arg and arg[1]) or "2500") or 2500
local started = clock()

local function fibish(n)
	local a, b = 1, 1
	for _ = 1, n do
		a, b = b, (a + b) % 100000
	end
	return b
end

local function mapper(limit, fn)
	local out = {}
	for i = 1, limit do
		out[i] = fn(i)
	end
	return out
end

local values = mapper(64, function(i)
	return fibish(i % 22) + i
end)

local acc = 0
for i = 1, rounds do
	local value = values[(i % #values) + 1]
	if value % 2 == 0 then
		acc = acc + value / 2
	else
		acc = acc - value * 3
	end
	values[(i % #values) + 1] = (value + acc + i) % 100000
end

print("BENCH_RESULT mixed_vm_stress " .. string.format("%.6f", clock() - started) .. " " .. tostring(acc))
