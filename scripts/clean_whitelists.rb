require 'json'
require 'date'
require 'net/http'

module Clean_whitelists
    ANDROID_WHITELIST_PATH_FILE = "./android-whitelist.json"
    IOS_WHITELIST_PATH_FILE = "./ios-whitelist.json"

    def self.get_json_from_file(pathFile)
        file = File.read pathFile
        return JSON.parse(file)
    end

    def self.save_json_to_file(hashData, pathFile)
        File.open(pathFile,"w") do |f|
            f.write(JSON.pretty_generate(hashData))
            f.write("\n") # we add this enter at the end, to avoid false JSONSORT check validation
        end
    end

    def self.removeExpired(hashDataList)
        listCopy = hashDataList
        hashDataList["whitelist"].each_entry do |entry, v|
            if entry.key?("expires") && is_expired(entry["expires"])
                listCopy["whitelist"].delete(entry)
            end
        end
        return listCopy
    end

    def self.is_expired(expireDate)
        Date.parse(expireDate) < Date.today
    end

	# Eliminamos las libs que estan expiradas de las listas y actualizamos el repo
    def self.main()
        dataHashAndroid = get_json_from_file(ANDROID_WHITELIST_PATH_FILE)
        dataHashIos = get_json_from_file(IOS_WHITELIST_PATH_FILE)

        cleanHash = removeExpired(dataHashAndroid)
        save_json_to_file(cleanHash, ANDROID_WHITELIST_PATH_FILE)
        cleanHash = removeExpired(dataHashIos)
        save_json_to_file(cleanHash, IOS_WHITELIST_PATH_FILE)

        res = `git diff --stat`
        puts res

        # if we have changes in the repo we create the PR
        if res && res.size > 0
            create_pr()
        end

    end

    def self.create_pr()
        currentDate = Date.today.to_s()
        puts "\nCreating pull request " + currentDate
        prBranchName = "fix/cleanExpiredLibs/" + currentDate

        system("git checkout -b " + prBranchName)
        system('git config user.email "cleaningBot@mercadolibre.com"')
        system('git config user.name "Cleaning Bot"')
        system('git commit -am "remove expired libs until: ' +currentDate + '"')
        system('git remote set-url origin https://mercadolibre:$GITHUB_TOKEN@github.com/mercadolibre/mobile-dependencies_whitelist.git')
        system('git push --set-upstream origin ' + prBranchName)
        system("git push origin " + prBranchName)

        url = "https://api.github.com/repos/mercadolibre/mobile-dependencies_whitelist/pulls"
        uri = URI.parse(url)

        header = {'Content-Type': 'application/json',
            'Accept': 'application/vnd.github.v3+json',
            'Authorization': "token " + ENV["GITHUB_TOKEN"]
        }
        # Create the HTTP objects
        http = Net::HTTP.new(uri.host, uri.port)
        http.use_ssl = true
        request = Net::HTTP::Post.new(uri.request_uri, header)
        request.body = {
            title: "[Trivial] Clean old expired libs",
            head: prBranchName,
            base: "master",
            body: "This Pull Request was generated automatically to delete expired libs"
        }.to_json
        response = http.request(request)
        puts response
    end
end