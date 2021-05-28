#!/bin/bash

sudo gem install httparty

ruby -r "./scripts/test.rb" -e "Test::main()"

#EXIT_CODE=${PIPESTATUS[0]}


