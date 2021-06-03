require 'json'
require 'date'

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
		listCopy = hashDataList
		hashDataList["whitelist"].each_entry do |entry, v|
			if entry.key?("expires")
				if is_expired(entry["expires"])
					listCopy["whitelist"].delete(entry)
				end
			end
		end
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
		save_json_to_file(cleanHash, "pp.txt")
		cleanHash = removeExpired(dataHashIos)
		save_json_to_file(cleanHash, "ppios.txt")

		puts 'git commit -am "remove expired libs"'
		create_pr()
    end

	def self.create_pr()
		puts "\nCreating pull request " + Date.today.to_s()
		#os.chdir(self.repo_path)
		prBranchName = "fix/cleanExpiredLibs/" + Date.today.to_s()

		res = system("git checkout -b " + prBranchName) #+ " --quiet >/dev/null 2>&1")
		res = system('git commit -am "remove expired libs"') #+ " --quiet >/dev/null 2>&1")
		res = system("git push origin " + prBranchName) #+ " --quiet >/dev/null 2>&1")

		url = "https://github.com/mercadolibre/mobile-dependencies_whitelist/pulls"
		uri = URI.parse(url)
		header = {'Content-Type': 'application/vnd.github.v3+json'}
		# Create the HTTP objects
		http = Net::HTTP.new(uri.host, uri.port)
		http.use_ssl = true
		request = Net::HTTP::Post.new(uri.request_uri, header)
		request.body = {
			title: "[Trivial] Clean old expired libs",
			head: PR_BRANCH_NAME,
			base: "master",
			body: "This Pull Request deletes expired libs"
		}.to_json
		# response = http.request(request)

	end

    main()
end