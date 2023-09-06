# Changelog

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
