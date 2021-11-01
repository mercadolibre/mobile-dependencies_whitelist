#!/bin/bash

echo "Run linter: this step checks if file ($FILE) its well formed."
#jsonlint "$FILE"

echo "Run JSON sorter lint: this step checks if the content of the file ($FILE) its properly sorted."
#jsonsort "$FILE"

echo "Run JSON expire validator: this step checks if date are in the proper format YYYY-MM-DD."
pwd
ruby "./scripts/checkdate.rb"
EXIT_CODE=${PIPESTATUS[0]}
# exit_code == 0 -> success; exit_code == 1 -> fail
if [ $EXIT_CODE != 0 ]; then
  exit 1;
fi

echo "Results: "
if [[ $(git ls-files -m | wc -l | xargs) != 0 ]]; then
    echo "[ERROR] $FILE needs to be properly linted or sorted."
    exit 1;
  else
    echo "$FILE its ok."
    exit 0;
fi