#!/bin/bash

# pre-prod staging event

EVENT_ID="CF_SUMMIT_2025_25BCC"

TARGETS="02_create-infrastructure-platform-category-updated-pre-prod 03_create-impactful-spo-category-updated-pre-prod 04_create-impactful-native-asset-category-updated-pre-prod 05_create-developer-tooling-excellence-category-updated-pre-prod 06_create-governance-champion-category-updated-pre-prod 07_create-educational-influencer-category-updated-pre-prod 08_create-dex-category-updated-pre-prod 09_create-lending-protocol-category-updated-pre-prod 10_create-aggregator-category-updated-pre-prod 11_create-stablecoin-category-updated-pre-prod 12_create-tools-analytics-category-updated-pre-prod 13_create-other-dapps-category-updated-pre-prod"

docker run --env-file .preprod.env ballot-admin-cli:latest 01_create-cf-summit05-event-updated-pre-prod
sleep 30

for target in $TARGETS
do
    echo "**** try $target"
    while ! docker run --env-file .preprod.env ballot-admin-cli:latest $target --event $EVENT_ID
    do
        sleep 30
    done
    echo "**** $target successfull"

    sleep 30
done
