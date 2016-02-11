#!/bin/sh

curl -H "Authorization: token ${GITHUB_TOKEN}" \
  -H "Accept: application/vnd.github.v3.raw" \
  -L https://api.github.com/repos/mercadolibre/mobile-cd/contents/android/Rakefile?ref=master \
  -o Rakefile

rake release['base application library jacoco robolectric',bintray]
