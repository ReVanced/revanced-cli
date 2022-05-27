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
