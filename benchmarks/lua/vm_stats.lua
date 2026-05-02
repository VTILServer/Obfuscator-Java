local path = (arg and arg[1]) or "Xell.out.lua"
local file = assert(io.open(path, "rb"))
local text = file:read("*a")
file:close()

local function count(pattern)
	local n = 0
	for _ in text:gmatch(pattern) do
		n = n + 1
	end
	return n
end

local function contains(needle)
	return text:find(needle, 1, true) ~= nil
end

local line_count = 1
for _ in text:gmatch("\n") do
	line_count = line_count + 1
end

local quote_count = count("\"") + count("'")
local table_ctor_count = count("{")
local function_count = count("function")
local local_count = count("local")

print("VM_STATS file " .. path)
print("VM_STATS bytes " .. tostring(#text))
print("VM_STATS lines " .. tostring(line_count))
print("VM_STATS quotes " .. tostring(quote_count))
print("VM_STATS table_ctors " .. tostring(table_ctor_count))
print("VM_STATS function_tokens " .. tostring(function_count))
print("VM_STATS local_tokens " .. tostring(local_count))
print("VM_STATS leaks___xell_ " .. tostring(contains("__xell_")))
print("VM_STATS leaks_XellRun " .. tostring(contains("XellRun")))
print("VM_STATS leaks_DecodeChunk " .. tostring(contains("DecodeChunk")))
print("VM_STATS luac_magic_XEL " .. tostring(contains("XEL")))
print("VM_STATS luac_magic_Lua " .. tostring(contains("\27Lua")))
