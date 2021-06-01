require 'json'
require 'date'
require 'net/http'
require 'uri'

module Notification
    ANDROID_WHITELIST_PATH_FILE = "./android-whitelist.json"
    IOS_WHITELIST_PATH_FILE = "./ios-whitelist.json"
    SLACK_WEBHOOK_URL = ENV['SLACK_NOTIFICATION_LIB_WEBHOOK']
    A_WEEK = 6
    A_MONTH = 30
    PLATFORM = "platform"
    ANDROID = "android"
    IOS = "ios"

    def self.is_going_to_expire_before(expireDate, maxDayToCompare, pastWeek)
        Date.parse(expireDate) >= Date.today + (pastWeek ? A_WEEK : 0) && Date.parse(expireDate) <= Date.today+maxDayToCompare
    end

    def self.format_android_entry_to_string(entryNode)
        group = entryNode.key?("group") ? entryNode["group"].gsub("\\", "") : "unspecified"
        moduleName = entryNode.key?("name") ? entryNode["name"] : ""
        version = entryNode.key?("version") ? entryNode["version"].gsub("\\", "") : "ALL_VERSIONS"
        return "(" + entryNode["expires"] + ") :android: "+group+":"+moduleName+":"+version
    end

    def self.format_ios_entry_to_string(entryNode)
        moduleName = entryNode.key?("name") ? entryNode["name"] : "NO_NAME"
        version = entryNode.key?("version") ? entryNode["version"].gsub("\\", "") : "ALL_VERSIONS"
        return "(" + entryNode["expires"] + ") :apple3: " + moduleName +":"+version
    end

    def self.sort_by_date(arr)
      arr.sort_by { |h| h["expires"].split('-') }
    end

    def self.get_json_from_file(pathFile)
        file = File.read pathFile
        return JSON.parse(file)
    end

    def self.get_libs_expiring(fullLibList, untilDate, pastWeek, platform)
        libs = []
        fullLibList["whitelist"].each_entry do |entry, v|
            if entry.key?("expires")
                libDate = entry["expires"]
                if is_going_to_expire_before(libDate, untilDate, pastWeek)
                    entry[PLATFORM] = platform
                    puts entry
                    libs.push(entry)
                end
            end
        end
        return libs
    end

    def self.send_notification(message)
        if message == ""
            puts "Empty message"
            return
        end
        if SLACK_WEBHOOK_URL == ""
            puts "Couldn't find the slack webhook ENV!! Not sending the notif."
            return
        end

        puts "Enviando notif"

        uri = URI.parse(SLACK_WEBHOOK_URL)
        header = {'Content-Type': 'application/json'}
        # Create the HTTP objects
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        request = Net::HTTP::Post.new(uri.request_uri, header)
        request.body = {
            message: message,
        }.to_json
        response = http.request(request)
        puts response
    end

    def self.get_message(libs_weekly, libs_monthly)
        libs_weekly = sort_by_date(libs_weekly)
        libs_monthly = sort_by_date(libs_monthly)

        weekly = ""
        libs_weekly.each_entry do |entry, v|
            if entry[PLATFORM] == ANDROID
                weekly += format_android_entry_to_string(entry) + "\n"
            else
                weekly += format_ios_entry_to_string(entry) + "\n"
            end
        end

        monthly = ""
        libs_monthly.each_entry do |entry, v|
            if entry[PLATFORM] == ANDROID
                monthly += format_android_entry_to_string(entry) + "\n"
            else
                monthly += format_ios_entry_to_string(entry) + "\n"
            end
        end

        message = ""
        if (weekly != "")
            message = "\n• Libs que expiran esta semana: \n" + weekly
        end
        if (monthly != "")
            message += "\n• Libs que expiran en los proximos 30 dias: \n" + monthly
        end

        if message.size > 0
        	message = ":alerta: Friendly Reminder! :alerta:\n" + message
            message += "\nPodes ver las versiones que deberias usar en la "
            message += "https://github.com/mercadolibre/mobile-dependencies_whitelist"
        end
        return message
    end

    def self.main()
        dataHashAndroid = get_json_from_file(ANDROID_WHITELIST_PATH_FILE)
        dataHashIos = get_json_from_file(IOS_WHITELIST_PATH_FILE)

        libsWeekly = get_libs_expiring(dataHashAndroid, A_WEEK, false, ANDROID)
        libsMonthly = get_libs_expiring(dataHashAndroid, A_MONTH, true, ANDROID)

        libsWeekly.concat(get_libs_expiring(dataHashIos, A_WEEK, false, IOS))
        libsMonthly.concat(get_libs_expiring(dataHashIos, A_MONTH, true, IOS))

        message = get_message(libsWeekly, libsMonthly)
        puts message

        send_notification(message)
    end
end
