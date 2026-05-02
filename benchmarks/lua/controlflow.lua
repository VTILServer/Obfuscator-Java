local clock = os.clock

local rounds = tonumber((arg and arg[1]) or "5000") or 5000
local started = clock()
local acc = 0

for i = 1, rounds do
	if i % 2 == 0 then
		acc = acc + i
	else
		acc = acc - (i % 97)
	end

	if i % 5 == 0 then
		acc = acc + 7
	elseif i % 7 == 0 then
		acc = acc - 11
	elseif i % 11 == 0 then
		acc = acc + 13
	end

	local j = 0
	while j < 4 do
		if (acc + j) % 3 == 0 then
			acc = acc + j * 2
		else
			acc = acc - j
		end
		j = j + 1
	end
end

print("BENCH_RESULT controlflow " .. string.format("%.6f", clock() - started) .. " " .. tostring(acc))
