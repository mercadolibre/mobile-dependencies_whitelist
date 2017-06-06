#!/bin/sh

echo -e "Cloning: mobile-cd"
git clone "https://${GITHUB_PUBLIC_TOKEN}@github.com/mercadolibre/mobile-cd.git" --branch master --single-branch # <branch-to-clone> should be replaced with the version branch
bash ./mobile-cd/scripts/run_before_install.sh