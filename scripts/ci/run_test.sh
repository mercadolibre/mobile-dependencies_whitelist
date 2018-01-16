#!/bin/bash

set -e
set -o pipefail

source mobile-cd/scripts/base_script.sh
./gradlew -Dorg.gradle.daemon=true $CIRCLE_JOB$TEST_TASK -PdisablePreDex | sed -f "$STDOUT_FILTERS"
f_exit_on_error ""