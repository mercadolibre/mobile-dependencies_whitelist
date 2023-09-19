require 'json'
require 'date'

WEDNESDAY_DAY = 3
THURSDAY_DAY = 4
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

# Check if the expiration date is after the current date and if it is Wednesday or Thursday
def expirationDayCheck(hashDataList)
	today = Date.today
	hashDataList["whitelist"].each_entry do |entry, v|
		if entry.key?("expires")
			date = Date.parse(entry["expires"])
			if today > date
				puts "[ERROR] name:#{entry["name"]}, group: #{entry["group"]}, expires: #{entry["expires"]}, cannot expire on a past date"
                return false
			end	
			if [WEDNESDAY_DAY,THURSDAY_DAY].include?(date.wday)
				puts "[ERROR] name:#{entry["name"]}, group: #{entry["group"]}, expires: #{entry["expires"]}, cannot expire on wednesday or thursday"
                return false
			end
		end
	end
	true	
end


puts "File: #{ENV["FILE"]}"
dataHashFile = get_json_from_file(ENV["FILE"])

begin
 	if checkDatesAreParseable(dataHashFile) && expirationDayCheck(dataHashFile)
 		# no invalid dates found.
 		exit(0)
 	end

rescue Date::Error => e
	# we show a more friendly message.
	puts "[ERROR] Invalid date. Valid format should be: 'YYYY-MM-DD'"
	exit(1) # we return fail.
end