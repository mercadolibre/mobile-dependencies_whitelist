require 'json'
require 'date'
require 'net/http'
require 'uri'

def send_slack_notification(message, slackhook_url)
    if message == ""
        puts "Empty message"
        return
    end
    if slackhook_url == ""
        puts "Couldn't find the slack webhook ENV!! Not sending the notif."
        return
    end

    puts "Enviando notif"

    uri = URI.parse(slackhook_url)
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