## Hydra Tally App

# Application Description

Hydra-Tally-App is a CLI application which contains logic to connect to Hydra network. Application demonstrates usage
of smart contracts (Aiken) to perform counting (tally) of the votes and providing result.

The application should be run in a fedration of hydra operators. It should be used to validate and assert results, which
are provided in a centralised manner.

# Disclaimer
Application is currently not ready to run in Byzantine environment. It should be hosted in a federated way. There are scenarios known,
in which a malicous actor could exploit the tally process, currently it serves as a Hydra / Aiken show-case.

# Removing Federation
In order to enable Hydra-Tally-App to work in a decentralised manner, the following limitations would have to lifted / solved:
- Deduplication of votes within Smart Contract (e.g. using https://github.com/micahkendall/distributed-set)
- Preventing any Hydra Operator to close the head while tallying the votes (e.g. by forcing them to lock up in a contract and slashing in case of early fan-out)
- Prevent accumulator eUTxO fraud, any Hydra operator could commit fraudulent eUTxO to the contract address (e.g. Watch Towers to check if eUTxO is pointing to the root via a fraud proof transaction, 2 contracts idea)
 
- private votes on hydra without early results publishing to the network (no idea yet) 
