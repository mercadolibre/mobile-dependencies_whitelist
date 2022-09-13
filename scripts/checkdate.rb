require 'json'
require 'date'

# lets read the file
def get_json_from_file(pathFile)
	file = File.read pathFile
	return JSON.parse(file)
end

# If expire dates can be parsed then its OK else fail.
def checkDatesAreParseable(hashDataList)
	hashDataList["whitelist"].each_entry do |entry, v|
		if entry.key?("expires")
			Date.parse(entry["expires"])
		end
	end
	return true
end

puts "File: #{ENV["FILE"]}"
dataHashFile = get_json_from_file(ENV["FILE"])

begin
 	if checkDatesAreParseable(dataHashFile)
 		# no invalid dates found.
 		exit(0)
 	end

rescue Date::Error => e
	# we show a more friendly message.
	puts "[ERROR] Invalid date. Valid format should be: 'YYYY-MM-DD'"
	exit(1) # we return fail.
end