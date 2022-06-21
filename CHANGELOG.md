## [1.6.2](https://github.com/revanced/revanced-cli/compare/v1.6.1...v1.6.2) (2022-06-21)


### Bug Fixes

* CLI not working ([29105ba](https://github.com/revanced/revanced-cli/commit/29105bab3dabd9d16af6b511caef9727f98afd1a))
* improper use of mount variable ([31853fe](https://github.com/revanced/revanced-cli/commit/31853fe5393af04805857b78a54434e1c51e4c8e))

## [1.6.1](https://github.com/revanced/revanced-cli/compare/v1.6.0...v1.6.1) (2022-06-21)


### Bug Fixes

* remove `-e` from `experimental` option ([3829136](https://github.com/revanced/revanced-cli/commit/3829136c49ddce1dc6b01eda04ce013540b213c2))

# [1.6.0](https://github.com/revanced/revanced-cli/compare/v1.5.1...v1.6.0) (2022-06-21)


### Features

* rename `debugging` option to `experimental` ([98bd6f3](https://github.com/revanced/revanced-cli/commit/98bd6f3f4b3eb34c445cbd5e3a3196f399f8bb3b))
* use `install` mode by default ([1a3db77](https://github.com/revanced/revanced-cli/commit/1a3db77c21b5795220fbac1ec36827fc521fface))

## [1.5.1](https://github.com/revanced/revanced-cli/compare/v1.5.0...v1.5.1) (2022-06-21)


### Bug Fixes

* update patcher version ([09b9027](https://github.com/revanced/revanced-cli/commit/09b9027e5e28f0483e74b711cf65a7876267a339)), closes [#45](https://github.com/revanced/revanced-cli/issues/45)

# [1.5.0](https://github.com/revanced/revanced-cli/compare/v1.4.5...v1.5.0) (2022-06-20)


### Features

* allow listing patches without other parameters ([#42](https://github.com/revanced/revanced-cli/issues/42)) ([b977d70](https://github.com/revanced/revanced-cli/commit/b977d7039f877be242823a4eef0fb8df6550dd05))

## [1.4.5](https://github.com/revanced/revanced-cli/compare/v1.4.4...v1.4.5) (2022-06-20)


### Bug Fixes

* update patcher version (fix apktool) ([496f821](https://github.com/revanced/revanced-cli/commit/496f82121879443609667a792cc1e16441ef5c2f))

## [1.4.4](https://github.com/revanced/revanced-cli/compare/v1.4.3...v1.4.4) (2022-06-18)


### Bug Fixes

* add execute permission to `./gradlew` file ([#36](https://github.com/revanced/revanced-cli/issues/36)) ([072d9e1](https://github.com/revanced/revanced-cli/commit/072d9e15d7e44b1080923f3afeb194eeb4fe4682))

## [1.4.3](https://github.com/revanced/revanced-cli/compare/v1.4.2...v1.4.3) (2022-06-18)


### Bug Fixes

* update patcher to 1.2.5 ([055c282](https://github.com/revanced/revanced-cli/commit/055c282dd3d147e3600bdfdf4fd6b4bd72cbf379))

## [1.4.2](https://github.com/revanced/revanced-cli/compare/v1.4.1...v1.4.2) (2022-06-16)


### Bug Fixes

* dummy publish task (1/2) [skip ci] ([afff4c8](https://github.com/revanced/revanced-cli/commit/afff4c8418bd33959440a3ac9e6c46816b3153ad))
* releases (2/2) ([227d8d9](https://github.com/revanced/revanced-cli/commit/227d8d94c89ec24051bd5bc20808049164d92492))

## [1.4.1](https://github.com/revanced/revanced-cli/compare/v1.4.0...v1.4.1) (2022-06-14)


### Bug Fixes

* move the keystore to the output directory ([6ceb449](https://github.com/revanced/revanced-cli/commit/6ceb449cf8539a92d89eeba8136fdc686319e2ef))

# [1.4.0](https://github.com/revanced/revanced-cli/compare/v1.3.3...v1.4.0) (2022-06-14)


### Features

* chcon on mount ([e1c7d10](https://github.com/revanced/revanced-cli/commit/e1c7d1082a6946d1082c8744a1d0118c1a2263ea))

## [1.3.3](https://github.com/revanced/revanced-cli/compare/v1.3.2...v1.3.3) (2022-06-13)


### Bug Fixes

* missing implementation ([48102c6](https://github.com/revanced/revanced-cli/commit/48102c66077c4ae17e3de1076e9da72de5f00366))

## [1.3.2](https://github.com/revanced/revanced-cli/compare/v1.3.1...v1.3.2) (2022-06-13)


### Bug Fixes

* only upload `-all.jar` asset ([ca8e1ba](https://github.com/revanced/revanced-cli/commit/ca8e1ba6af00e275c6981476f773b13b103799d1))

## [1.3.1](https://github.com/revanced/revanced-cli/compare/v1.3.0...v1.3.1) (2022-06-13)


### Bug Fixes

* check if `packageVersion` is compatible with any from `compatiblePackages` ([32589c8](https://github.com/revanced/revanced-cli/commit/32589c88e438e0a1375c256e9bb8a93f5a4d319b))

# [1.3.0](https://github.com/revanced/revanced-cli/compare/v1.2.0...v1.3.0) (2022-06-11)


### Bug Fixes

* `Main-Class` attribute pointing to wrong method ([6e82418](https://github.com/revanced/revanced-cli/commit/6e824189586bfa4f8aaac4a5f33aed8d59261115))
* `ZipAligner` not correctly calculating the file offset ([2975a47](https://github.com/revanced/revanced-cli/commit/2975a47d0f682a92da7b3ed455f5078298b0cbaa))
* broken control flow of `includeFilter` ([a0644c7](https://github.com/revanced/revanced-cli/commit/a0644c7045344e6a6c324392cb8f507a6d9dbfad))
* check for root even though when not needed ([0d7581a](https://github.com/revanced/revanced-cli/commit/0d7581ad750525e59bd6c019d987c640588ead62))
* overwrite output file ([2bfbbc2](https://github.com/revanced/revanced-cli/commit/2bfbbc2eb9057e66a362d37973a108baf44edf7a))
* resource patcher ([9da4f70](https://github.com/revanced/revanced-cli/commit/9da4f707ac62d11993021871ef39f4f1709ba89d))
* sign the aligned file instead of the input file ([22d2535](https://github.com/revanced/revanced-cli/commit/22d2535af8b3ea8fa58b6beb2938d240afa0a17d))


### Features

* support for `--install` ([d1ceab4](https://github.com/revanced/revanced-cli/commit/d1ceab45c89901f79d46c62f03186502021afb26))

# [1.2.0](https://github.com/revanced/revanced-cli/compare/v1.1.5...v1.2.0) (2022-06-05)


### Bug Fixes

* migrate to latest patcher api changes ([ace70e4](https://github.com/revanced/revanced-cli/commit/ace70e417fdf280c7630a5a89a773879fd240e96))


### Features

* add path for `cacheDirectory` and enable resource patching by default ([54c0a03](https://github.com/revanced/revanced-cli/commit/54c0a03d44c8d1b586bc487ee1ca71859d6f0b57))
* debugging option ([1b645c6](https://github.com/revanced/revanced-cli/commit/1b645c67db58eb4d49b5290fd247507c9b43a9c6))

# [1.2.0-dev.2](https://github.com/revanced/revanced-cli/compare/v1.2.0-dev.1...v1.2.0-dev.2) (2022-06-05)


### Features

* debugging option ([1b645c6](https://github.com/revanced/revanced-cli/commit/1b645c67db58eb4d49b5290fd247507c9b43a9c6))

# [1.2.0-dev.1](https://github.com/revanced/revanced-cli/compare/v1.1.6-dev.1...v1.2.0-dev.1) (2022-06-04)


### Features

* add path for `cacheDirectory` and enable resource patching by default ([54c0a03](https://github.com/revanced/revanced-cli/commit/54c0a03d44c8d1b586bc487ee1ca71859d6f0b57))

## [1.1.6-dev.1](https://github.com/revanced/revanced-cli/compare/v1.1.5...v1.1.6-dev.1) (2022-05-31)


### Bug Fixes

* migrate to latest patcher api changes ([ace70e4](https://github.com/revanced/revanced-cli/commit/ace70e417fdf280c7630a5a89a773879fd240e96))

## [1.1.5](https://github.com/revanced/revanced-cli/compare/v1.1.4...v1.1.5) (2022-05-27)


### Bug Fixes

* invalid code flow when adding patches ([206f202](https://github.com/revanced/revanced-cli/commit/206f2029d7498b6474c16a47cbe451c170fdd31f))

## [1.1.4](https://github.com/revanced/revanced-cli/compare/v1.1.3...v1.1.4) (2022-05-26)


### Bug Fixes

* migrate from `PatchLoader.load(...)` to `JarPatchBundle(...).loadPatches()` ([cabd32f](https://github.com/revanced/revanced-cli/commit/cabd32fda41d32616a61ae450c60e1ee7c35bc59))

## [1.1.3](https://github.com/revanced/revanced-cli/compare/v1.1.2...v1.1.3) (2022-05-25)


### Bug Fixes

* only accept directories when looking for files in resource patch ([c76da7e](https://github.com/revanced/revanced-cli/commit/c76da7e5ffa208860eea008dad358e4e3bb3d735))

## [1.1.2](https://github.com/revanced/revanced-cli/compare/v1.1.1...v1.1.2) (2022-05-22)


### Bug Fixes

* delete `outputFile` after deploying ([329f8a3](https://github.com/revanced/revanced-cli/commit/329f8a383fe52f4c2a66075d893c6599d3550bee))

## [1.1.1](https://github.com/revanced/revanced-cli/compare/v1.1.0...v1.1.1) (2022-05-22)


### Bug Fixes

* breaking changes by `revanced-patcher` dependency ([51d2504](https://github.com/revanced/revanced-cli/commit/51d250491f390695aedc64e7ee71a9dcf99d695c))
* wrong use of dependency to `revanced-patches` ([351de6c](https://github.com/revanced/revanced-cli/commit/351de6cb90aa0f2ec93e8b8f6c10d7d312082079))
* wrong use of variable substitution / typo ([81d53b5](https://github.com/revanced/revanced-cli/commit/81d53b5518454e479b7a8f2e9be934bee30702af)), closes [revanced/revanced-cli#12](https://github.com/revanced/revanced-cli/issues/12)

# [1.1.0-dev.3](https://github.com/revanced/revanced-cli/compare/v1.1.0-dev.2...v1.1.0-dev.3) (2022-05-15)


### Bug Fixes

* wrong use of variable substitution / typo ([81d53b5](https://github.com/revanced/revanced-cli/commit/81d53b5518454e479b7a8f2e9be934bee30702af)), closes [revanced/revanced-cli#12](https://github.com/revanced/revanced-cli/issues/12)

# [1.1.0-dev.2](https://github.com/revanced/revanced-cli/compare/v1.1.0-dev.1...v1.1.0-dev.2) (2022-05-07)


### Bug Fixes

* wrong use of dependency to `revanced-patches` ([351de6c](https://github.com/revanced/revanced-cli/commit/351de6cb90aa0f2ec93e8b8f6c10d7d312082079))

# [1.1.0-dev.1](https://github.com/revanced/revanced-cli/compare/v1.0.1...v1.1.0-dev.1) (2022-05-07)


### Bug Fixes

* ClassLoader not working with Java 9+ ([3a11e11](https://github.com/revanced/revanced-cli/commit/3a11e1135bd1e8958dd21247622d549440725ead))
* leftover TODOs ([5b1139c](https://github.com/revanced/revanced-cli/commit/5b1139ce43df1f5c2c848a8209a9e618857031ce))


### Features

* run `release.yml` workflow on branch `dev` ([9a64730](https://github.com/revanced/revanced-cli/commit/9a6473056b940c6df4860dd09c09d7ac61545f7d))

## [1.0.1](https://github.com/revanced/revanced-cli/compare/v1.0.0...v1.0.1) (2022-05-07)


### Bug Fixes

* broken script `CONTENT_UNMOUNT_SCRIPT` ([be53e64](https://github.com/revanced/revanced-cli/commit/be53e649a7a43de70ba2a7227c49b085001066a6))
* use latest version of patches dependency ([029f1ad](https://github.com/revanced/revanced-cli/commit/029f1ad72223e5be6664c2c8810ac35e5807d9a8))

# 1.0.0 (2022-05-07)


### Bug Fixes

* deploy to `adb` ([f9b987e](https://github.com/revanced/revanced-cli/commit/f9b987e858292332a4b99e4e4280647425b8c0b8))
* gradle build script ([6ffba3e](https://github.com/revanced/revanced-cli/commit/6ffba3ef0a089c01fd31b667a37a27e77186bbbd))
* gradle sync dependencies ([407efdc](https://github.com/revanced/revanced-cli/commit/407efdc8df1bd15710a9617462bfb123cfe739fe))
* make cli compatible with breaking changes of the patcher ([555b38f](https://github.com/revanced/revanced-cli/commit/555b38f386363661a1433d82b9825dc345855f65))
* make integrations optional ([bea8b82](https://github.com/revanced/revanced-cli/commit/bea8b829c701eee3c5b0bd6fe41c2f3f7df48d9b))
* resolve signatures before applying patches ([c9941fe](https://github.com/revanced/revanced-cli/commit/c9941fe182e11066c34c3d390352862bb0f95ca2))
* this tiny thing has caused me the worst headache ever in my life ([a37304e](https://github.com/revanced/revanced-cli/commit/a37304e032c9bb7d8b76f48c7eeaededb8a32a1e))
* uncomment merging integrations ([f2d9da4](https://github.com/revanced/revanced-cli/commit/f2d9da4dca890241f6fc52bc2049b5655bc2b8ae))
* unfinished todo message ([fb068ef](https://github.com/revanced/revanced-cli/commit/fb068ef7532fc236086205b41756c26f53489645))
* unmount script `CONTENT_UMOUNT_SCRIPT` ([3a2fa30](https://github.com/revanced/revanced-cli/commit/3a2fa30676338518ab4a320e16c4c1fab78e0615))
* update cli for new patcher version ([9fc2f96](https://github.com/revanced/revanced-cli/commit/9fc2f9602aa2f134106fa400daf388176957dd57))


### Features

* Add CLI ([6664f49](https://github.com/revanced/revanced-cli/commit/6664f49a11d655fe0723ad4846673b39b08fcadd))
* Add progress bar ([8d96ec8](https://github.com/revanced/revanced-cli/commit/8d96ec83cb11ac9323ef268884912961a2405435))
* add semantic-release ([78d7aa3](https://github.com/revanced/revanced-cli/commit/78d7aa361e4079b979fbf31d4fca2a7eec445618))
* Added root-only adb runner (tested on emulator) ([37ecc5e](https://github.com/revanced/revanced-cli/commit/37ecc5eaa6f9b6640061400270d192959e3d69b2))
* integrations merge ([919b34e](https://github.com/revanced/revanced-cli/commit/919b34e174e95ee9b6adef50e405b9bbe117803a))
* load patches dynamically & use kotlinx.cli ([4624384](https://github.com/revanced/revanced-cli/commit/4624384f28378efeb5cae54365169905a0ed4de7))
