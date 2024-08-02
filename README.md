<div align="center">
  <img src="https://github.com/cardano-foundation/cf-cardano-ballot/blob/main/ui/summit-2023/public/static/Cardano_Ballot_black.png?raw=true#gh-light-mode-only" alt="Cardano Foundation | Cardano Ballot" height="150" />
  <img src="https://github.com/cardano-foundation/cf-cardano-ballot/blob/main/ui/summit-2023/public/static/Cardano_Ballot_white.png?raw=true#gh-dark-mode-only" alt="Cardano Foundation | Cardano Ballot" height="150" />
  <hr />
    <h1 align="center" style="border-bottom: none">Cardano Foundation | Cardano Ballot</h1>

[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-%23FE5196?logo=conventionalcommits&logoColor=white)](https://conventionalcommits.org)
![GitHub release (with filter)](https://img.shields.io/github/v/release/cardano-foundation/cf-cardano-ballot)
![Discord](https://img.shields.io/discord/1022471509173882950)

[![Build and Publish Docker images](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/publish.yaml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/publish.yaml)
[![Voting-Verification-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-verification-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-verification-app-build.yml)
[![User-Verification-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/user-verification-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/user-verification-app-build.yml)
[![UI-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/ui-cypress-tests.yaml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/ui-cypress-tests.yaml)

  <hr/>
</div>

# Overview
  Cardano Ballot is a user-friendly, hybrid on- and off-chain voting system developed by the Cardano Foundation.  Cardano Ballot leverages a set of backend services combined with frontend applications to facilitate voting within the Cardano Ecosystem.
  
  Most recently, stake-based voting was introduced into Cardano Ballot inorder to support IOG with CIP-1694 Pre-ratification polling events.  Currently, Cardano Ballot supports user-based (1 x user, 1 x vote) and stake-based (weighted) voting events.  The modularised backend services make the process of organising, deploying, and auditing a Cardano Ballot event more decentralized and user-friendly.

  In 2023, Hydra and Aiken Smart Contracts were also introduced into Cardano Ballot.  The first implementation of this was a final Hydra tally of all votes submitted for the Cardano Summit Awards 2023.


# Features

| **Event Types**    | |   **Backend Service Modules**  | | **Cardano Standards, Libraries and Components** |           
|----------------|---------------|---------------|---------------|---------------|
|  User-based | |  Admin | | [CIP-45](https://github.com/cardano-foundation/CIPs/pull/395) - Decentralized WebRTC d'App Wallet Communication  | 
|  Stake-based | | Voting App |  | [CIP-93](https://cips.cardano.org/cips/cip93/) - Authenticated Web3 HTTP Requests  | 
| | | Ledger Follower | | [CIP-30](https://cips.cardano.org/cips/cip30/) - Cardano dApp-Wallet Web Bridge | 
| | | Vote Commitment | | [CIP-08](https://cips.cardano.org/cips/cip8/) - Message Signing |
| | | Vote Verification | |  Cardano Foundation - [cardano-connect-with-wallet](https://github.com/cardano-foundation/cardano-connect-with-wallet) |  
| | | User Verification | |  [Bloxbean Projects](https://github.com/bloxbean)  | 
| | | Hydra Tally | |  [Aiken](https://aiken-lang.org/) - A Modern Smart Contract Platform for Cardano  | 
| | | | | [Hydra](https://hydra.family/head-protocol/) - Head Protocol|




# Getting Started

## Requirements
- Node.js 18.x LTS
- Java 17 LTS
- Postgres DB 14.x or H2 file db (local development / community running)
- Docker


## Repository Structure
- [backend-services](backend-services) - Contains various backend services:
  - [hydra-tally-app](backend-services/user-verification-service) - A CLI application which contains logic to connect to the Hydra network. Application demonstrates usage of smart contracts (Aiken) to perform counting (tally) of the votes and providing results.
  - [user-verification-service](backend-services/user-verification-service) - A Spring Boot application that verifies user wallets using their phone number (via SMS OTP one time password) or Discord account.
  - [vote-commitment-app](backend-services/vote-commitment-app) - Service that reads active events from ledger-follower-service and individual votes 
from database constructs merkle tree and at periodic, configurable intervals sends it to the Cardano blockchain.
  - [voting-admin-app](backend-services/voting-admin-app) - Application to be used by the organisers to create events and proposals.
  - [voting-app](backend-services/voting-app) - Voting Application that allows users to submit votes, receive vote receipts, and access leaderboard data.
  - [voting-ledger-follower-app](backend-services/voting-ledger-follower-app) - Ledger Follower Application that is listening to the Cardano blockchain to fetch information about event data and user stake amounts in case of stake-based voting.
  - [voting-verification-app](backend-services/voting-verification-app) - Application to be used by the community / voters to independently verify and check vote proofs.
  - [keri-ballot-verifier](backend-services/keri-ballot-verifier) - A Python microservice to verify the votes from Cardano Ballot signed using KERI identifiers.

- [ui](ui) - Contains React applications for Cardano Ballopt event user interfaces:
  - [cip-1694](ui/cip-1694) - Frontend application for the CIP-1694 pre-ratification polling event.
  - [summit-2023](ui/summit-2023) - Frontend application for the Cardano Summit 2023 Awards voting.
  - [verification-app](ui/verification-app) - A generic frontend application for the verification / validation of the vote proof within a certain Cardano Ballot Event.

## Creating a Cardano Ballot Event
### Voting Admin App
By default all backend apps are working with Cardano Pre-Production network.

```shell
cd cf-cardano-ballot/backend-services/voting-admin-app
./gradlew bootRun
```

Instructions on how to create a new voting event can be found [here](./backend-services/voting-admin-app/EVENT_REGISTRATION.md).

## Running the Backend Services
### Ledger Follower
By default all backend apps are working with Cardano Pre-Production network.

```shell
cd cf-cardano-ballot/backend-services/voting-ledger-follower-app
./gradlew bootRun
```
This will launch main voting-ledger-follower-app on port: 9090 by default.
For a detailed description and interactive interface of the API, visit the Swagger UI documentation here:
[http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)

### Voting App
```shell
cd cf-cardano-ballot/backend-services/voting-app
./gradlew bootRun
```

This will launch main voting-app on port: 9091 by default.
For a detailed description and interactive interface of the API, visit the Swagger UI documentation here:
[http://localhost:9091/swagger-ui/index.html](http://localhost:9091/swagger-ui/index.html)

### Voting Verification
```shell
cd cf-cardano-ballot/backend-services/voting-verification-app
./gradlew bootRun
```

This will launch voting-verification-app on port: 9092 by default.
For a detailed description and interactive interface of the API, visit the Swagger UI documentation here:
[http://localhost:9092/swagger-ui/index.html](http://localhost:9092/swagger-ui/index.html)

Instructions on how to run the `Vote Verification` app can be found [here](./backend-services/voting-verification-app/README.md).

### Voting Commitment App
```bash
cd cf-cardano-ballot/backend-services/vote-commitment-app
cp .env.template .env
# Update .env with required values (e.g. organiser's mnemonic)
# Run the service locally via:
./gradlew bootRun
```

### User Verification App
```bash
export AWS_SNS_ACCESS_KEY_ID=...
export AWS_SNS_SECRET_ACCESS_KEY=...
cd cf-cardano-ballot/backend-services/user-verification-service
./gradlew bootRun
```

This will launch user-verification-app on port: 9093 by default.
[http://localhost:9093/swagger-ui/index.html](http://localhost:9093/swagger-ui/index.html)

**Note: user-verification-service is only needed for Cardano Summit 2023.**

**Note: binding PORT can be changed via SERVER_PORT env variable.**

e.g.
```
SERVER_PORT=8888 ./gradlew bootRun
```

Use `setupProxy.js` to proxy services urls.

### KERI Ballot Verifier
More instructions on the [README](backend-services/keri-ballot-verifier/README) of the microservice.

## Running the Frontend (User Interface)
Copy the [`.env.example`](ui/summit-2023/.env.example) file and rename it as `.env`. 

Then run:

```shell
cd cf-cardano-ballot/ui/summit-2023
cp .env.example .env
npm i
npm run start
```

## Backend -> Frontend Types Generation
All backend apps will generate TypeScript types for the frontend by using the following command:

As an example:
```shell
cd cf-cardano-ballot/backend-services/voting-app
./gradlew buildAndCopyTypescriptTypes -Pui_project_name=summit-2023
```
This will generate TypeScript types in the ui/summit-2023/build/typescript-generator/voting-app-types.ts


For your own project you will need to replace summit-2023 with your respective project ui directory name.

# Contributing

All contributions are welcome! Please feel free to open a new thread on the issue tracker or submit a new pull request.

Please read [Contributing](CONTRIBUTING.md) first. Thank you for contributing.

## Additional Docs
- [Code of Conduct](CODE_OF_CONDUCT.md)
- [Security](SECURITY.md)
- [Changelog](CHANGELOG.md)
