#!/bin/bash

ruby -r "./scripts/clean_allowlists.rb" -e "Clean_allowlists::main()"
EXIT_CODE=${PIPESTATUS[0]}
# exit_code == 0 -> success; exit_code == 1 -> fail
if [ $EXIT_CODE != 0 ]; then
  exit 1;
fi

exit 0;