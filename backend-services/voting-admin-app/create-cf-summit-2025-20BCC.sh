#! /bin/bash

EVENT_ID="CF_SUMMIT_2025_20BCC"

TARGETS="02_create-ambassador-category-pre-prod 03_create-blockchain-for-good-category-pre-prod 04_create-innovation-standards-category-pre-prod 05_create-dex-category-pre-prod 06_create-developer-tooling-category-pre-prod 07_create-educational-influencer-category-pre-prod 08_create-nft-digital-collectibles-category-pre-prod 09_create-infrastructure-platform-category-pre-prod 10_create-dao-tooling-governance-category-pre-prod 11_create-defi-platform-category-pre-prod 12_create-sspo-category-pre-prod"

docker run --env-file .env ballot-admin-cli:latest 01_create-cf-summit05-event-pre-prod 
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
