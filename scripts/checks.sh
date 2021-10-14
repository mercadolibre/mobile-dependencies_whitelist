#!/bin/bash

echo "Run linter: this step checks if file its well formed"
jsonlint "$FILE"

echo "Run JSON sorter lint: this step checks if the content of the file its properly sorted"
jsonsort "$FILE"

echo "Results: "
if [[ $(git ls-files -m | wc -l | xargs) != 0 ]]; then
    echo "$FILE needs to be properly linted or sorted"
    exit 1;
  else
    echo "$FILE its ok"
    exit 0;
fi