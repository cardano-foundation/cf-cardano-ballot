## Voting Admin App

# Application Description
Application to be used by the organisers to create events and proposals.

# Run in docker

```
docker run -it --rm \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e CARDANO_NETWORK=main \
  -e BLOCKFROST_URL=https://blockfrost-api.pro.dandelion-mainnet.eu-west-1.metadata.dev.cf-deployments.org \
  -e BLOCKFROST_PASSWORD=password \
  -e TX_SUBMIT_URL=https://submit-api.pro.dandelion-mainnet.eu-west-1.metadata.dev.cf-deployments.org/api/submit/tx \
  -e ORGANISER_MNEMONIC_PHRASE="_CHANGE_ME_" \
  pro.registry.gitlab.metadata.dev.cf-deployments.org/base-infrastructure/docker-registry/voting-admin-app
