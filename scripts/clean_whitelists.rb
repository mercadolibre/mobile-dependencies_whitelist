require 'json'
require 'date'
require 'net/http'

module Clean_whitelists
    ANDROID_WHITELIST_PATH_FILE = "./android-whitelist.json"
    IOS_WHITELIST_PATH_FILE = "./ios-whitelist.json"
    puts "can execute class"

    def self.get_json_from_file(pathFile)
        file = File.read pathFile
        return JSON.parse(file)
    end

    def self.save_json_to_file(hashData, pathFile)
        File.open(pathFile,"w") do |f|
            f.write(JSON.pretty_generate(hashData))
        end
    end

    def self.removeExpired(hashDataList)
        #listCopy = {}
        listCopy = hashDataList
        #listCopy["whitelist"]
        hashDataList["whitelist"].each_entry do |entry, v|
            if entry.key?("expires") && is_expired(entry["expires"])
                    listCopy["whitelist"].delete(entry)
            end
            #if !entry.key?("expires") or !is_expired(entry["expires"])
            #    puts "something"
	        #    listCopy["whitelist"].add(entry)
			#end
        end
        #puts listCopy
        return listCopy
    end

    def self.is_expired(expireDate)
        Date.parse(expireDate) < Date.today
    end

    def self.main()
        puts "can execute main"
        dataHashAndroid = get_json_from_file(ANDROID_WHITELIST_PATH_FILE)
        dataHashIos = get_json_from_file(IOS_WHITELIST_PATH_FILE)

        cleanHash = removeExpired(dataHashAndroid)
        puts "Size cleanHash: " + cleanHash["whitelist"].size.to_s
        puts "Size dataHashAndroid: " + dataHashAndroid["whitelist"].size.to_s
        puts cleanHash["whitelist"].to_a == dataHashAndroid["whitelist"].to_a

        save_json_to_file(cleanHash, "pp.txt")

        cleanHash = removeExpired(dataHashIos)
        save_json_to_file(cleanHash, "ppios.txt")

        create_pr()
    end

    def self.create_pr()
    	currentDate = Date.today.to_s()
        puts "\nCreating pull request " + currentDate
        prBranchName = "fix/cleanExpiredLibs/" + currentDate

        res = system("git checkout -b " + prBranchName) #+ " --quiet >/dev/null 2>&1")
        puts res
        res = system("git add pp.txt")
        puts res
        res = system("git add ppios.txt")
        puts res

        res = system('git config user.email "cleaningBot@mercadolibre.com"')
        puts res
        res = system('git config user.name "Cleaning Bot"')
        puts res

        res = system('git commit -am "remove expired libs until: ' +currentDate + '"') #+ " --quiet >/dev/null 2>&1")
        puts res

        res = system("git push origin " + prBranchName) #+ " --quiet >/dev/null 2>&1")
        puts res

        url = "https://github.com/mercadolibre/mobile-dependencies_whitelist/pulls"
        uri = URI.parse(url)

        header = {'Content-Type': 'application/json',
			'Accept': 'application/vnd.github.v3+json',
			'Authorization': ENV["GITHUB_TOKEN_CLEAN_BOT"]
        }
        # Create the HTTP objects
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        request = Net::HTTP::Post.new(uri.request_uri, header)
        request.body = {
            title: "[Trivial] Clean old expired libs",
            head: prBranchName,
            base: "master",
            body: "This Pull Request deletes expired libs"
        }.to_json
        response = http.request(request)
		puts response
    end

    #main()
end