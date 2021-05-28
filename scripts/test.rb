require 'json'
require 'date'
require 'net/http'
require 'uri'

module Test
	ANDROID_WHITELIST_PATH_FILE = "./android-whitelist.json"
	IOS_WHITELIST_PATH_FILE = "./ios-whitelist.json"
	A_WEEK = 6
	A_MONTH = 30

	def self.isGoingToExpireBefore(expireDate, maxDayToCompare, pastWeek)
		Date.parse(expireDate) >= Date.today + (pastWeek ? A_WEEK : 0) && Date.parse(expireDate) <= Date.today+maxDayToCompare
	end

	def self.formatAndroidEntryToString(entryNode)
		group = entryNode.key?("group") ? entryNode["group"].gsub("\\", "") : "unspecified"
		moduleName = entryNode.key?("name") ? entryNode["name"] : ""
		version = entryNode.key?("version") ? entryNode["version"].gsub("\\", "") : "ALL_VERSIONS"
		return "(" + entryNode["expires"] + ") :android: "+group+":"+moduleName+":"+version
	end

	def self.formatIosEntryToString(entryNode)
		moduleName = entryNode.key?("name") ? entryNode["name"] : ""
		version = entryNode.key?("version") ? entryNode["version"].gsub("\\", "") : "ALL_VERSIONS"
		return "(" + entryNode["expires"] + ") :apple3: " + moduleName +":"+version
	end

	def self.sort_by_date(arr)
	  arr.sort_by { |h| h["expires"].split('-') }
	end

	def self.getJsonFromFile(pathFile)
		file = File.read pathFile
		return JSON.parse(file)
	end

	def self.getLibsInExpires(fullLibList, untilDate, pastWeek)
		libs = []
		fullLibList["whitelist"].each_entry do |entry, v|
			if entry.key?("expires")
				lib_date = entry["expires"]
				if isGoingToExpireBefore(lib_date, untilDate, pastWeek)
					#puts lib_date
					libs.push(entry)
				end
			end
		end
		return libs
	end

	def self.sendNotification(message)
		puts "enviando notif"
		slack_url = ENV['SLACK_NOTIFICATION_LIB_WEBHOOK']

		uri = URI.parse(slack_url)
		header = {'Content-Type': 'application/json'}
		# Create the HTTP objects
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        request = Net::HTTP::Post.new(uri.request_uri, header)
        request.body = {
			message: message
	  	}.to_json
        response = http.request(request)
        puts response
	end

	def self.getMessage(libs_weeklyAndroid, libs_monthlyAndroid, libs_weeklyIos, libs_monthlyIos)
		libs_weeklyAndroid = sort_by_date(libs_weeklyAndroid)
		libs_monthlyAndroid = sort_by_date(libs_monthlyAndroid)

		libs_weeklyIos = sort_by_date(libs_weeklyIos)
		libs_monthlyIos = sort_by_date(libs_monthlyIos)

		weekly = ""
		libs_weeklyAndroid.each_entry do |entry, v|
			weekly += formatAndroidEntryToString(entry) + "\n"
		end
		monthly = ""
		libs_monthlyAndroid.each_entry do |entry, v|
			monthly += formatAndroidEntryToString(entry) + "\n"
		end

		libs_weeklyIos.each_entry do |entry, v|
			weekly += formatIosEntryToString(entry) + "\n"
		end
		libs_monthlyIos.each_entry do |entry, v|
			monthly += formatIosEntryToString(entry) + "\n"
		end

		message = ""
		if (weekly != "")
			message = "\nLibs que expiran esta semana: \n" + weekly
		end
		if (monthly != "")
			message += "\nLibs que expiran en los proximos 30 dias: \n" + monthly
		end

		if message.size > 0
			message += "\nPodes ver las versiones que deberias usar en la "
			message += "https://github.com/mercadolibre/mobile-dependencies_whitelist)"
		end
		return message
	end

	def self.main()
		data_hashAndroid = getJsonFromFile(ANDROID_WHITELIST_PATH_FILE)
		data_hashIos = getJsonFromFile(IOS_WHITELIST_PATH_FILE)
		#data_hash.merge(getJsonFromFile(IOS_WHITELIST_PATH_FILE))
		libs_weeklyAndroid = getLibsInExpires(data_hashAndroid, A_WEEK, false)
		libs_monthlyAndroid = getLibsInExpires(data_hashAndroid, A_MONTH, true)

		libs_weeklyIos = getLibsInExpires(data_hashIos, A_WEEK, false)
		libs_monthlyIos = getLibsInExpires(data_hashIos, A_MONTH, true)

		message = getMessage(libs_weeklyAndroid, libs_monthlyAndroid, libs_weeklyIos, libs_monthlyIos)

		sendNotification(message)
		puts message
	end

	main()
end