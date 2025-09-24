#!/bin/bash

# mainnet prod test event
EVENT_ID="CARDANO_SUMMIT_AWARDS_2025"

TARGETS="02_create-infrastructure-platform-category-updated-main 03_create-impactful-spo-category-updated-main 04_create-impactful-native-asset-category-updated-main 05_create-developer-tooling-excellence-category-updated-main 06_create-governance-champion-category-updated-main 07_create-educational-influencer-category-updated-main 08_create-dex-category-updated-main 09_create-lending-protocol-category-updated-main 10_create-aggregator-category-updated-main 11_create-stablecoin-category-updated-main 12_create-tools-analytics-category-updated-main 13_create-other-dapps-category-updated-main"


docker run --env-file .env ballot-admin-cli:latest 01_create-cf-summit05-event-updated-main
sleep 30

for target in $TARGETS
do
    echo "**** try $target"
    while ! docker run --env-file .env ballot-admin-cli:latest $target --event $EVENT_ID
    do
        sleep 30
    done
    echo "**** $target successfull"

    sleep 30
done
