#!/bin/bash

# check if environment $FILE is set
if [ -z "$FILE" ]; then
  echo 'FILE environment variable is not set. for example you could use: export FILE=android-whitelist.json'
  exit 1;
fi

echo "Run linter: this step checks if file ($FILE) its well formed."
jsonlint "$FILE"

echo "Run JSON sorter lint: this step checks if the content of the file ($FILE) its properly sorted."
jsonsort "$FILE" --arrays

echo "Results: "
if [[ $(git ls-files -m | wc -l | xargs) != 0 ]]; then
    echo "[ERROR] $FILE needs to be properly linted or sorted."
    echo "before commiting run locally: jsonlint $FILE "
    echo "before commiting run locally: jsonsort $FILE --arrays "
    exit 1;
  else
    echo "$FILE its properly linted and sorted."
fi

echo "Run JSON expire validator: this step checks if the expires dates are in the proper format: YYYY-MM-DD."
ruby "./scripts/checkdate.rb"
EXIT_CODE=${PIPESTATUS[0]}
# exit_code == 0 -> success; exit_code == 1 -> fail
if [ $EXIT_CODE != 0 ]; then
  exit 1;
fi

echo "Run Check keynames"
ruby "./scripts/checkkeynames.rb"
EXIT_CODE=${PIPESTATUS[0]}
# exit_code == 0 -> success; exit_code == 1 -> fail
if [ $EXIT_CODE != 0 ]; then
  exit 1;
fi

echo "Run check version pattern"
ruby "./scripts/check_version_pattern.rb"
EXIT_CODE=${PIPESTATUS[0]}
# exit_code == 0 -> success; exit_code == 1 -> fail
if [ $EXIT_CODE != 0 ]; then
  exit 1;
fi
