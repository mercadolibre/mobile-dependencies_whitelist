require 'json'

# Parses the file as a JSON and returns a Hashmap
def get_json_from_file(pathFile)
	file = File.read pathFile
	return JSON.parse(file)
end

# finds in the file the given property and returns his value else returns nil
# ex: version = 1.0.0 -> 1.0.0
def getValueFromPropertyInFile(file, property)
	file.each_line do |line|
		if line.include? property
			return line.split("=")[1].strip
		end
	end
	return nil
end


# checks if the group its in the list
def isGroupInList(dataHashFile, group)
	dataHashFile["whitelist"].each_entry do |entry, v|
		if entry["group"].gsub('\\','') == group
			return true
		end
	end
	return false
end

# checks if the group its in the list
def isGroupInListiOS(dataHashFile, group)
	dataHashFile["whitelist"].each_entry do |entry, v|
		if entry["name"] == group
			return true
		end
	end
	return false
end