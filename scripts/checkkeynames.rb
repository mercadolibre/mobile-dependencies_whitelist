require 'json'

begin

    # Load the JSON file and parse it
    def get_json_from_file(pathFile)
        file = File.read pathFile
        return JSON.parse(file)
    end

    # an array with names of keys that should be present
    valid_keys = ['group', "name", 'version', 'expires', 'whitelist', 'description', "source", "target"]

    # for each hash in parsed_json check if the keys are valid
    def checkKeyNames(hashDataList, valid_keys)
        hashDataList["whitelist"].each do |node, value|
            node.each_entry do |keyname, value|
                if !valid_keys.include?(keyname)
                    return keyname
                end
            end
        end
        return ""
    end

    puts "File: #{ENV["FILE"]}"
    parsed_json = get_json_from_file(ENV["FILE"])

    # check if the keys are valid
    result = checkKeyNames(parsed_json, valid_keys)
    if result == ""
        puts "All key names are valid"
    else
        # we show a error message.
        puts "[ERROR] '" + result + "' is not a valid key name"
        puts "Valid key names:" + valid_keys.to_s
        exit(1) # pipeline fail for CI
    end

    exit(0) # successful
end