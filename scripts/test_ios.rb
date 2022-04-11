require 'json'
require './utils.rb'

PROPERTIES_FILE = "./PPMagnesWrapper.podspec" # generic *.podspec ???
ALLOWLIST_JSON_FILE = '../ios-whitelist.json'
GROUP_PROPERTY = "s.name"

# open file properties file and finds the value
file = File.open(PROPERTIES_FILE, "r")
repoPackage = getValueFromPropertyInFile(file, GROUP_PROPERTY).gsub('\'','')
file.close

if repoPackage
	puts "Property found in the FILE: #{repoPackage}"
else
	puts "Property not found in FILE"
	return
end

# we get the JSON file
dataHashFile = get_json_from_file(ALLOWLIST_JSON_FILE)

# we check if the group its in list.
isInTheList = isGroupInListiOS(dataHashFile, repoPackage)

puts "The package: #{repoPackage} #{isInTheList ? 'is a core library' : 'is not a core library' }"