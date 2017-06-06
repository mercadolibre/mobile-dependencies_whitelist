#!/bin/sh

bash ./mobile-cd/scripts/run_checks_leader_job.sh # this one checks if the job is the leader. It's optional and only works in after_success

if [ $? -ne 0 ]; then
    exit 1
fi

# saves the build size and/or deploys the lib/application
bash ./mobile-cd/scripts/run_after_success.sh
