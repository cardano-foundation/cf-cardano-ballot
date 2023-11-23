<div align="center">
  <img src="https://github.com/cardano-foundation/cf-cardano-ballot/blob/main/ui/summit-2023/public/static/Cardano_Ballot_black.png?raw=true#gh-light-mode-only" alt="Cardano Foundation | Cardano Ballot" height="150" />
  <img src="https://github.com/cardano-foundation/cf-cardano-ballot/blob/main/ui/summit-2023/public/static/Cardano_Ballot_white.png?raw=true#gh-light-mode-only#gh-light-mode-only" alt="Cardano Foundation | Cardano Ballot" height="150" />
  <hr />
    <h1 align="center" style="border-bottom: none">Cardano Foundation | Cardano Ballot</h1>

[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-%23FE5196?logo=conventionalcommits&logoColor=white)](https://conventionalcommits.org)
![GitHub release (with filter)](https://img.shields.io/github/v/release/cardano-foundation/cf-cardano-ballot)
![Discord](https://img.shields.io/discord/1022471509173882950)

[![Voting-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-app-build.yml)
[![Voting-Verification-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-verification-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-verification-app-build.yml)
[![User-Verification-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/user-verification-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/user-verification-app-build.yml)
[![Voting-Admin-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-admin-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-admin-app-build.yml)
[![UI-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/ui-cypress-tests.yaml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/ui-cypress-tests.yaml)

  <hr/>
</div>


# Overview

<div style="text-align: justify">
  Cardano Ballot is a user-friendly, hybrid on- and off-chain voting system developed by the Cardano Foundation.  Cardano Ballot leverages a set of backend services combined with frontend applications to facilitate voting within the Cardano Ecosystem.

  Most recently, stake-based voting was introduced into Cardano Ballot inorder to support IOG with CIP-1694 Pre-ratififcation polling events.  Currently, Cardano Ballot supports user-based (1 x user, 1 x vote) and stake-based (weighted) voting events.  The modularised backend services make the process of organising, deploying, and auditing a Cardano Ballot event more decentralized and user-friendly.

  In 2023, Hydra and Aiken Smart Contracts were also introduced into Cardano Ballot.  The first implementation of this was a final Hydra tally of all votes submitted for the Cardano Summit Awards 2023.
</div>

# Features
#### Event Types
- User-based 
- Stake-based 

#### Modularised Backend Service
  - Voting Admininsitration
  - Voting App
  - Ledger Follwer
  - Vote Commitment
  - Vote Verification
  - User Verification
  - Hydra Tally

  #### CIP-45 | Decentralized WebRTC d'App Wallet Communication
  #### CIP-93 | Authenticated Web3 HTTP Requests 
  #### YACI-Store
  #### YACI-Dev-Kit 
  #### Aiken Smart Contracts

# Getting Started

## Requirements
- Node.js 18.x LTS
- Java 17 LTS
- Postgres DB 14.x or H2 file db (local development / community running).


## Repository Structure
- [backend-services](backend-services) - contains various backend services:
  - [hydra-tally-app](backend-services/user-verification-service) - A CLI application which contains logic to connect to Hydra network. Application demonstrates usage of smart contracts (Aiken) to perform counting (tally) of the votes and providing result.
  - [user-verification-service](backend-services/user-verification-service) - A Spring Boot application that verifies user wallets using their phone number (via SMS OTP one time password) or Discord account
  - [vote-commitment-app](backend-services/vote-commitment-app) - TODO
  - [voting-admin-app](backend-services/voting-admin-app) - Application to be used by the organisers to create events and proposals.
  - [voting-app](backend-services/voting-app) - Voting Application that allows users to submit votes, receive vote receipts, and access leaderboard data.
  - [voting-ledger-follower-app](backend-services/voting-ledger-follower-app) - Ledger Follower Application that is listening to the Cardano blockchain to fetch information about event data and user stake amounts in case of stake-based voting.
  - [voting-verification-app](backend-services/voting-verification-app) - Application to be used by the community / voters to independently verify and check vote proofs.

- [ui](ui) - contains React.JS frontend code apps to cast votes / display voting results:
  - [cip-1694](ui/cip-1694) - front-end application for the CIP-1694 voting event.
  - [summit-2023](ui/summit-2023) - front-end application for the Cardano Summit 2023 Awards voting.

## Running the Backend Services
### Ledger Follower
By default all backend apps are working with Cardano Pre-Production network.

```shell
cd cf-ballot-app/backend-services/voting-ledger-follower-app
./gradlew bootRun
```
this will launch main voting-ledger-follower-app on port: 9090 by default.

### Voting App
```shell
cd cf-ballot-app/backend-services/voting-app
./gradlew bootRun
```

this will launch main voting-app on port: 9091 by default.

### Voting Verification
```shell
cd cf-ballot-app/backend-services/voting-verification-app
./gradlew bootRun
```

this will launch voting-verification-app on port: 9092 by default.

### User Verification App
```bash
export AWS_SNS_ACCESS_KEY_ID=...
export AWS_SNS_SECRET_ACCESS_KEY=...
cd cf-ballot-app/backend-services/user-verification-service
./gradlew bootRun
```

this will launch user-verification-app on port: 9093 by default.

**Note: user-verification-service is only needed for Cardano Summit 2023.**

**Note: binding PORT can be changed via SERVER_PORT env variable.**

e.g.
```
SERVER_PORT=8888 ./gradlew bootRun
```

use `setupProxy.js` to proxy services urls. 

## Running the Frontend (User Interface)
Copy the [`.env.example`](ui/summit-2023/.env.example) file and rename it as `.env`. 

Then run:

```shell
cd cf-ballot-app/ui/summit-2023
npm i
npm run start
```

## Backend -> Frontend Types Generation
All backend apps will generate TypeScript types for the frontend by using the following command:

As an example:
```shell
cd voting-app
./gradlew buildAndCopyTypescriptTypes -Pui_project_name=summit-2023
```
This will generate TypeScript types in the ui/summit-2023/build/typescript-generator/voting-app-types.ts

# Contributing

All contributions are welcome. Feel free to open a new thread on the issue tracker or submit a new pull request. Please read [CONTRIBUTING.md](CONTRIBUTING.md) first. Thanks!

## Additional Docs
- [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- [SECURITY.md](SECURITY.md)
- [CHANGELOG.md](CHANGELOG.md)