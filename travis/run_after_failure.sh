#!/bin/bash

# this uploads a failure report to file.io. <report-dir> is the full directory with all reports (e. /moduledir/build/reports)
bash ./mobile-cd/scripts/run_after_failure.sh $MODULE_DIR/build/reports
