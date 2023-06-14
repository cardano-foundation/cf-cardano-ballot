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
```shell
docker-compose up -d
```
./gradlew bootRun
```

## Repository Structure
- service - contains java backend code needed for the frontend
- ui - contains React.JS frontend code to cast votes / display voting results
