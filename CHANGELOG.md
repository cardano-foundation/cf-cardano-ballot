# Changelog

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
