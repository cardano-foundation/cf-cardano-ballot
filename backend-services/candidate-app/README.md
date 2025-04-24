# Candidate App

<p align="left">
    <img alt="Tests" src="https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/test.yaml/badge.svg?branch=main" />
    <a href="https://cardano-foundation.github.io/cf-cardano-ballot/candidate-app/coverage-report/index.html">
    <img alt="Coverage" src="https://cardano-foundation.github.io/cf-cardano-ballot/candidate-app/coverage-report/badges/jacoco.svg" />
    </a>
</p>

## 🚀 Getting Started

This is the Candidate Application that allows users to register as a candidate and view other candidates.

```zsh
git clone https://github.com/cardano-foundation/cf-cardano-ballot.git
cd cf-cardano-ballot/backend-services/candidate-app

./gradlew build

./gradlew test

# You would need to configure the app using the application.yml file

# PostgreSQL Database with default profile (requires db container)
./gradlew bootRun
# H2 Database with h2 profile (does not require db container)
./gradlew bootRun --args='--spring.profiles.active=h2'
```

## 🧪 Test Reports

To ensure the stability and reliability of this project, we have implemented API tests based on REST Assured. By clicking on the links below, you can access the detailed coverage report

📊 [Coverage Report](https://cardano-foundation.github.io/cf-cardano-ballot/candidate-app/coverage-report/index.html)