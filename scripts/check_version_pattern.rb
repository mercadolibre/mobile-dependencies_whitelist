require 'json'

IOS_FILE_PREFIX = "ios-"
ANDROID_FILE_PREFIX = "android-"
KMP_FILE_PREFIX = "cross-kmp-"

IOS_EXCLUDED_LIBRARIES = [
  "MLDynamicModal",
  "MLUI",
  "MPDynamicSkeleton",
  "MPTopFloatingView",
  "MLBusinessComponents",
  "AndesUI",
  "AndesUI$",
  "AndesUI/(Core|AndesCoachmark|AndesBottomSheet|AndesDropdown|AndesTimePicker)",
  "AndesUI/SwiftUI"
]

ANDROID_EXCLUDED_LIBRARIES = [
  # Meli gradle plugin dont accept version "1+jason" as valid 
  "com\\.bitmovin\\.player:player",
]

LIBRARIES_NAMES_WITH_INVALID_VERSION = []

# Reads the file and returns its content as a JSON hash
def get_json_from_file(pathFile)
  file = File.read(pathFile)
  JSON.parse(file)
end

# Checks if the version in iOS public libraries isn't dynamic 
def check_version_pattern_ios(node)
  public_source = "public"
  dynamic_pattern = /\+\$|\[0-9\]|^\~>/

  name = node["name"]
  source = node["source"]
  version = node["version"]
  
  if source == public_source && version =~ dynamic_pattern && !IOS_EXCLUDED_LIBRARIES.include?(name)
    LIBRARIES_NAMES_WITH_INVALID_VERSION.push(name)
  end
end

# Checks if the version in Android or KPM public libraries isn't dynamic 
def check_version_pattern_android(node)
  private_group_prefix = ["mercadolibre", "mercadopago"]
  dynamic_pattern = /\.\+/

  group = node["group"]
  name = node["name"]
  version = node["version"]

  is_public = !private_group_prefix.any? { |prefix| group.include?(prefix) }

  if is_public && version =~ dynamic_pattern && !ANDROID_EXCLUDED_LIBRARIES.include?(group + ":" + name)
    LIBRARIES_NAMES_WITH_INVALID_VERSION.push(name)
  end
end

# Checks patterns in the data hash
def check_version_pattern(parsed_json, file_name)
  parsed_json["whitelist"].each do |node|
    
    if node.key?("version")
      # Ensure file name contains the iOS prefix
      if file_name.include?(IOS_FILE_PREFIX)
        check_version_pattern_ios(node)
      # Ensure file name contains the Android or KMP prefix
      elsif file_name.include?(ANDROID_FILE_PREFIX) || file_name.include?(KMP_FILE_PREFIX)
        check_version_pattern_android(node)
      end
    end
  end
end

puts "File: #{ENV["FILE"]}"

# Read the JSON file
file_name = ENV["FILE"]
parsed_json = get_json_from_file(file_name)

begin
  # Check version patterns
  check_version_pattern(parsed_json, file_name)

  if LIBRARIES_NAMES_WITH_INVALID_VERSION.size > 0
    # No invalid versions found
    LIBRARIES_NAMES_WITH_INVALID_VERSION.each do |name|
      puts "Error: '"+ name + "' is a public library and has a dynamic version"
    end
    # Invalid versions found
    exit(1)
  else
    # No invalid versions found
    exit(0)
  end
rescue StandardError => e
  # Handle any exceptions and exit with an error status code
  puts "Error: #{e.message}"
  exit(1)
end
