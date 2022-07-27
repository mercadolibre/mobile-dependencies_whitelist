require 'json'
require 'date'
require 'net/http'
require 'uri'
require_relative 'util/slack_notification'

# Script that makes a list of libs that are close to be expired and sends a slack notification telling it
# Libs that expire in the current week
# Libs that expire in the next 30 days
module Notification
    ANDROID_ALLOWLIST_PATH_FILE = "./android-whitelist.json"
    IOS_ALLOWLIST_PATH_FILE = "./ios-whitelist.json"
    SLACK_WEBHOOK_URL_ANDROID = ENV['SLACK_NOTIFICATION_LIB_WEBHOOK']
    SLACK_WEBHOOK_URL_IOS = ENV['SLACK_NOTIFICATION_LIB_WEBHOOK_IOS']
    A_WEEK = 6
    A_MONTH = 30
    PLATFORM = "platform"
    ANDROID = "android"
    IOS = "ios"

    def self.is_going_to_expire_before(expireDate, maxDayToCompare, pastWeek)
	    puts "expireDate: " + expireDate.to_s + " maxDayToCompare: " + maxDayToCompare.to_s + " pastWeek: "+pastWeek.to_s
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

    def self.notify_platform(platform, pathFile, webhook)
        dataHash = get_json_from_file(pathFile)

        libsWeekly = get_libs_expiring(dataHash, A_WEEK, false, platform)
        libsMonthly = get_libs_expiring(dataHash, A_MONTH, true, platform)

        message = get_message(libsWeekly, libsMonthly)
        puts message

        send_slack_notification(message, webhook)
    end

    def self.main()
        notify_platform(ANDROID, ANDROID_ALLOWLIST_PATH_FILE, SLACK_WEBHOOK_URL_ANDROID)
        notify_platform(IOS, IOS_ALLOWLIST_PATH_FILE, SLACK_WEBHOOK_URL_IOS)
    end
end
