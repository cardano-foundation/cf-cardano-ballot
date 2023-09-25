# Cardano Ballot

<p align="left">
<img alt="Tests" src="https://github.com/cardano-foundation/cf-voting-app/actions/workflows/tests.yaml/badge.svg" />
<a href="https://conventionalcommits.org"><img alt="conventionalcommits" src="https://img.shields.io/badge/Conventional%20Commits-1.0.0-%23FE5196?logo=conventionalcommits" /></a>
</p>

## Getting started

## Available Scripts

In the project directory, you can run:

### `npm install`

This will install all packages in node_modules folder.

### `npm start`

Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.\
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.\

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

## Running backend services in localhost

### T1 Data follower app on port: 9090
Edit ``application.properties``:
1. Comment CIP-1694 Pre Ratification start block.
2. Uncomment CF Summit 2023 start block
```bash
cd voting-ledger-follower-app
./gradlew bootRun

```
### T2 Voting app on port: 9091
```bash
cd voting-app
./gradlew bootRun
```
### T3 Vote verification app on port: 9092
```bash
cd voting-verification-app
./gradlew bootRun
```
### T4 User verification app on port: 9093
```bash
export AWS_SNS_ACCESS_KEY_ID=...
export AWS_SNS_SECRET_ACCESS_KEY=...
cd user-verification-service
./gradlew bootRun
```

## Frontend requests
### Register phone number
```bash
var myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");

var raw = JSON.stringify({
  "eventId": "CF_SUMMIT_2023_24DC",
  "stakeAddress": "stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg",
  "phoneNumber": "+19144244762"
});

var requestOptions = {
  method: 'POST',
  headers: myHeaders,
  body: raw,
  redirect: 'follow'
};

fetch("localhost:9093/api/user-verification/start-verification", requestOptions)
  .then(response => response.text())
  .then(result => console.log(result))
  .catch(error => console.log('error', error));
```
#### Response
```bash
{
    "eventId": "CF_SUMMIT_2023_24DC",
    "stakeAddress": "stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg",
    "requestId": "fcf92e23-22d3-57a0-b25a-7348cdbe993b",
    "createdAt": "2023-09-01T15:54:53.471914",
    "expiresAt": "2023-09-01T16:09:53.466412"
}
```
### Confirm sms code
```bash
### Verify phone number with code

var myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");

var raw = JSON.stringify({
  "eventId": "CF_SUMMIT_2023_24DC",
  "stakeAddress": "stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg",
  "requestId": "fcf92e23-22d3-57a0-b25a-7348cdbe993b",
  "phoneNumber": "+19144244762",
  "verificationCode": "420374"
});

var requestOptions = {
  method: 'POST',
  headers: myHeaders,
  body: raw,
  redirect: 'follow'
};

fetch("localhost:9093/api/user-verification/check-verification", requestOptions)
  .then(response => response.text())
  .then(result => console.log(result))
  .catch(error => console.log('error', error));
```

#### Response
```bash
{
"verified": true
}
```

### Verify User
```bash
var myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");

var raw = JSON.stringify({
  "coseSignature": "HEX",
  "cosePublicKey": "HEX"
});

var requestOptions = {
  method: 'GET',
  headers: myHeaders,
  body: raw,
  redirect: 'follow'
};

fetch("localhost:9093/api/user-verification/verified/CF_SUMMIT_2023_24DC/stake_test1uqwcz0754wwpuhm6xhdpda6u9enyahaj5ynlc9ay5l4mlms4pyqyg", requestOptions)
  .then(response => response.text())
  .then(result => console.log(result))
  .catch(error => console.log('error', error));
  ```

#### Response
```bash
{
    "verified": true
}
```

### Generate types from backend
```bash
cd cf-ballot-app
cd backend-services/voting-app
./gradlew buildAndCopyTypescriptTypes -Pui_project_name=summit-2023
```
Repeat for every service.
