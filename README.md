## Voltaire Voting Apps

Voltaire Voting Applications to be used by Cardano Community to cast CIP-1694 pre-ratification vote. The applications are currently WIP (work in progress).

[![Build](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/build.yml/badge.svg)](https://github.com/cardano-foundation/cf-voting-app/actions/workflows/build.yml)
[![License](https://img.shields.io:/github/license/cardano-foundation/cf-voting-app?label=license)](https://github.com/cardano-foundation/cf-voting-app/blob/master/LICENSE)
![Discord](https://img.shields.io/discord/1022471509173882950)

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


```shell
cd cf-voting-app/backend-services/voting-verification-app
SERVER_PORT=8888 ./gradlew bootRun
```

```shell
npm run start
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
