# Vote Verification

The Vote Verification application allows users to autonomously verify their vote is on correctly persisted on-chain.

In order to run the Vote Verification app, it is required to run three components:

1. ledger-follower-app
2. voting-verification-app
3. verification-ui

### Ledger Follower App

The Ledger Follower app is a service the crawls the blockchain looking for relevant information. In this case votes and 
voting events.

### Voting Verification App

The Voting Verification app is a service that leverages the Ledger Follower app to verify votes for a particular voting event.

### Verification UI

The verification UI is a user-friendly front end app that allows users to quickly verify their vote receipts. 

## Running the Vote Verification app

There are multiple ways of running the Vote Verification stack, but the quickest and more concise one is definitely via
[docker compose](https://docs.docker.com/compose/)

You can install docker compose [here](https://docs.docker.com/compose/install/)

In order to run the stack is sufficient to check-out the github project and execute:

`docker compose -f docker-compose-vote-verification.yml up -d`

It is possible to quickly visualise the services status by running

`docker ps -a`

And it should look something similar to:

```bash
docker ps -a
CONTAINER ID   IMAGE                                                                                                                                    COMMAND                  CREATED          STATUS         PORTS                            NAMES
6049aee2f6db   cf-voting-app-verification-ui                                                                                                            "/docker-entrypoint.…"   10 seconds ago   Up 9 seconds   80/tcp, 0.0.0.0:8080->8080/tcp   cf-voting-app-verification-ui-1
a0528218c570   pro.registry.gitlab.metadata.dev.cf-deployments.org/base-infrastructure/docker-registry/voting-ledger-follower-app:v0.2.74-1-g33bffc1b   "java -jar app.jar"      10 seconds ago   Up 9 seconds                                    cf-voting-app-follower-api-1
f43fd3af49db   pro.registry.gitlab.metadata.dev.cf-deployments.org/base-infrastructure/docker-registry/voting-verification-app:v0.2.74-1-g33bffc1b      "/docker-entrypoint.…"   10 seconds ago   Up 9 seconds   80/tcp, 0.0.0.0:9092->9092/tcp   cf-voting-app-verification-app-1
```

Logs can be checked by running:

`docker logs -f <container_nam>`

An example is

`docker logs -f cf-voting-app-follower-api-1`

# Accessing the UI

Now that everything is running, you can access the voting app in your browser by visiting http://localhost:8080

# Event Specific Configuring 

It is possible to customise the Vote Verification app with a specific event by configuring the following parameters.

Event specific values will be disclosed by the team close to the start of the voting event.

* `CARDANO_NETWORK=PREPROD` the network on which the event is setup, it can be `MAIN`, `PREPROD` or `PREVIEW`
* `ORGANISER_STAKE_ADDRESS` it's the stake address of the admin wallet used to setup and run the event. 
* A preprod example is `stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg`
* `BIND_ON_EVENT_IDS` it's the id of the event. Some preprod events are `CF_SUMMIT_2023_025E` or `CIP-1694_Pre_Ratification_3316`
* `CARDANO_NODE_PROTOCOL_MAGIC` it's a network specific id required to ensure follower app is connected to the right network
* `YACI_STORE_CARDANO_SYNC_START_BLOCK_HASH` it's the hash of the block from when the follower should start syncing. Preprod eg. `3337e297121fda1262372c28de3d917bd2a60bb1e3c6326e3b7e832eb8017615`
* `YACI_STORE_CARDANO_SYNC_START_SLOT` it's the slot number of the block at point above. Eg. 38748711
