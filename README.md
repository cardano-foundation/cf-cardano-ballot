<div align="center">
  <img src="https://cryptologos.cc/logos/cardano-ada-logo.svg?v=026" alt="Cardano Foundation | Cardano Ballot" height="150" />
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

Cardano Ballot is a user-friendly, hybrid on- and off-chain voting system developed by the Cardano Foundation.  Cardano Ballot leverages a set of backend services combined with frontend applications to facilitate voting within the Cardano Ecosystem.  

Most recently, stake-based voting was introduced into Cardano Ballot inorder to support IOG with CIP-1694 Pre-ratififcation polling events.  Currently, Cardano Ballot supports user-based (1 x user, 1 x vote) and stake-based (weighted) voting events.  The modularised backend services make the process of organising, deploying, and auditing a Cardano Ballot event more decentralized and user-friendly.  

In 2023, Hydra and Aiken Smart Contracts were also introduced into Cardano Ballot.  The first implementation of this was a final Hydra tally of all votes submitted for the Cardano Summit Awards 2023.


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
- more...


## Repository Structure
- backend-services - contains various backend services
- ui - contains React.JS frontend code apps to cast votes / display voting results

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

**?** more..

## Running the Frontend (User Interface)
Create `.env` file on the same level as `.env.development`. 

Then run:

```shell
cd cf-ballot-app/ui/summit-2023
npm i
npm run start
```

## Building native image with GraalVM
Some applications should be GraalVM compatible (https://www.graalvm.org/)

**?** more...

```shell
export GRAALVM_HOME=/Users/mati/.sdkman/candidates/java/20.0.2-graalce

cd cf-ballot-app/backend-services/voting-verification-app
./gradlew nativeCompile
cd cf-ballot-app/backend-services/voting-app
./gradlew nativeCompile
cd cf-ballot-app/backend-services/user-verification-service
./gradlew nativeCompile

```

## Developing locally with Yaci DevKit
If you want to develop using Yaci-DevKit (https://github.com/bloxbean/yaci-devkit) you have to start the backend applications in the special YACI_DEV_KIT DEV mode.

```shell
cd cf-voting-app/backend-services/voting-ledger-follower-app

export SPRING_CONFIG_LOCATION=classpath:/application.properties,classpath:/application-dev--yaci-dev-kit.properties
export SPRING_PROFILES_ACTIVE=dev--yaci-dev-kit

./gradlew bootRun
```

This will effectively load `application.properties` and `application-dev-yaci-dev-kit.properties` file from the classpath 
but values / properties in `application-dev-yaci-dev-kit.properties` will override the ones in `application.properties`.

On start up of the app, you can verify if the right profile has been used, there should be a message related to that at the beginning.

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