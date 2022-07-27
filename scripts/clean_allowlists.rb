require 'json'
require 'date'
require 'net/http'
require_relative 'util/slack_notification'

# Script that checks and remove all libs that has expired dates and Makes a PR with the modifications
module Clean_allowlists
    ANDROID_ALLOWLIST_PATH_FILE = "./android-whitelist.json"
    IOS_ALLOWLIST_PATH_FILE = "./ios-whitelist.json"
    SLACK_WEBHOOK_FAIL_URL = ENV['SLACK_NOTIFICATION_FAIL_WEBHOOK']
    CIRCLE_BUILD_URL = ENV['CIRCLE_BUILD_URL']

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
        listCopy = {"whitelist" => []}
        hashDataList["whitelist"].each_entry do |entry, v|
            if !entry.key?("expires") || !is_expired(entry["expires"])
                listCopy["whitelist"].push(entry)
            end
        end
        return listCopy
    end

    def self.is_expired(expireDate)
        Date.parse(expireDate) < Date.today
    end

    def self.notify_error()
        send_slack_notification("CleanAllowList@Weekly: Ups, something happened and I couldn't make the weekly PR: " +
                            " please check this link:" +CIRCLE_BUILD_URL+ " for more details", SLACK_WEBHOOK_FAIL_URL)
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
            body: "This Pull Request was generated automatically to remove expired libs"
        }.to_json
        response = http.request(request)
        puts response
        return response
    end

    # We delete the libs that are expired from the lists and makes an PR updating the repo
    def self.main()
        begin
            puts "Starting clean AllowList"
            dataHashAndroid = get_json_from_file(ANDROID_ALLOWLIST_PATH_FILE)
            dataHashIos = get_json_from_file(IOS_ALLOWLIST_PATH_FILE)

            cleanHash = removeExpired(dataHashAndroid)
            save_json_to_file(cleanHash, ANDROID_ALLOWLIST_PATH_FILE)
            cleanHash = removeExpired(dataHashIos)
            save_json_to_file(cleanHash, IOS_ALLOWLIST_PATH_FILE)

            res = `git diff --stat`
            puts res

            # if we have changes in the repo we create the PR
            if res && res.size > 0
                response = create_pr()

                if !(response.kind_of? Net::HTTPCreated)
                    puts "post to github failed"
                    notify_error()
                    exit(1) # we return fail.
                end
            end
        rescue
            notify_error()
            exit(1) # we return fail.
        end
    end
end