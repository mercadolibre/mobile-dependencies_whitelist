require 'json'
require 'net/http'
require 'uri'

begin

    # Load the JSON file and parse it
    def get_json_from_file(pathFile)
        file = File.read pathFile
        return JSON.parse(file)
    end

    puts "File: #{ENV["FILE"]}"
    parsed_json = get_json_from_file(ENV["FILE"])

    areDependenciesValid = true
    fails = Array[]

    parsed_json["whitelist"].each do |node, value|
          # Is a internal dependency
          if node["group"].include? "com\\.mercadolibre\\.android\\."
              request = "http://android.artifacts.furycloud.io/service/rest/v1/search/?"

              request + "repository=releases&"

              node.each_entry do |keyname, value|
                  if keyname == "group"
                      request + "group=#{value}"
                  elsif keyname == "name"
                      request + "name=#{value}"
                  elsif keyname == "version"
                      request + "version=#{value}"
                  end
              end

              uri = URI.parse(request)
              response = Net::HTTP.get_response(uri)

              puts response.body

              if response.body.include? "\"items\": [],"
                areDependenciesValid = false
                fails.push(node)
              end
          end
    end

    if areDependenciesValid == false
        # we show a error message.
        puts "[ERROR] These dependencies: '" + fails + "'  do not exists"
        exit(1) # pipeline fail for CI
    end

    exit(0) # successful
end