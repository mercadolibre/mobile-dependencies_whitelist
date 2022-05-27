#!/bin/bash

ruby -r "./scripts/clean_whitelists.rb" -e "Clean_whitelists::main()"
EXIT_CODE=${PIPESTATUS[0]}
# exit_code == 0 -> success; exit_code == 1 -> fail
if [ $EXIT_CODE != 0 ]; then
  exit 1;
fi

exit 0;