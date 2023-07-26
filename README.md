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
npm run start
```

## Repository Structure
- service - contains java backend code needed for the frontend
- ui - contains React.JS frontend code to cast votes / display voting results
