# User Verification App

<p align="left">
    <img alt="Tests" src="https://github.com/cardano-foundation/cf-cardano-ballot/actions/workflows/test.yaml/badge.svg?branch=main" />
    <a href="https://cardano-foundation.github.io/cf-cardano-ballot/user-verification-service/coverage-report/index.html">
    <img alt="Coverage" src="https://cardano-foundation.github.io/cf-cardano-ballot/user-verification-service/coverage-report/badges/jacoco.svg" />
    </a>
</p>

## 🚀 Getting Started
This is the User Verification App, a Spring Boot application that verifies user wallets using their phone number (via SMS OTP one time password) or Discord account. 

```zsh
git clone https://github.com/cardano-foundation/cf-cardano-ballot.git
cd cf-cardano-ballot/backend-services/user-verification-service

./gradlew build

./gradlew test

# You would need to configure the app using the application.properties file
./gradlew bootRun
```

## 🧪 Test Reports

To ensure the stability and reliability of this project, we have implemented API tests based on REST Assured. By clicking on the links below, you can access the detailed coverage report

📊 [Coverage Report](https://cardano-foundation.github.io/cf-cardano-ballot/user-verification-service/coverage-report/index.html)