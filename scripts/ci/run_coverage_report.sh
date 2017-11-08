#!/bin/bash

set -e
set -o pipefail

source mobile-cd/scripts/base_script.sh
./gradlew -Dorg.gradle.daemon=true $CIRCLE_JOB:jacocoFullReport -PdisablePreDex | sed -f "$STDOUT_FILTERS"
bash <(curl -s https://codecov.io/bash) -t $CODECOV_REPO_TOKEN

f_exit_on_error "Error with jacocoFullReport"