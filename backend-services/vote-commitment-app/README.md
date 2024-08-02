## Vote Commitment Service

### Overview
Vote Commitment Service is a service that reads active events from ledger-follower-service and individual votes 
from database constructs merkle tree and at periodic, configurable intervals sends it to the Cardano blockchain.
On top of that once merkle tree is constructed for each vote a vote proof will be constructed and published to the 
local database.

### Technical Overview
Vote Commitment Service is a Spring Boot application written in java that uses Cardano Client Library
to post " vote commitments" to Cardano blockchain.

It uses fully functional merkle tree library written in Java to construct merkle tree from votes and periodically send it
to the blockchain for all active events.

It is recommended that there are at least two vote commitment services running in parallel to ensure high availability.
One commits at certain time and the other at a different time.

It is important to notice that application's internal jobs are not using any locking mechanism to ensure that 
only one job is running, however, technically it should be possible to run multiple jobs at the same time
(this scenario has so far not been tested as vote commitment jobs from more than 1 deployed instances have been running
at different time intervals).

### Tech Stack
- Spring Boot 3.2.x
- Java 21
- Gradle 8.5
- PostgreSQL / H2
- Bloxbean's Cardano Client Library 0.5.x
- Bloxbean's Yaci 0.2.x
- CF Merkle Tree Java (0.0.7)

### Dev Requirements
- JDK 21

### Development
```bash
cd cf-cardano-ballot/backend-services/vote-commitment-app
cp .env.template .env
# Update .env with required values (e.g. organiser's mnemonic)
# Run the service locally via:
./gradlew bootRun
```
