# Cardano Ballot KERI Verifier
A Python microservice to verify the votes from Cardano Ballot signed using KERI identifiers.

## How to run

### Build from source

#### Requirements

- Python 3
- pip 3

#### Steps

1. Create and activate a virtual enviroment

```bash
python -m venv venv
source venv/bin/activate
```

2. Build and install the binaries:
```bash
pip3 install -r requirements.txt
```

3. Execute the service:
```bash
./scripts/start_verifier.sh
```

### Run on docker
```bash
docker-compose up -d --build
```

## Endpoints
Once the app starts running, it exposes the ports `5666` and `5667` by default. The endpoints that interact with the application use port `5667`.

#### /oobi
- get: Retrieves and returns the CID associated with the given OOBI if it exists, otherwise returns a 404 status.
- post: Creates a new OOBI record with the current timestamp and stores it in the database, returning a 202 status.

#### /keystate
- get: Checks if the key state notification is complete for the given AID, returning a JSON response indicating completeness and a 200 status, or a 404 status if the AID is unknown.
- post: Removes key state notifications and state nonces associated with the given AID from the database, then appends the AID to the queries list and returns a 202 status.

#### /verify
- post: Verifies the provided signature against the payload for the given AID. Returns a 200 status if successful, or a 404 status if the AID is unknown, or a 400 status if the signature is invalid.

#### /health
- get: Returns a health status message with the current time, indicating the service is healthy, with a 200 status.

## Run the tests

1. Install pytest
```bash
pip install pytest
```

2. Run the tests
```bash
pytest
```