#!/bin/bash

set -e
set -o pipefail

rm -rf ./mobile-cd
echo -e "${CYAN}Cloning: mobile-cd ${NO_COLOR}"
git clone "https://${GITHUB_PUBLIC_TOKEN}@github.com/mercadolibre/mobile-cd.git" --branch master --single-branch

# runs the before_install from mobile-cd
bash ./mobile-cd/scripts/run_before_install.sh
