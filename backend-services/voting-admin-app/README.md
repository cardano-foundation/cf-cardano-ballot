## Voting Admin App

# Application Description
Application to be used by the organisers to create events and proposals.

# Run in docker
```
cd cf-cardano-ballot/backend-services/voting-admin-app
docker run -it --rm \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e CARDANO_NETWORK=main \
  -e BLOCKFROST_URL=https://cardano-preprod.blockfrost.io/api/v0 \
  -e BLOCKFROST_PASSWORD=<BLOCKFROST_PASS> \
  -e TX_SUBMIT_URL=https://usa.freeloaderz.io/api/submit/tx \
  -e ORGANISER_MNEMONIC_PHRASE="_CHANGE_ME_" \
  pro.registry.gitlab.metadata.dev.cf-deployments.org/base-infrastructure/docker-registry/voting-admin-app
```

