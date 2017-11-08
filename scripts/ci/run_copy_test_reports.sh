mkdir -p tmp/build/reports
mkdir -p tmp/build/test-results
if [ -d "$CIRCLE_JOB/build/reports" ]; then
    cp -r $CIRCLE_JOB/build/reports tmp/build/reports
fi
if [ -d "$CIRCLE_JOB/build/test-results" ]; then
    cp -r $CIRCLE_JOB/build/test-results tmp/build/test-results
fi