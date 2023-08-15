## CF Cardano Ballot Apps

Voltaire Voting Applications to be used by Cardano Community to cast CIP-1694 pre-ratification vote. The applications are currently WIP (work in progress).

[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)
![Discord](https://img.shields.io/discord/1022471509173882950)

[![Voting-App-Build](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/voting-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/voting-app-build.yml)

[![Voting-Verification-App-Build](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/voting-verification-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/voting-verification-app-build.yml)

[![Voting-Admin-App-Build](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/voting-admin-app-build.yml/badge.svg)](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/voting-admin-app-build.yml)

[![UI-App-Build](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/ui-cypress-tests.yaml/badge.svg)](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/ui-cypress-tests.yaml)

## Requirements
- Node.js 18.x LTS
- Java 17 LTS
- Postgres DB 14.x

## Requirements (Development)
- Docker
- Docker-Compose

## Running (Development)

By default all backend apps are working with Cardano Pre-Production network.


- create `.env` file on the same level as `.env.development`

```shell
brew install maven
```

```shell
git clone git@github.com:cardano-foundation/merkle-tree-java.git
mvn clean install
```

```shell
git clone https://github.com/cardano-foundation/cip30-data-signature-parser
mvn install clean
```

```shell
cd cf-voting-app
rm -rf db
docker-compose rm
docker-compose up
```

```shell
cd cf-voting-app/backend-services/voting-app
./gradlew bootRun
```

this will launch main voting-app on port: 9090 by default.

```shell
cd cf-voting-app/backend-services/voting-verification-app
./gradlew bootRun
```

this will launch voting-verification-app on port: 9091 by default.

binding PORT can be change via SERVER_PORT env variable.

e.g. 
```
SERVER_PORT=8888 ./gradlew bootRun
```

```shell
npm run start
```

# Building native image with GraalVM
Applications are GraalVM compatible (https://www.graalvm.org/)

```shell
cd cf-voting-app/backend-services/voting-verification-app
./gradlew nativeCompile
cd cf-voting-app/backend-services/voting-app
./gradlew nativeCompile
```

# Developing locally with Yaci DevKit
If you want to develop using Yaci-DevKit (https://github.com/bloxbean/yaci-devkit) you have to start the backend applications in the special YACI_DEV_KIT DEV mode.

```shell
export SPRING_CONFIG_LOCATION=classpath:/application.properties,classpath:/application-dev--yaci-dev-kit.properties
export SPRING_PROFILES_ACTIVE=dev--yaci-dev-kit

java -jar cf-voting-admin-app.jar
```

This will effectively load `application.properties` and `application-dev-yaci-dev-kit.properties` file from the classpath 
but values / properties in `application-dev-yaci-dev-kit.properties` will override the ones in `application.properties`.

On start up of the app, you can verify if the right profile has been used, there should be a message related to that at the beginning.

## Repository Structure
- service - contains JAVA backend services
- ui - contains React.JS frontend code to cast votes / display voting results
