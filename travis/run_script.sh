#!/bin/bash

./gradlew $TEST_SUITE | sed -f "travis/stdout_filters.sed"

if [ ${PIPESTATUS[0]} -ne 0 ]; then
	exit 1
fi