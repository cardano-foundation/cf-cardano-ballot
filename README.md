## Cardano Foundation | Cardano Ballot

A set of backend services and UI applications to facilitate CIP-1694 voting as well as Cardano Summit 2023 voting.

[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)
![Discord](https://img.shields.io/discord/1022471509173882950)

[![Voting-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-app-build.yml)

[![Voting-Verification-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-verification-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-verification-app-build.yml)

[![User-Verification-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/user-verification-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/user-verification-app-build.yml)

[![Voting-Admin-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-admin-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/voting-admin-app-build.yml)

[![UI-App-Build](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/ui-cypress-tests.yaml/badge.svg)](https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/ui-cypress-tests.yaml)

## Requirements
- Node.js 18.x LTS
- Java 17 LTS
- Postgres DB 14.x or H2 file db (local development / community running).

## Additional Docs
- [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- [SECURITY.md](SECURITY.md)
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [CHANGELOG.md](CHANGELOG.md)

## Running (Development)

By default all backend apps are working with Cardano Pre-Production network.

```shell
cd cf-ballot-app/backend-services/voting-ledger-follower-app
./gradlew bootRun
```
this will launch main voting-ledger-follower-app on port: 9090 by default.

```shell
cd cf-ballot-app/backend-services/voting-app
./gradlew bootRun
```

this will launch main voting-app on port: 9091 by default.

```shell
cd cf-ballot-app/backend-services/voting-verification-app
./gradlew bootRun
```

this will launch voting-verification-app on port: 9092 by default.

### User verification app on port
```bash
export AWS_SNS_ACCESS_KEY_ID=...
export AWS_SNS_SECRET_ACCESS_KEY=...
cd user-verification-service
./gradlew bootRun
```

this will launch user-verification-app on port: 9093 by default. Note that
user-verification-service is only needed for Cardano Summit 2023 voting.

Note that binding PORT can be changed via SERVER_PORT env variable.

e.g.
```
SERVER_PORT=8888 ./gradlew bootRun
```

use `setupProxy.js` to proxy services urls

- create `.env` file on the same level as `.env.development`

```shell
npm i
npm run start
```

# Building native image with GraalVM
Some applications should be GraalVM compatible (https://www.graalvm.org/)

```shell
export GRAALVM_HOME=/Users/mati/.sdkman/candidates/java/20.0.2-graalce

cd cf-ballot-app/backend-services/voting-verification-app
./gradlew nativeCompile
cd cf-ballot-app/backend-services/voting-app
./gradlew nativeCompile
cd cf-ballot-app/backend-services/user-verification-service
./gradlew nativeCompile

```

# Developing locally with Yaci DevKit
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

## Repository Structure
- backend-services - contains various backend services
- ui - contains React.JS frontend code apps to cast votes / display voting results
