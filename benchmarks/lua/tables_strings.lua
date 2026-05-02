local clock = os.clock

local rounds = tonumber((arg and arg[1]) or "3500") or 3500
local started = clock()
local bag = {}
local acc = 0

for i = 1, rounds do
	local key = "k" .. tostring(i % 257)
	local value = "value:" .. tostring((i * 17) % 65535)
	bag[key] = value
	if i % 3 == 0 then
		local text = bag["k" .. tostring((i * 11) % 257)] or value
		acc = acc + #text + string.byte(text, 1)
	end
end

local joined = {}
for i = 0, 128 do
	joined[#joined + 1] = bag["k" .. tostring(i)] or ""
end

print("BENCH_RESULT tables_strings " .. string.format("%.6f", clock() - started) .. " " .. tostring(acc + #table.concat(joined, "|")))
