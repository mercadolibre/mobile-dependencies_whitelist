require 'json'
require './utils.rb'

PROPERTIES_FILE = "./gradle.properties"
ALLOWLIST_JSON_FILE = '../android-whitelist.json'
GROUP_PROPERTY = "libraryGroupId"

# open file gradle.properties and finds the value for the property: libraryGroupId
file = File.open(PROPERTIES_FILE, "r")
repoPackage = getValueFromPropertyInFile(file, GROUP_PROPERTY)
file.close

if repoPackage
	puts "Property found in the FILE: \n\t #{GROUP_PROPERTY}=#{repoPackage}"
else
	puts "Property not found in FILE"
	return
end

# we get the JSON file
dataHashFile = get_json_from_file(ALLOWLIST_JSON_FILE)

# we check if the group its in list.
isInTheList = isGroupInList(dataHashFile, repoPackage)

puts "The package: #{repoPackage} #{isInTheList ? 'is a core library' : 'is not a core library' }"