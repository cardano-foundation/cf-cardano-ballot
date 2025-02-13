# Changelog

## [0.2.75](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.74...v0.2.75) (2023-10-27)


### Features

* **cip-1694-ui:** change faq text ([4eda4ee](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4eda4eeff72d837a9e5c3ef428dec0b09e56637d))
* **cip-1694-ui:** change main title ([4408194](https://github.com/cardano-foundation/cf-cardano-ballot/commit/44081945823c0ee424f763a9f234a4758cc7e85f))
* **cip-1694-ui:** change vote to ballot ([f69493f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f69493fac3989a350a674b197402a1e9c5fba5b1))
* **cip-1694-ui:** provide status page url via env var ([#490](https://github.com/cardano-foundation/cf-cardano-ballot/issues/490)) ([9b0b2ea](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9b0b2eaac719e93097124d120729d2dd4c50a175))
* **cip-1694-ui:** remove view the leaderboard anyways option ([52d6842](https://github.com/cardano-foundation/cf-cardano-ballot/commit/52d684231ca8930ae4a07c5d36296e2986bea2f8))

## [0.2.74](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.73...v0.2.74) (2023-10-24)


### Bug Fixes

* **cip-1694-ui:** add onClose handlers for each dialog ([c78caf8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c78caf83e9efd1b88ef96b2088edefd12f2e211d))
* **cip-1694-ui:** change stats mapping for the leaderboard page ([10a394f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/10a394f216e1f37e75e648ae4f2b97dc4f9a5c07))

## [0.2.73](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.72...v0.2.73) (2023-10-20)


### Features

* **cip-1694-ui:** show not installed dependencies for mobile ([2fa7172](https://github.com/cardano-foundation/cf-cardano-ballot/commit/2fa717211193e02bf037caab0edbc894b45c6384))

## [0.2.72](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.71...v0.2.72) (2023-10-20)


### Features

* **cip-1694-ui:** change copy for the main question, change before event started intro page behaviour ([544c83f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/544c83fc000aeb67ab9780dff12d07d21e0a8006))

## [0.2.71](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.70...v0.2.71) (2023-10-20)


### Bug Fixes

* **cip-1694-ui:** add proper styles for button loaders ([a674f9f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a674f9f42b1f4d57f46fa6a72b115042715624e3))

## [0.2.70](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.69...v0.2.70) (2023-10-19)


### Features

* add api tests for ledger follower app ([2e33736](https://github.com/cardano-foundation/cf-cardano-ballot/commit/2e33736cd9df2df0bbcc3986bff1d8055c052acc))
* add BlockchainDataTest, fix minor issue regarding block hash ([d8e87cb](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d8e87cb92a2ade10dc79111cd28cd16c74e6999e))
* add ledger follower app to the test pipeline ([192519e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/192519e6ce2009a3848ac235ebe2ca0ef7647f3d))
* add tests for reference data endpoints ([3116d12](https://github.com/cardano-foundation/cf-cardano-ballot/commit/3116d125c09826141d6fd08bdf09d76e7369b1b8))
* **cip-1694-ui:** add faq link in the footer ([85f51e9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/85f51e9088e05483ace174df143a09dc781a623b))
* **cip-1694-ui:** allow closing confirm with signature modal ([41efa07](https://github.com/cardano-foundation/cf-cardano-ballot/commit/41efa075f8059b56a3f0c939dd40293c1bdc94f2))
* **cip-1694-ui:** change copy for the description in the confirm with wallet signature modal ([281a7ed](https://github.com/cardano-foundation/cf-cardano-ballot/commit/281a7ed25f69bcbb3c1e99539a018fb58cfe3504))
* **cip-1694-ui:** change voting closes and connect button colors ([b908387](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b908387c3b0a485b6f349c7eff2e8f53ae815e63))
* more intelligent caching voting receipt. ([#332](https://github.com/cardano-foundation/cf-cardano-ballot/issues/332)) ([d7a91a7](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d7a91a711b7df286acb3231fc4d64c6d8ee15b10))


### Bug Fixes

* change wiremock call to support new account info structure ([f12d729](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f12d729b80c7041f6401763cae38299bf34eb3a6))
* **cip-1694-ui:** after event closes fixes ([308c344](https://github.com/cardano-foundation/cf-cardano-ballot/commit/308c34432e2cf835276fdb04c71880a347371ce1))
* fixed issue where BackendService would fall in an infinite loop instead of returning stake key not found. ([05451b5](https://github.com/cardano-foundation/cf-cardano-ballot/commit/05451b538c12b11d1f78d0087e23c63e116bf895))
* improve leaderboard queries performance ([#455](https://github.com/cardano-foundation/cf-cardano-ballot/issues/455)) ([91c9450](https://github.com/cardano-foundation/cf-cardano-ballot/commit/91c9450359e9ab9e5c8d112bfc59d5b1fd651b5b))
* re-enabled periodic vote commitment. ([#462](https://github.com/cardano-foundation/cf-cardano-ballot/issues/462)) ([ed47660](https://github.com/cardano-foundation/cf-cardano-ballot/commit/ed4766095c434f65b2ffb275075a0a0a3e97c760))
* Remove spring-boot-starter-data-rest dependency to avoid auto-generation of REST apis for domain models and JPA repositories. ([#392](https://github.com/cardano-foundation/cf-cardano-ballot/issues/392)) ([7f75d8e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/7f75d8ef2baf82ecfde36ac9369c51ddeea78c1c))
* rollback issue fix after event window commitment finishes. ([#463](https://github.com/cardano-foundation/cf-cardano-ballot/issues/463)) ([36c6741](https://github.com/cardano-foundation/cf-cardano-ballot/commit/36c6741652bd63c349d7e89d2e30522165f3bb9e))

## [0.2.69](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.68...v0.2.69) (2023-10-11)


### Features

* **cip-1694-ui:** inject env vars via script ([#447](https://github.com/cardano-foundation/cf-cardano-ballot/issues/447)) ([4209ee8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4209ee8a9f1a46de663f62e6bd24f6df9c65d8f4))

## [0.2.68](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.67...v0.2.68) (2023-10-10)


### Features

* **cip-1694-ui:** make vote context optional ([f30729a](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f30729a47e905221f598eda4ed710fd9292fd10d))
* fix for category level stats. ([#444](https://github.com/cardano-foundation/cf-cardano-ballot/issues/444)) ([b8e9ea9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b8e9ea968a2d5e50f1329d6ca994dbea410a00e7))

## [0.2.67](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.66...v0.2.67) (2023-10-09)


### Features

* bloxbean CCL: 0.5.0 ([#434](https://github.com/cardano-foundation/cf-cardano-ballot/issues/434)) ([6421723](https://github.com/cardano-foundation/cf-cardano-ballot/commit/64217236dff1f6c9df779e662db682f8f4fcfa8b))

## [0.2.66](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.65...v0.2.66) (2023-10-05)


### Features

* added begin and cardwallet to cip1694 ([8071d3f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8071d3f0af1f965c8707f2bd766044473743b6ae))

## [0.2.65](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.64...v0.2.65) (2023-10-05)


### Features

* add api test for casting votes ([dfd2ac4](https://github.com/cardano-foundation/cf-cardano-ballot/commit/dfd2ac4352613c75de07c7b6ddd0ae968c7d35aa))
* add badge and coverage report generation to ci pipeline ([dfb63c0](https://github.com/cardano-foundation/cf-cardano-ballot/commit/dfb63c0b4a98fa60934aefe39376e859199f1540))
* add eternl beta version for cip45 ([6503f1f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6503f1f659e05be1e7d400f4377d51e0939413fa))
* add leaderboard tests and jacoco plugin ([d0474ea](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d0474ea2ea6ad8de0d47cad7e33bbe87080b56b7))
* add login api tests for voting-app ([d09848d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d09848db94bb98ab7dde540ae36c607a129ea152))
* add SupportedWalletsList component ([7e36bfb](https://github.com/cardano-foundation/cf-cardano-ballot/commit/7e36bfbff4c738100ab41c7fe3225d97f8b71964))
* finish testing for VoteResource ([580e583](https://github.com/cardano-foundation/cf-cardano-ballot/commit/580e583038e2e9d14d77e4b36af4e488813dfcf2))
* separate vote commitment service ([#357](https://github.com/cardano-foundation/cf-cardano-ballot/issues/357)) ([db146e8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/db146e8c3acc9449d7a9cb7a019aabbd3d21a318))


### Bug Fixes

* add EVENT_IS_NOT_ACTIVE to errors ([396fbf3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/396fbf34a88dc7c56a3d0cf67989b7ce0c2616f5))
* fixed the layout on userguide ([a9f5833](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a9f58330c22f01caedb658b465bd997c5935cc18))
* fullwidth button on homepage in mobile view ([4ad437b](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4ad437b29e78fec3e12e8f611e2432a41e6a7ee0))
* hexagon breaking on tablet or small laptops ([664ccc7](https://github.com/cardano-foundation/cf-cardano-ballot/commit/664ccc78d97d37cb7d509c73f5338076bc6de3a5))
* hide toast error on event not found ([e6507d6](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e6507d6c501082a1152f072955aff1c76fa45ff6))
* merge develop ([6fd3703](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6fd3703fd366a75a30c774b35d0e721db4ed2436))
* openSupportedWalletModal event name ([99aa76b](https://github.com/cardano-foundation/cf-cardano-ballot/commit/99aa76b5ea96b326470b16c5aeaa668993122f3d))
* phone code inputs in mobile ([500f6b0](https://github.com/cardano-foundation/cf-cardano-ballot/commit/500f6b0df0585dac613a6423a4c17055165d5a60))
* reduce login popups and add login button ([805c75c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/805c75ca3ff07d2ff9ec20c19180bf8f26447000))
* remove comment ([089568b](https://github.com/cardano-foundation/cf-cardano-ballot/commit/089568b1bb01ec67fc2377f7d38b37ee3c01a87e))
* remove debugs ([824a7d7](https://github.com/cardano-foundation/cf-cardano-ballot/commit/824a7d7d153e337afe3eff68a325314f73a2cc2e))
* remove debugs ([db45ea1](https://github.com/cardano-foundation/cf-cardano-ballot/commit/db45ea1c99dd827eda14e5cfc6fc6fa4ac4c9575))
* remove debugs ([af40e18](https://github.com/cardano-foundation/cf-cardano-ballot/commit/af40e183249541c7189e0fd7e1f994e0ce3c46e7))
* remove unused css class ([e0f341d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e0f341d5a9ebcbed23b682ca23a9134e712ec8e8))
* run prettier ([ab2a2ea](https://github.com/cardano-foundation/cf-cardano-ballot/commit/ab2a2ea13263d3aef814c478dbab27f1cfc00202))
* wallet name ([d8e2c1c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d8e2c1c2db97c54fde7a9a51154b200c59b8c3f1))

## [0.2.64](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.63...v0.2.64) (2023-10-02)


### Bug Fixes

* cip-30 parser 0.0.11 upgrade. ([#371](https://github.com/cardano-foundation/cf-cardano-ballot/issues/371)) ([5c17800](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5c1780078061ef5d2c49ff2a19f1baee20593bd2))
* typhon is not CIP-30 compatible, there is typhoncip30 wallet. ([#373](https://github.com/cardano-foundation/cf-cardano-ballot/issues/373)) ([8c3b3bd](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8c3b3bde30217cfea190f5527fb41d4453258858))

## [0.2.63](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.62...v0.2.63) (2023-09-29)


### Bug Fixes

* adding the new urls on discord links and change of text on popup ([8e3376e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8e3376e616de8dec9ce4d19fc0fafc21cb725b0d))
* **cip-1694-ui:** remove option selection on fetch receipt ([e43ad42](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e43ad426767d8e6ac4f1814fd7ebde09972f4e3b))
* footer fix ([4fdb846](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4fdb846a39be9b1d2441a3b14591b3c5d1e39663))
* mobile view broken layout fix ([99faa41](https://github.com/cardano-foundation/cf-cardano-ballot/commit/99faa41a1ba6f6271caa5eb2d27233f598b62a8c))
* nft color fix on leaderboard ([208b7c8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/208b7c889081ed83cf5ae1d5b449e6514bca8632))

## [0.2.62](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.61...v0.2.62) (2023-09-29)


### Features

* HEAD requests for vote receipts. ([#355](https://github.com/cardano-foundation/cf-cardano-ballot/issues/355)) ([bc4e58c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/bc4e58c6cdf723203ce30fe74b692e627a882bcb))


### Bug Fixes

* ci cd pipeline breaking due to account billing fix ([93cb122](https://github.com/cardano-foundation/cf-cardano-ballot/commit/93cb122663c582cd7689ab083dead496faa536ab))
* close discord modal ([0a25dbc](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0a25dbc7fc03423c0d2a410476c92e746f359711))
* hide login modal on discord verification starts ([fde9385](https://github.com/cardano-foundation/cf-cardano-ballot/commit/fde9385d16bd161ba7a35154d52e4ee5f4679487))
* link has been added on userguide card ([81bb6c5](https://github.com/cardano-foundation/cf-cardano-ballot/commit/81bb6c5f9dcaaaaec05faba552ec37ca716db477))
* open login modal on connect if not session ([5301661](https://github.com/cardano-foundation/cf-cardano-ballot/commit/530166185d3fcf06557cf66e73264ea1156cc885))
* remove debugs ([06e787a](https://github.com/cardano-foundation/cf-cardano-ballot/commit/06e787a0d67491e3a3447ec95a8246d55dd7e2d3))
* rounded numbers instead squares ([9da7f49](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9da7f4905517eff2b150f18c3df7507182b7f057))
* summit link and count down event dates ([6f15a3e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6f15a3ef915c256584d5d9428dee7d795d7785cd))
* undefined voted nominee ([4444d39](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4444d39108566c943ffdc0e4bf6f3097693f95f2))

## [0.2.61](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.60...v0.2.61) (2023-09-28)


### Bug Fixes

* cip45 ui ([be4fb65](https://github.com/cardano-foundation/cf-cardano-ballot/commit/be4fb655290f7e0f2e81dea90544e82eb26641c6))
* clean session on disconnect ([5e40a7d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5e40a7dbdadf3b62165c24ec9816af8cbf6b82f9))
* hide receipt banner if not wallet connected ([1669fcc](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1669fcce6d85c3ecbff9cae50505847af4199f14))
* layout fixes on all pages ([0315459](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0315459898c1749a47b769308eedf01b862a77e5))
* login after voting ends and remove debugs ([72e3fbd](https://github.com/cardano-foundation/cf-cardano-ballot/commit/72e3fbde84c80b98fa4baa326dfb3a55a8fc04f6))
* resolve conflicts ([09f21d6](https://github.com/cardano-foundation/cf-cardano-ballot/commit/09f21d606ccd54a7163550f77e26fc7f7a90a3ae))
* show see receipt after casting a vote and call user votes after casting a vote ([be1eb6e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/be1eb6ee2180640b2b1938ad32bd80e16f376671))
* Successfully cast vote and Receipt help texts ([57a5466](https://github.com/cardano-foundation/cf-cardano-ballot/commit/57a54665d8b84ca4a1f2a1312fdf4f90816c0588))

## [0.2.60](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.59...v0.2.60) (2023-09-28)


### Bug Fixes

* **cip-1694-ui:** remove user session on wallet disconnect, enforce code coverage ([2021071](https://github.com/cardano-foundation/cf-cardano-ballot/commit/202107100247a42617283602f087de66d87667a0))

## [0.2.59](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.58...v0.2.59) (2023-09-28)


### Features

* display winners in nominees ([da57a8e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/da57a8e82f02f78fdf510a924942e9b4a1cc37e3))
* sort nominees by vote and winner ([be7b2d0](https://github.com/cardano-foundation/cf-cardano-ballot/commit/be7b2d00a90c3d0ace097fe49bd1f37c4f3b56d7))


### Bug Fixes

* remove fixture ([f1d56bb](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f1d56bbe3ee9ee785735db95599b46fdf016fe6f))
* see login banner in nominees on event ended ([915f5c9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/915f5c9565890cbc343dde9823081b41f0a74e5e))
* show receipt banner if receipt exists in redux ([511e225](https://github.com/cardano-foundation/cf-cardano-ballot/commit/511e2255ef015773fb13ebaf973420bb70a6ea1a))

## [0.2.58](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.57...v0.2.58) (2023-09-28)


### Features

* attempting to force a release ([f373e7c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f373e7c3cc7ad8b3824b700d20dabf14e95c5ee3))

## [0.2.57](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.56...v0.2.57) (2023-09-27)


### Features

* new PRE-PROD event. ([#333](https://github.com/cardano-foundation/cf-cardano-ballot/issues/333)) ([c6d71b7](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c6d71b78f1250ea7ec56596c9b3da290b9517949))
* **verification-app:** initial commmit ([c0e3118](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c0e3118953c6e7f7b34bc9f3cc1da5b0923d0415))


### Bug Fixes

* disable verify wallet on event ends ([1e387f4](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1e387f482835f3cce6cf625ef0ae565eef8cdc8e))
* remove unnecessary debug ([9d5a7d4](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9d5a7d4d6657ceb1cf79915679ed979db1526000))

## [0.2.56](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.55...v0.2.56) (2023-09-27)


### Bug Fixes

* disable some caches to avoid bugs. ([#329](https://github.com/cardano-foundation/cf-cardano-ballot/issues/329)) ([5b185c9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5b185c99f0a825500f5ca9f61f0852b89627dc54))

## [0.2.55](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.54...v0.2.55) (2023-09-26)


### Bug Fixes

* develop to main: UI fixes ([75feb91](https://github.com/cardano-foundation/cf-cardano-ballot/commit/75feb919254a5d3da1f042d0bf4cd54179b70019))

## [0.2.54](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.53...v0.2.54) (2023-09-26)


### Bug Fixes

* if stake bound check is disabled then we should not use account history API. ([#324](https://github.com/cardano-foundation/cf-cardano-ballot/issues/324)) ([2881f18](https://github.com/cardano-foundation/cf-cardano-ballot/commit/2881f18d4714ceff3a6b90aa596a0f6d658e7453))

## [0.2.53](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.52...v0.2.53) (2023-09-26)


### Features

* cip1694 FRUITS_CF62 ([7ecf234](https://github.com/cardano-foundation/cf-cardano-ballot/commit/7ecf2341e4824ba3cb7425caefc4998bbbe30e22))

## [0.2.52](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.51...v0.2.52) (2023-09-26)


### Features

* ability to disable snapshot check as an application parameter. ([#319](https://github.com/cardano-foundation/cf-cardano-ballot/issues/319)) ([412123c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/412123c15a5382b62ab2e634e90400be568f06c6))

## [0.2.51](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.50...v0.2.51) (2023-09-26)


### Features

* new fruits test event, minor fixes as well as a new helper script. ([#317](https://github.com/cardano-foundation/cf-cardano-ballot/issues/317)) ([7ac1d85](https://github.com/cardano-foundation/cf-cardano-ballot/commit/7ac1d85c4e9628b8d9825f417e9f67a4e66a666c))

## [0.2.50](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.49...v0.2.50) (2023-09-25)


### Features

* show verify modal on connect wallet ([349a89e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/349a89e0cfde17515a31b1c812f6c67ac428f074))


### Bug Fixes

* add INVALID_NETWORK to errors ([42fd8c5](https://github.com/cardano-foundation/cf-cardano-ballot/commit/42fd8c5f39d076c5d05827a0035be2c2f25b48f1))
* hide QR code on receive cip45 request ([ed0865e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/ed0865e61229dd877cc4d7f220a40536e5e4d20b))
* include main in network type ([889544c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/889544cb052faccdedbdc25d3f03a0ef11666a9f))

## [0.2.49](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.48...v0.2.49) (2023-09-25)


### Bug Fixes

* yaci changed interface adjustment. ([847b8cc](https://github.com/cardano-foundation/cf-cardano-ballot/commit/847b8cc3629ca623155350883446a2030a77082e))

## [0.2.48](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.47...v0.2.48) (2023-09-25)


### Features

* Cache-Control headers. ([#305](https://github.com/cardano-foundation/cf-cardano-ballot/issues/305)) ([29ebeb2](https://github.com/cardano-foundation/cf-cardano-ballot/commit/29ebeb2e2641a3e1081134fbe193cc7df38fbf47))
* improved error messages. ([#304](https://github.com/cardano-foundation/cf-cardano-ballot/issues/304)) ([da0bccb](https://github.com/cardano-foundation/cf-cardano-ballot/commit/da0bccb71f4171be60619b0e1e6cbd09e5265593))
* libraries dependency updates ([#309](https://github.com/cardano-foundation/cf-cardano-ballot/issues/309)) ([2d82bcd](https://github.com/cardano-foundation/cf-cardano-ballot/commit/2d82bcdd86458b042bd167a098a0ea763beb69f7))
* made gradle task to generate UI types more generic. ([#300](https://github.com/cardano-foundation/cf-cardano-ballot/issues/300)) ([9d95f96](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9d95f96e9f5f931f7750e187403ecc9a8d8e2606))


### Bug Fixes

* event start date and event end date based on absolute slot calculation fix. ([#303](https://github.com/cardano-foundation/cf-cardano-ballot/issues/303)) ([1ef39b6](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1ef39b672a703084472b6dc4cd709231cbc5c466))
* IDE gradle plugin crash fixes. ([#308](https://github.com/cardano-foundation/cf-cardano-ballot/issues/308)) ([d98f392](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d98f392905fc8f90fd4a43f7659f142324d947b8))

## [0.2.47](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.46...v0.2.47) (2023-09-22)


### Features

* backend -&gt; frontend types re-generated. ([#293](https://github.com/cardano-foundation/cf-cardano-ballot/issues/293)) ([c574bc3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c574bc39d75372932c9278db2efcd61be958862b))
* summit 2023 prod test 2 ([#299](https://github.com/cardano-foundation/cf-cardano-ballot/issues/299)) ([cadc9c2](https://github.com/cardano-foundation/cf-cardano-ballot/commit/cadc9c2cf6e9485b3b2354837c1faa0abd2841f7))


### Bug Fixes

* backend types ([efaf0a0](https://github.com/cardano-foundation/cf-cardano-ballot/commit/efaf0a0ce4229047732ec117745e7822b678bfca))
* get votes after login ([4a6d002](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4a6d002be7e3ed303e3414ebe78d84efdf87c42f))
* get votes after login ([552c0f8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/552c0f8be6ea9d1cb6fd0b1797dc4db0eef2270b))
* get votes after login ([f619d27](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f619d27a80a47dbc75f460e5638d8e039cf2941e))
* get votes when not connected, not verified, no JWT ([9064ce8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9064ce8b61a0c8feb42565995b9de87cbe27fb55))
* hide vote button when already voted in category ([0298938](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0298938ee1ba6a38e63c735ff45cda2555925f2f))
* refactor getUserVotes ([3450889](https://github.com/cardano-foundation/cf-cardano-ballot/commit/345088988b93e4c0608be1a0128066804012b1a6))
* remove debugs and inline category description ([4deb878](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4deb878d66fa14937368b3abd8f252680e2f4ada))
* remove unused imports ([e39af1c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e39af1c471207174dff18f0263ce3520a6cbf629))

## [0.2.46](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.45...v0.2.46) (2023-09-22)


### Features

* replaced the names of nominees for testing ([e647e4c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e647e4c51c2868ae824f39a75716ceb2e3ec218d))


### Bug Fixes

* chain sync fix and removal of account status. ([#287](https://github.com/cardano-foundation/cf-cardano-ballot/issues/287)) ([f9633f2](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f9633f2d0652158daf650b437ed93dfc4642f9a0))
* match with content in prod ([5f9c4a1](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5f9c4a137dc00277bcc9860e0eea7e3d51cc709e))
* merge conflicts from develop ([8d80eae](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8d80eae48afef139c33c7df57ccdff0a03483100))
* summit content changes with ids and minor renames ([e142ee8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e142ee8f0e9dc0ef5c035f0225a5ffc4c5a6f449))
* when blockfrost falls behind, we still consider this to be healthy for follower-api. ([#290](https://github.com/cardano-foundation/cf-cardano-ballot/issues/290)) ([2ff6048](https://github.com/cardano-foundation/cf-cardano-ballot/commit/2ff6048099e965db74e93e98710a1eb1403e3048))

## [0.2.45](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.44...v0.2.45) (2023-09-21)


### Features

* deploying pizza event FRUITS_9172 ([9531d81](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9531d818bb22b623ee6874b7ef05987ba60d2cf8))
* more details on errors when details where misssing. ([#282](https://github.com/cardano-foundation/cf-cardano-ballot/issues/282)) ([315cc67](https://github.com/cardano-foundation/cf-cardano-ballot/commit/315cc675eed3aac3669d72fed1c0681d3cbbd72a))

## [0.2.44](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.43...v0.2.44) (2023-09-21)


### Features

* winner winner chicken dinner ([#279](https://github.com/cardano-foundation/cf-cardano-ballot/issues/279)) ([5da0d00](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5da0d004e5e61b3a6c857e88a5f884672bd1aa7f))
* yaci-store goes spring 3.1.x ([#275](https://github.com/cardano-foundation/cf-cardano-ballot/issues/275)) ([df35210](https://github.com/cardano-foundation/cf-cardano-ballot/commit/df352105d812b6cbfdc887165fce24c9f8d60c73))


### Bug Fixes

* slot diff between yaci and blockfrost CAN be negative, ie yaci being faster than blockfrost. Go YACI ([#281](https://github.com/cardano-foundation/cf-cardano-ballot/issues/281)) ([9c5d8c3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9c5d8c36a5343a84cfa6602e5dfa766c0ba744bc))
* voting-admin-app: pass the network to CCL code. ([8b020c1](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8b020c16137b01243675512a1c020a191b6ba1a1))

## [0.2.43](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.42...v0.2.43) (2023-09-20)


### Bug Fixes

* merkle proof generation fix. ([#270](https://github.com/cardano-foundation/cf-cardano-ballot/issues/270)) ([235f3cb](https://github.com/cardano-foundation/cf-cardano-ballot/commit/235f3cb1c9ebc8708f9b84b227841f628a148c61))
* vote-admin-cli pre-prod fix and added fruits test event. ([b3b5a3f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b3b5a3f6bcd005cf3d974bd982393bd3015f695c))

## [0.2.42](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.41...v0.2.42) (2023-09-20)


### Features

* gradle 8.3 upgrade. ([#264](https://github.com/cardano-foundation/cf-cardano-ballot/issues/264)) ([0534092](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0534092ad18adcca90df3072cd76d5d1b52097af))


### Bug Fixes

* allow actuator calls. ([#266](https://github.com/cardano-foundation/cf-cardano-ballot/issues/266)) ([8834436](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8834436f4e68594f27beec3a1b49a72fbdb1ea26))
* **cip-1694-ui:** optimize event image size, hide discrod icon ([de54ee3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/de54ee3cd22f247bd1fb8358aa3ae3d36e2d1504))
* **cip-1694-ui:** show next question btn on vote submit ([dd9c955](https://github.com/cardano-foundation/cf-cardano-ballot/commit/dd9c9557086909139b4702ba41579bd9b9665209))
* prevent vote casting when chain is not fully synced. ([#261](https://github.com/cardano-foundation/cf-cardano-ballot/issues/261)) ([84263f3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/84263f34a714ae53ce8fd5e102fe2f3d62e71882))

## [0.2.41](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.40...v0.2.41) (2023-09-19)


### Bug Fixes

* capitalize error message ([1d12873](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1d128737059d75d7b73b0f66d3973384f168261e))
* continue L1 commitments even tough event finishes, delay by number of slots and epochs for now. ([#257](https://github.com/cardano-foundation/cf-cardano-ballot/issues/257)) ([fc2390b](https://github.com/cardano-foundation/cf-cardano-ballot/commit/fc2390bae445b3bc2651ef54e43c49b7d831c82f))
* remove unnecessary toast and dep ([71bad68](https://github.com/cardano-foundation/cf-cardano-ballot/commit/71bad6882844897fcb642259bfb2e9193f914a1f))
* remove unused styles in terms&cond ([c621594](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c6215945e2ce5981319804cf14e9173e21e475e3))
* terms&cond modal width and backdrop ([9b992d1](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9b992d1388d811017aebef1be6521d9be774e025))
* using CustomButton in terms&cond ([6a0e9f7](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6a0e9f7e4243f8e7b9945f3e2138b6d9c141deb1))

## [0.2.40](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.39...v0.2.40) (2023-09-19)


### Features

* add login modal on session expired ([0a413dd](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0a413ddf32a0c4e5faa3345fccee73aa771117ad))


### Bug Fixes

* is verified typo in discord bot ([991e9b3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/991e9b38eeab1baa2eb01fb463c855139c769265))
* leaderboard alignment ([3178157](https://github.com/cardano-foundation/cf-cardano-ballot/commit/31781576b8582e056efa3f0371670aa0fb1658a3))
* updated event for cip1694 CIP-1694_Pre_Ratification_3316 ([393abd5](https://github.com/cardano-foundation/cf-cardano-ballot/commit/393abd574e44698a80a4f42e60017e178e228994))

## [0.2.39](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.38...v0.2.39) (2023-09-19)


### Features

* my votes on delivering list of categoryIds and proposalIds user already voted on (protected by JWT) ([#236](https://github.com/cardano-foundation/cf-cardano-ballot/issues/236)) ([22654f4](https://github.com/cardano-foundation/cf-cardano-ballot/commit/22654f4f9c01701454217f2bf59ba8c85fd81941))

## [0.2.38](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.37...v0.2.38) (2023-09-19)


### Features

* adjust bot to the backend endpoints ([8a40364](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8a403648008df0dbedfab914e76ac6df4ebf1933))

## [0.2.37](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.36...v0.2.37) (2023-09-18)


### Features

* discord fixes. ([#243](https://github.com/cardano-foundation/cf-cardano-ballot/issues/243)) ([4195aef](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4195aef0eb8cf99076584f6f715d595e7c7d4fe8))
* Discord user-verification flow and integration with Discord Bot. ([#210](https://github.com/cardano-foundation/cf-cardano-ballot/issues/210)) ([f9189fa](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f9189facd37c9dc4579b9490f651f5c91c8ff5f5))
* prod test - git ignore. ([0b58fd2](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0b58fd2b9fd9fb88b690b97c72a2bccaae2096df))
* prod test. ([c533b2a](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c533b2a72568577149611c8eb0c10d7f5f3f0b13))
* shelley account generation ([c53c6ef](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c53c6ef88420c34e7cff2aca3679ed84586f001c))
* shelley account generation. ([a30499e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a30499e49d0db72855a5faae5ddd220c43f450b8))


### Bug Fixes

* ability to run voting-app from command line. ([1c0349b](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1c0349b47f09f1b510e3229f04de9b014fba983b))
* **admin-cli:** ability to run voting-app from command line. ([af196ca](https://github.com/cardano-foundation/cf-cardano-ballot/commit/af196ca326a08b5e2a750aa124189f83bed030c9))
* **cip-1694-ui:** support second question on ui ([02a87f3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/02a87f3af48b5243ae75be9d3af31ea5753d446c))
* **cip-1694-ui:** update to the latest be services ([8057e40](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8057e40401eec41047ad493bf046f25b5fe795c5))

## [0.2.36](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.35...v0.2.36) (2023-09-14)


### Features

* keep memory low even for large number of votes. ([#204](https://github.com/cardano-foundation/cf-cardano-ballot/issues/204)) ([a121628](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a121628b352a2e7b670a09988566d9e10f09509b))


### Bug Fixes

* Transactional readOnly bug fix. ([#220](https://github.com/cardano-foundation/cf-cardano-ballot/issues/220)) ([52eac44](https://github.com/cardano-foundation/cf-cardano-ballot/commit/52eac44451445492f54887df33954724b5508dca))

## [0.2.35](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.34...v0.2.35) (2023-09-14)


### Bug Fixes

* dummy commit to force release-please ([34ec8ca](https://github.com/cardano-foundation/cf-cardano-ballot/commit/34ec8ca5639f7c57530f37b316311f5b486ebf96))

## [0.2.34](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.33...v0.2.34) (2023-09-13)


### Features

* web3 spring security ([#188](https://github.com/cardano-foundation/cf-cardano-ballot/issues/188)) ([a81e735](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a81e7359e668b6e34e8314a885e7b8e3a5000053))

## [0.2.33](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.32...v0.2.33) (2023-09-13)


### Bug Fixes

* removed the unused component ([753da52](https://github.com/cardano-foundation/cf-cardano-ballot/commit/753da52098b98d2282205433c25a5b051f3b30e5))

## [0.2.32](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.31...v0.2.32) (2023-09-13)


### Bug Fixes

* leaderboard results preinitialized with 0. ([#208](https://github.com/cardano-foundation/cf-cardano-ballot/issues/208)) ([5faa9de](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5faa9deca352a22a4cf8962e23373a411ad9d606))

## [0.2.31](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.30...v0.2.31) (2023-09-13)


### Features

* leaderboard overhaul ([#179](https://github.com/cardano-foundation/cf-cardano-ballot/issues/179)) ([bfd5173](https://github.com/cardano-foundation/cf-cardano-ballot/commit/bfd51739e588e4956a3ec92aa4e5097192c72164))

## [0.2.30](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.29...v0.2.30) (2023-09-12)


### Features

* fix viewVoteReceipt toggle ([9ab71c4](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9ab71c479a423761b545a02c2be64ec4db3f4f9e))
* qrcode for final receipt ([6cf2605](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6cf26050622ccd98c000d5ba3f32d244875bdd56))

## [0.2.29](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.28...v0.2.29) (2023-09-11)


### Bug Fixes

* added react-inject-env ([#187](https://github.com/cardano-foundation/cf-cardano-ballot/issues/187)) ([8144881](https://github.com/cardano-foundation/cf-cardano-ballot/commit/814488126e4cf4e03480fd5dff064a7587ef9036))

## [0.2.28](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.27...v0.2.28) (2023-09-11)


### Features

* versions upgrade. ([#192](https://github.com/cardano-foundation/cf-cardano-ballot/issues/192)) ([3459dce](https://github.com/cardano-foundation/cf-cardano-ballot/commit/3459dce7e007630a98854f999a72a8cb5697d57f))

## [0.2.27](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.26...v0.2.27) (2023-09-08)


### Bug Fixes

* add missing react-inject-env in deps ([34ef803](https://github.com/cardano-foundation/cf-cardano-ballot/commit/34ef803d659bb0ccabb1fddc2d1dabcb44893bf3))
* add missing react-inject-env script ([af1a474](https://github.com/cardano-foundation/cf-cardano-ballot/commit/af1a47447ea38df80ba44f9cbb3f73dd6cc5fc09))

## [0.2.26](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.25...v0.2.26) (2023-09-08)


### Features

* add app version ([68165d3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/68165d300c163453595f080a6de1817699e29c7c))


### Bug Fixes

* update README.md ([a7a8c99](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a7a8c992588fe1fb044e9337536bb84e4694fb70))
* vote receipt and nominee button after casting vote ([04680e9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/04680e9acec88971e78c4e2f60e082d0096a9b51))

## [0.2.25](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.24...v0.2.25) (2023-09-08)


### Features

* code quality improvements. ([#169](https://github.com/cardano-foundation/cf-cardano-ballot/issues/169)) ([a37de47](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a37de4760d3afdc9e4c59457f6c599a95d0f9e0f))

## [0.2.24](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.23...v0.2.24) (2023-09-08)


### Bug Fixes

* env variables for user verification ([1ce178a](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1ce178a3884c3aabe2147bb3fb4606c718b6273e))
* env variables for user verification ([f27f53b](https://github.com/cardano-foundation/cf-cardano-ballot/commit/f27f53b5017f11b4ed753f617f358d0add0e4558))

## [0.2.23](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.22...v0.2.23) (2023-09-08)


### Features

* attempting to force a release ([31e5106](https://github.com/cardano-foundation/cf-cardano-ballot/commit/31e510627095fee069d4be731f227bbacaee3a2a))

## [0.2.22](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.21...v0.2.22) (2023-09-08)


### Features

* rollback handling via vanilla Yaci and code cleanup. ([#158](https://github.com/cardano-foundation/cf-cardano-ballot/issues/158)) ([a2535d6](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a2535d66f3ddb1fb1e920f16557d18c30dd600d1))
* switching from prometheus summary to histograms ([#160](https://github.com/cardano-foundation/cf-cardano-ballot/issues/160)) ([1ea0679](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1ea06798c5e856d2ba41ba0f2f01821d89192998))

## [0.2.21](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.20...v0.2.21) (2023-09-06)


### Features

* mocks for discord verification bot. ([#157](https://github.com/cardano-foundation/cf-cardano-ballot/issues/157)) ([741b601](https://github.com/cardano-foundation/cf-cardano-ballot/commit/741b6012990d951792204e5f6ec013cb0d61f804))
* project documentation, security, code of conduct, etc. ([#156](https://github.com/cardano-foundation/cf-cardano-ballot/issues/156)) ([25c4464](https://github.com/cardano-foundation/cf-cardano-ballot/commit/25c4464e3f61c48d0ffd9f4dd88c0508c4b718f1))


### Bug Fixes

* improved Stake Address validation. ([#154](https://github.com/cardano-foundation/cf-cardano-ballot/issues/154)) ([5359d5d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5359d5de12ab93779922dbb38474948473c2c265))

## [0.2.20](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.19...v0.2.20) (2023-09-06)


### Bug Fixes

* explicit AOP plus loggin fix. ([085e42d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/085e42ddaea2bd27f8c858232879c84bccfd1dcd))
* removing unavailable presentation namefrom reducer ([ade2a24](https://github.com/cardano-foundation/cf-cardano-ballot/commit/ade2a2447c16bbe6bcd7489548c16e1d6a3f3240))
* we expect tx confirmation in 5 mins. ([11cdd17](https://github.com/cardano-foundation/cf-cardano-ballot/commit/11cdd1777e45c691722c56bc70a9b3a2014cdc37))

## [0.2.19](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.18...v0.2.19) (2023-09-06)


### Bug Fixes

* added aop in verification app ([425fbef](https://github.com/cardano-foundation/cf-cardano-ballot/commit/425fbef7193216f5acf7601842172c2af092a435))

## [0.2.18](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.17...v0.2.18) (2023-09-06)


### Features

* JDK-20 ([#148](https://github.com/cardano-foundation/cf-cardano-ballot/issues/148)) ([6511e66](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6511e663ba1f76eeb559c3bcd470fdd49c8c6992))
* JWT token support. ([#139](https://github.com/cardano-foundation/cf-cardano-ballot/issues/139)) ([c5e4a09](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c5e4a090af00096a92b1f4b01a2a17c95017ec05))


### Bug Fixes

* is verified hot fix. ([e6fe6cd](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e6fe6cdcb03ea934f63d49452705a48beb794eb9))
* missing content on list view ([6b9614d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6b9614d7c7e923839c2abc4f05c7a62d1a894efc))
* renaming proposal to nominee in FE. ([95a0559](https://github.com/cardano-foundation/cf-cardano-ballot/commit/95a0559de8ae6419045777e7d87e4a56d858c267))

## [0.2.17](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.16...v0.2.17) (2023-09-05)


### Features

* category and nominee pages with real data and readmore page with sidepage component ([9e3b3cd](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9e3b3cd0607f838f075c1a45459f76af7fa295e0))
* injecting proper env var for preprod ([#144](https://github.com/cardano-foundation/cf-cardano-ballot/issues/144)) ([e7b4d11](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e7b4d110fea3a06bffc1b5d645152ddd08376325))


### Bug Fixes

* missing name on card ([ec3f085](https://github.com/cardano-foundation/cf-cardano-ballot/commit/ec3f085ea2573ac96fadcfc6df38b4c80370a731))
* moved user verified to a new resource, it will be common for discord and sms flows. ([fc700c1](https://github.com/cardano-foundation/cf-cardano-ballot/commit/fc700c1413501f4976799345de61d2ac599baa49))
* wrong file has been added css ([57cd2f3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/57cd2f3caef9dc0023e0e6d7052e295d087c3574))
* wrong file has been added css ([6213dfa](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6213dfa43ddecc6a72f11832a3d09469eb744b09))

## [0.2.16](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.15...v0.2.16) (2023-09-05)


### Features

* added dockerfile, nginx conf and moved dev dep ([#137](https://github.com/cardano-foundation/cf-cardano-ballot/issues/137)) ([b13a806](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b13a8062dc3ec3afa2ed549bc3981633c28a2a45))
* remove i18n from the backend. ([#128](https://github.com/cardano-foundation/cf-cardano-ballot/issues/128)) ([df39319](https://github.com/cardano-foundation/cf-cardano-ballot/commit/df3931939cf943206f7b783b9477f3b12242885f))
* user-verification-service sms endpoints, salted phone numbers as well as used phone number protection ([#134](https://github.com/cardano-foundation/cf-cardano-ballot/issues/134)) ([8938848](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8938848181813a1820fa210d80a0684f516c6eb8))


### Bug Fixes

* create user-verification-app-build.yml ([582ae78](https://github.com/cardano-foundation/cf-cardano-ballot/commit/582ae786ad5cfaa08d918fb28669d86d975e2a1c))
* fixed build urls ([48ef658](https://github.com/cardano-foundation/cf-cardano-ballot/commit/48ef658fc6cb8d6bd162a39d7438caceaa6d6e7d))
* readme link fix ([c46bba4](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c46bba4022bfebc266dcc718d1fd73d95de51243))
* user-verification-app-build.yml typo ([9ec83f0](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9ec83f0c888a1fc63451df5ab61d308ffff304fe))
* wrong condition, voting power should not be present for USER_BASED_EVENTS. ([394afa5](https://github.com/cardano-foundation/cf-cardano-ballot/commit/394afa5aec495973e48b0fa5e9b9dfaac8bef4ff))

## [0.2.15](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.14...v0.2.15) (2023-09-04)


### Features

* split origins by comma to allow multiple CORS origin addresses ([#131](https://github.com/cardano-foundation/cf-cardano-ballot/issues/131)) ([5506ad3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5506ad3c397722b4464ccb9bde96edb032505bbe))
* user-verification-service, H2 for local dev, postgres for PROD. ([#88](https://github.com/cardano-foundation/cf-cardano-ballot/issues/88)) ([4273ef1](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4273ef1b4cc594f55598cbcea01c7fa7db48cc3c))


### Bug Fixes

* **cip-1694:** update ui tests ([7bf0930](https://github.com/cardano-foundation/cf-cardano-ballot/commit/7bf0930463ce4d8fe4ee2d6ce91e2de582c7d7cd))

## [0.2.14](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.13...v0.2.14) (2023-09-01)


### Bug Fixes

* **cip-1694:** wait for absolute slot to vote ([4baf24d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/4baf24d68d00f8e5a9be8a9c070c82b9352da062))

## [0.2.13](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.12...v0.2.13) (2023-09-01)


### Bug Fixes

* **cip-1694:** uncomment logs ([dd975eb](https://github.com/cardano-foundation/cf-cardano-ballot/commit/dd975eb6ed4b82f5dc07c04a4c1ecf9e3aa8fa6f))
* **cip-1694:** uncomment logs ([1561420](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1561420bffb84ee5aa40722b543e60aa1a5ddf20))

## [0.2.12](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.11...v0.2.12) (2023-09-01)


### Bug Fixes

* **cip-1694:** hide modals on vote not found ([77fe463](https://github.com/cardano-foundation/cf-cardano-ballot/commit/77fe4638269fbfdeba6aeb748e7fdd310cdf143f))

## [0.2.11](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.10...v0.2.11) (2023-09-01)


### Features

* **cip-1694:** add preloaders, confirmation modals ([1b61063](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1b61063d829ad808da53d50054f600d51889250f))
* event and category leaderboard availability check. ([e407d6a](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e407d6a16c9474cdd9c5b7bd019ed5d3b0bab446))

## [0.2.10](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.9...v0.2.10) (2023-08-31)


### Features

* **cip-1694:** add t&c and privacy policy ([3d89a61](https://github.com/cardano-foundation/cf-cardano-ballot/commit/3d89a6121b9737c1d9f3f553e85470125dcac2d6))


### Bug Fixes

* **cip-1694:** change proxy port for leaderboard url, fix cta sizing on vote page ([d3d7bd2](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d3d7bd236a3006eb28ee7bc0b74e7cc4caccdedb))

## [0.2.9](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.8...v0.2.9) (2023-08-29)


### Features

* attempting to force a release ([27dc303](https://github.com/cardano-foundation/cf-cardano-ballot/commit/27dc30303c685b2d1b9a49b3f634276228164c7c))

## [0.2.8](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.7...v0.2.8) (2023-08-29)


### Features

* init summit 2023 UI ([4270818](https://github.com/cardano-foundation/cf-cardano-ballot/commit/42708180b5bfc9d48eb0d12a838bfda20e66ec3b))


### Bug Fixes

* **cip-1694:** add debounce to the wallet on connect cb ([315ea98](https://github.com/cardano-foundation/cf-cardano-ballot/commit/315ea98b1bdbda8238fcf856f4c36d1cbacce379))
* **cip-1694:** fix blockchain tip and voting power urls, hardcode verification service responce type, fix copy vote value ([740648d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/740648d1bd9ec900cb74825a64cbdba90a707192))
* **cip-1694:** wallet connected styling ([deca267](https://github.com/cardano-foundation/cf-cardano-ballot/commit/deca267060a3066f8442c75a0cf32735d6721467))
* public service layer compatible with resource layer. ([#103](https://github.com/cardano-foundation/cf-cardano-ballot/issues/103)) ([670f2b8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/670f2b8fe182468e433f79bc5a651363e9cd406b))
* remove slides ([1da75e6](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1da75e627f125e597d65192bcf74572420b352f4))
* remove unused resources ([5c30a6f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/5c30a6f74dd534ef5065eaf60d641dd6b6ef321a))
* update hostnames after endpoints were moved ([#92](https://github.com/cardano-foundation/cf-cardano-ballot/issues/92)) ([8982367](https://github.com/cardano-foundation/cf-cardano-ballot/commit/898236719160e29d2d72e070fa3c9e70e6d927d7))
* update readme ([bb74478](https://github.com/cardano-foundation/cf-cardano-ballot/commit/bb7447819300f02f2fa349ff71ec294f5405f8ac))
* update to summit 2023 name ([a744db9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a744db904815ba4e696dadac92fdbf92906e9f1b))
* updated hostnames after endpoints were moved ([8982367](https://github.com/cardano-foundation/cf-cardano-ballot/commit/898236719160e29d2d72e070fa3c9e70e6d927d7))

## [0.2.7](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.6...v0.2.7) (2023-08-28)


### Features

* CIP-1694 integration tests. ([#84](https://github.com/cardano-foundation/cf-cardano-ballot/issues/84)) ([b88eece](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b88eece3bdc52ab0b56091d372f605c8a2865df0))
* prepare CF Summit Voting 2023. ([#80](https://github.com/cardano-foundation/cf-cardano-ballot/issues/80)) ([da5ccba](https://github.com/cardano-foundation/cf-cardano-ballot/commit/da5ccbab55b112ca99861414447456ce28bd8482))
* versions upgrade ([#87](https://github.com/cardano-foundation/cf-cardano-ballot/issues/87)) ([39eb247](https://github.com/cardano-foundation/cf-cardano-ballot/commit/39eb247fffbf666c35621b41df7d42c638268998))
* yaci-blockfrost and original_blockfrost separation as well as ChainSyncService with HealthIndicator. ([#85](https://github.com/cardano-foundation/cf-cardano-ballot/issues/85)) ([9821d06](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9821d0661107bf763b045015670e566a036e2381))

## [0.2.6](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.5...v0.2.6) (2023-08-23)


### Bug Fixes

* added missing workflow for follower app ([da4c50e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/da4c50eb8356db6c60e616e9329f0f97839c0916))
* vote commitment every 15 mins ([cc92ee4](https://github.com/cardano-foundation/cf-cardano-ballot/commit/cc92ee45282c65134e60a52ae03a0103098e7c9c))

## [0.2.5](https://github.com/cardano-foundation/cf-cardano-ballot/compare/v0.2.4...v0.2.5) (2023-08-23)


### Features

* added deployment chart, argocd bootstrap and main app ([#75](https://github.com/cardano-foundation/cf-cardano-ballot/issues/75)) ([9c4bcd2](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9c4bcd2d61f2776bf46c66589c8953d29ccb270d))
* added eventStartTime, eventEndTime, snapshotTime and whether event is finished or not in the reference data ([#47](https://github.com/cardano-foundation/cf-cardano-ballot/issues/47)) ([0dd8dcd](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0dd8dcd737fe91039275123f8f2ac1b6f6fb4c81))
* **cip-1694:** add leaderboard page ([e4699b0](https://github.com/cardano-foundation/cf-cardano-ballot/commit/e4699b06cc96adbf9c6358b52c65a034dc249f1c))
* **cip-1694:** add mobile version of the intro page ([3e47c62](https://github.com/cardano-foundation/cf-cardano-ballot/commit/3e47c62ef6f3c29ecb23a80fb44f2d829a2a8ff1))
* **cip-1694:** add mobile version of the leaderboard and vote pages ([ee896de](https://github.com/cardano-foundation/cf-cardano-ballot/commit/ee896de23653b5bccf9a50123fd65fd201b63fe9))
* **cip-1694:** add screens for the events that are not active ([34a7aa9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/34a7aa9545dce8f32972b86d06b9b36ccae74060))
* **cip-1694:** add tests placeholders ([589bed5](https://github.com/cardano-foundation/cf-cardano-ballot/commit/589bed5d741f85956d6dad413a08109eae34a10e))
* **cip-1694:** add verify vote screens ([9c12618](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9c1261819ec8a635577d3e236efd638b5117c199))
* **cip-1694:** add vote receipt ([dd3d496](https://github.com/cardano-foundation/cf-cardano-ballot/commit/dd3d4960f63a5c10ba25d4b03baac54c293680bb))
* **cip-1694:** integrate fetch receipt ([2e696e3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/2e696e329c177d01f2e6d9cd039e7c3cb9d2ba6f))
* **cip-1694:** remove verify vote modal ([b69fc5d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b69fc5db0f0775bd020da0f05110572ea52526e2))
* **cip-1694:** restyle intro page ([dd97d7a](https://github.com/cardano-foundation/cf-cardano-ballot/commit/dd97d7a1f607ccf2802c7def6b9482f5441f373f))
* **cip-1694:** restyle receipt drawer ([6702a60](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6702a60e98ee653e0a639a1d6df5953ae658e100))
* **cip-1694:** restyle voting page ([0f66c2c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0f66c2cd78040b88e21f674ade68d68bd237a914))
* epoch times should work fine now ([#50](https://github.com/cardano-foundation/cf-cardano-ballot/issues/50)) ([b462642](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b4626425e6d25280798c4be2f608df814672ace9))
* fake leaderboard ([9fc96a8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/9fc96a8fba4520df4d04f72ee15618bc8c872dbe))
* JWT login system gone for now... ([cafe7d9](https://github.com/cardano-foundation/cf-cardano-ballot/commit/cafe7d95007d9f323c5f4f139dd2a2457612e6fd))
* mocks for leaderboard and verification. ([#46](https://github.com/cardano-foundation/cf-cardano-ballot/issues/46)) ([1799000](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1799000aff29f0693d907a55133bb53991ab75af))
* refactoring away from Nullable where possible ([1c2a77c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1c2a77cfc4d7c12e5045b1591396fc545eb61e93))
* rollback handling support via YACI ([#41](https://github.com/cardano-foundation/cf-cardano-ballot/issues/41)) ([3690cf2](https://github.com/cardano-foundation/cf-cardano-ballot/commit/3690cf2f74a9cc28aeb69ab2ce55c0a49fc71c71))
* support for all apps to run via local Yaci-DevKit network plus minor bug fixes and documentation improvements. ([#54](https://github.com/cardano-foundation/cf-cardano-ballot/issues/54)) ([6a15b6f](https://github.com/cardano-foundation/cf-cardano-ballot/commit/6a15b6f9432bef216b41c4c1eb59c60aa83905f9))
* verification app ([#44](https://github.com/cardano-foundation/cf-cardano-ballot/issues/44)) ([952920e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/952920eb5ecb394c144632f6d44d20c9af94123a))
* voter's receipt protected now with CIP-93 envelope ([b329f56](https://github.com/cardano-foundation/cf-cardano-ballot/commit/b329f56fb5e9e5edf6160403371df75d351d5182))
* voting admin app - full metadata scan is now propertly authenticated ([7ffeba0](https://github.com/cardano-foundation/cf-cardano-ballot/commit/7ffeba007bfd3b6221fad98161fd0adf9a0fcecc))
* yaci beta3 ([#51](https://github.com/cardano-foundation/cf-cardano-ballot/issues/51)) ([a6a0ebf](https://github.com/cardano-foundation/cf-cardano-ballot/commit/a6a0ebfe82b5588c00af7ecfd0e1ed0f1d3b9ca2))


### Bug Fixes

* added restore points for Yaci DevKit network. ([0ecc6d8](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0ecc6d8aa18093a64e6c3a26431ebd3deacb8732))
* bug fix when there are no votes and leaderboard is called. ([af8a709](https://github.com/cardano-foundation/cf-cardano-ballot/commit/af8a709105d4ad0e65ac57ef7f4412bb26b02046))
* **cip-1694:** get vote power typing ([#40](https://github.com/cardano-foundation/cf-cardano-ballot/issues/40)) ([113285c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/113285c8ee775ab9d70dff115a2d645f7d094629))
* Flyway (manual) DDL instead of hibernate auto generated DDL. ([#60](https://github.com/cardano-foundation/cf-cardano-ballot/issues/60)) ([1534802](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1534802a29d890964905a0f433852246958de26d))
* h2 console issue fix ([1434d6c](https://github.com/cardano-foundation/cf-cardano-ballot/commit/1434d6c7481659ebcb2ae5121dc80b431295b4cc))
* introducting high level leaderboard results, which can be configured on event level. ([#55](https://github.com/cardano-foundation/cf-cardano-ballot/issues/55)) ([7a1139e](https://github.com/cardano-foundation/cf-cardano-ballot/commit/7a1139eb02a45509858ad3c02a3754577a5139df))
* optional should not be wrapped in a string ([0e596f3](https://github.com/cardano-foundation/cf-cardano-ballot/commit/0e596f309ceee59fee380f852378acbcd9abc019))
* small fixes and code improvements, upgrade to latest yaci-store ([#65](https://github.com/cardano-foundation/cf-cardano-ballot/issues/65)) ([318a372](https://github.com/cardano-foundation/cf-cardano-ballot/commit/318a3721b01f7d28f1afcaa7e59ea9e46814733c))
* snapshots released ([#67](https://github.com/cardano-foundation/cf-cardano-ballot/issues/67)) ([dbfe7db](https://github.com/cardano-foundation/cf-cardano-ballot/commit/dbfe7dba3dcf72ace190b8ccda41d8b5c3fe05d4))
* sys out removal ([c062ec7](https://github.com/cardano-foundation/cf-cardano-ballot/commit/c062ec7d7e36c50d4485cb3b41e7a7a671cfdb8b))
* various fixes, affecting production and local development. ([#58](https://github.com/cardano-foundation/cf-cardano-ballot/issues/58)) ([d6dab1d](https://github.com/cardano-foundation/cf-cardano-ballot/commit/d6dab1d0cfb43af9edc8fce98957a867e3a6f873))
* votingPower is a string ([8be4c29](https://github.com/cardano-foundation/cf-cardano-ballot/commit/8be4c29413fa8fe9f68fd86e11c29809c262fd96))
