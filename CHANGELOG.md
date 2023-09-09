## [3.1.1](https://github.com/ReVanced/revanced-cli/compare/v3.1.0...v3.1.1) (2023-09-09)


### Bug Fixes

* Create options if it does not exist when updating them ([ca809f0](https://github.com/ReVanced/revanced-cli/commit/ca809f0948379e3a825f24d7a49aba8b6b8767d1))

## [3.1.1-dev.1](https://github.com/ReVanced/revanced-cli/compare/v3.1.0...v3.1.1-dev.1) (2023-09-03)


### Bug Fixes

* Create options if it does not exist when updating them ([ca809f0](https://github.com/ReVanced/revanced-cli/commit/ca809f0948379e3a825f24d7a49aba8b6b8767d1))

# [3.1.0](https://github.com/ReVanced/revanced-cli/compare/v3.0.1...v3.1.0) (2023-08-31)


### Bug Fixes

* check for package compatibility at first ([9fe5a0b](https://github.com/ReVanced/revanced-cli/commit/9fe5a0b6d93304f630436ed0e954723d9a27b0f6))
* do not filter explicitly included patches ([a3d8f00](https://github.com/ReVanced/revanced-cli/commit/a3d8f004ec405f696d99d96c74ca41b573ecf425))
* format patches input ([bbb1a63](https://github.com/ReVanced/revanced-cli/commit/bbb1a63abd80dcbecdcf362158c0429cf3e6318f))


### Features

* Simplify command description ([3b3f7c7](https://github.com/ReVanced/revanced-cli/commit/3b3f7c7a7a7b2795e3d1fad776f6b457f2e68c7b))

# [3.1.0-dev.1](https://github.com/ReVanced/revanced-cli/compare/v3.0.2-dev.2...v3.1.0-dev.1) (2023-08-28)


### Bug Fixes

* format patches input ([bbb1a63](https://github.com/ReVanced/revanced-cli/commit/bbb1a63abd80dcbecdcf362158c0429cf3e6318f))


### Features

* Simplify command description ([3b3f7c7](https://github.com/ReVanced/revanced-cli/commit/3b3f7c7a7a7b2795e3d1fad776f6b457f2e68c7b))

## [3.0.2-dev.2](https://github.com/ReVanced/revanced-cli/compare/v3.0.2-dev.1...v3.0.2-dev.2) (2023-08-28)


### Bug Fixes

* check for package compatibility at first ([9fe5a0b](https://github.com/ReVanced/revanced-cli/commit/9fe5a0b6d93304f630436ed0e954723d9a27b0f6))

## [3.0.2-dev.1](https://github.com/ReVanced/revanced-cli/compare/v3.0.1...v3.0.2-dev.1) (2023-08-28)


### Bug Fixes

* do not filter explicitly included patches ([a3d8f00](https://github.com/ReVanced/revanced-cli/commit/a3d8f004ec405f696d99d96c74ca41b573ecf425))

## [3.0.1](https://github.com/ReVanced/revanced-cli/compare/v3.0.0...v3.0.1) (2023-08-28)

## [3.0.1-dev.1](https://github.com/ReVanced/revanced-cli/compare/v3.0.0...v3.0.1-dev.1) (2023-08-28)

# [3.0.0](https://github.com/ReVanced/revanced-cli/compare/v2.22.0...v3.0.0) (2023-08-26)


### Bug Fixes

* also delete temporary files when uninstalling ([52c3be2](https://github.com/ReVanced/revanced-cli/commit/52c3be23f2915dccaee7f9941413c8f81e14acc8))
* delete temporary files after root installation ([a3d8705](https://github.com/ReVanced/revanced-cli/commit/a3d8705e89732a0dd4f51de28c405b6af13c8633))
* do not delete output file ([0f3e090](https://github.com/ReVanced/revanced-cli/commit/0f3e090418771e951dfd15e5c193421f72cbe459))
* do not use absolute path from custom AAPT2 binary option ([a9c2a5f](https://github.com/ReVanced/revanced-cli/commit/a9c2a5f096627dbbf8ab1b8da26fb14529ce6bc3))
* filtration of patches malfunctioning ([2d5a7fd](https://github.com/ReVanced/revanced-cli/commit/2d5a7fdf1eb2e13f5013a790b03f09851b167fe0))
* fix running commands not running ([2c7fcaf](https://github.com/ReVanced/revanced-cli/commit/2c7fcaf4add65a12052afc5bef779dbc73debd69))
* only check once for patch options ([11c3a6c](https://github.com/ReVanced/revanced-cli/commit/11c3a6cfd4fe59ba5d703358634a1853e1cc22a5))
* print original instead of kebab cased names ([5eaad33](https://github.com/ReVanced/revanced-cli/commit/5eaad33dc1fbd24c36e1498f04e21d068e85f53e))
* print stack trace when a patch failed ([924c1f8](https://github.com/ReVanced/revanced-cli/commit/924c1f80ec0d17a3bdc07a0fb2015e44c49162e4))
* specify correct class containing entry-point ([1fcc591](https://github.com/ReVanced/revanced-cli/commit/1fcc591222ab67112f2b78174a8b94106846838c))
* use correct option name ([f8972ea](https://github.com/ReVanced/revanced-cli/commit/f8972eac3e5ee0a4a186c12cbe711925656d657b))


* refactor!: restructure code ([07da528](https://github.com/ReVanced/revanced-cli/commit/07da528ce2223582f84bf64d2fec69714c647ddc))


### Features

* add install command ([0350b7f](https://github.com/ReVanced/revanced-cli/commit/0350b7f1a276d9dc795b22442ba4f202855ea090))
* add options command ([9edbbf3](https://github.com/ReVanced/revanced-cli/commit/9edbbf31635603f89fc7bc5dcc6c023d4cdbb5a6))
* Check for missing integrations ([c93186f](https://github.com/ReVanced/revanced-cli/commit/c93186fb9700907e65f33442e88073783cc163de))
* Improve command line argument descriptions ([f9cf7d2](https://github.com/ReVanced/revanced-cli/commit/f9cf7d21b7f1c2f11234d604a1047b9d2b165f83))
* properly make use of logging facade ([41898d7](https://github.com/ReVanced/revanced-cli/commit/41898d7547690e3130372414515c5645e5dc2634))
* show full package name when listing patches ([#240](https://github.com/ReVanced/revanced-cli/issues/240)) ([7174364](https://github.com/ReVanced/revanced-cli/commit/7174364ef8ef5d6ce8351a8340f9c1a5b58eac3c))
* use better logging text ([b0e748d](https://github.com/ReVanced/revanced-cli/commit/b0e748daff527ee7f417b3069882e074896fc131))
* use friendly descriptions ([3dd875d](https://github.com/ReVanced/revanced-cli/commit/3dd875d14cca488ade6d21bbd4cce0d481692134))
* use separate command to list patches ([b74213f](https://github.com/ReVanced/revanced-cli/commit/b74213f66e0d04d3a0ae6197d069631388e06580))
* use separate command to patch ([32da961](https://github.com/ReVanced/revanced-cli/commit/32da961d57537e99b39fd92b625a1c73f8314bc6))
* use separate command to uninstall ([c0cc909](https://github.com/ReVanced/revanced-cli/commit/c0cc90962646cfffd5e2730ae556423271a7990b))
* use simpler log ([ba758f0](https://github.com/ReVanced/revanced-cli/commit/ba758f00f4ce18791439b7e72fe1ad2e7f11f8af))


### BREAKING CHANGES

* This introduces major changes to how ReVanced CLI is used from the command line.

# [3.0.0-dev.10](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.9...v3.0.0-dev.10) (2023-08-25)


### Bug Fixes

* filtration of patches malfunctioning ([2d5a7fd](https://github.com/ReVanced/revanced-cli/commit/2d5a7fdf1eb2e13f5013a790b03f09851b167fe0))

# [3.0.0-dev.9](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.8...v3.0.0-dev.9) (2023-08-25)


### Features

* Check for missing integrations ([c93186f](https://github.com/ReVanced/revanced-cli/commit/c93186fb9700907e65f33442e88073783cc163de))

# [3.0.0-dev.8](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.7...v3.0.0-dev.8) (2023-08-24)


### Bug Fixes

* do not delete output file ([0f3e090](https://github.com/ReVanced/revanced-cli/commit/0f3e090418771e951dfd15e5c193421f72cbe459))

# [3.0.0-dev.7](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.6...v3.0.0-dev.7) (2023-08-24)


### Bug Fixes

* print stack trace when a patch failed ([924c1f8](https://github.com/ReVanced/revanced-cli/commit/924c1f80ec0d17a3bdc07a0fb2015e44c49162e4))

# [3.0.0-dev.6](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.5...v3.0.0-dev.6) (2023-08-24)

# [3.0.0-dev.5](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.4...v3.0.0-dev.5) (2023-08-24)


### Bug Fixes

* also delete temporary files when uninstalling ([52c3be2](https://github.com/ReVanced/revanced-cli/commit/52c3be23f2915dccaee7f9941413c8f81e14acc8))
* delete temporary files after root installation ([a3d8705](https://github.com/ReVanced/revanced-cli/commit/a3d8705e89732a0dd4f51de28c405b6af13c8633))
* fix running commands not running ([2c7fcaf](https://github.com/ReVanced/revanced-cli/commit/2c7fcaf4add65a12052afc5bef779dbc73debd69))
* only check once for patch options ([11c3a6c](https://github.com/ReVanced/revanced-cli/commit/11c3a6cfd4fe59ba5d703358634a1853e1cc22a5))


### Features

* add install command ([0350b7f](https://github.com/ReVanced/revanced-cli/commit/0350b7f1a276d9dc795b22442ba4f202855ea090))
* use friendly descriptions ([3dd875d](https://github.com/ReVanced/revanced-cli/commit/3dd875d14cca488ade6d21bbd4cce0d481692134))

# [3.0.0-dev.4](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.3...v3.0.0-dev.4) (2023-08-24)


### Features

* properly make use of logging facade ([41898d7](https://github.com/ReVanced/revanced-cli/commit/41898d7547690e3130372414515c5645e5dc2634))

# [3.0.0-dev.3](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.2...v3.0.0-dev.3) (2023-08-23)

# [3.0.0-dev.2](https://github.com/ReVanced/revanced-cli/compare/v3.0.0-dev.1...v3.0.0-dev.2) (2023-08-23)


### Bug Fixes

* specify correct class containing entry-point ([1fcc591](https://github.com/ReVanced/revanced-cli/commit/1fcc591222ab67112f2b78174a8b94106846838c))

# [3.0.0-dev.1](https://github.com/ReVanced/revanced-cli/compare/v2.23.0-dev.5...v3.0.0-dev.1) (2023-08-23)


### Bug Fixes

* do not use absolute path from custom AAPT2 binary option ([a9c2a5f](https://github.com/ReVanced/revanced-cli/commit/a9c2a5f096627dbbf8ab1b8da26fb14529ce6bc3))
* use correct option name ([f8972ea](https://github.com/ReVanced/revanced-cli/commit/f8972eac3e5ee0a4a186c12cbe711925656d657b))


* refactor!: restructure code ([07da528](https://github.com/ReVanced/revanced-cli/commit/07da528ce2223582f84bf64d2fec69714c647ddc))


### Features

* add options command ([9edbbf3](https://github.com/ReVanced/revanced-cli/commit/9edbbf31635603f89fc7bc5dcc6c023d4cdbb5a6))
* use better logging text ([b0e748d](https://github.com/ReVanced/revanced-cli/commit/b0e748daff527ee7f417b3069882e074896fc131))
* use separate command to list patches ([b74213f](https://github.com/ReVanced/revanced-cli/commit/b74213f66e0d04d3a0ae6197d069631388e06580))
* use separate command to patch ([32da961](https://github.com/ReVanced/revanced-cli/commit/32da961d57537e99b39fd92b625a1c73f8314bc6))
* use separate command to uninstall ([c0cc909](https://github.com/ReVanced/revanced-cli/commit/c0cc90962646cfffd5e2730ae556423271a7990b))
* use simpler log ([ba758f0](https://github.com/ReVanced/revanced-cli/commit/ba758f00f4ce18791439b7e72fe1ad2e7f11f8af))


### BREAKING CHANGES

* This introduces major changes to how ReVanced CLI is used from the command line.

# [2.23.0-dev.5](https://github.com/ReVanced/revanced-cli/compare/v2.23.0-dev.4...v2.23.0-dev.5) (2023-08-14)

# [2.23.0-dev.4](https://github.com/ReVanced/revanced-cli/compare/v2.23.0-dev.3...v2.23.0-dev.4) (2023-08-13)


### Features

* show full package name when listing patches ([#240](https://github.com/ReVanced/revanced-cli/issues/240)) ([7174364](https://github.com/ReVanced/revanced-cli/commit/7174364ef8ef5d6ce8351a8340f9c1a5b58eac3c))

# [2.23.0-dev.3](https://github.com/ReVanced/revanced-cli/compare/v2.23.0-dev.2...v2.23.0-dev.3) (2023-08-03)

# [2.23.0-dev.2](https://github.com/ReVanced/revanced-cli/compare/v2.23.0-dev.1...v2.23.0-dev.2) (2023-08-03)

# [2.23.0-dev.1](https://github.com/ReVanced/revanced-cli/compare/v2.22.1-dev.1...v2.23.0-dev.1) (2023-07-30)


### Features

* Improve command line argument descriptions ([f9cf7d2](https://github.com/ReVanced/revanced-cli/commit/f9cf7d21b7f1c2f11234d604a1047b9d2b165f83))

## [2.22.1-dev.1](https://github.com/ReVanced/revanced-cli/compare/v2.22.0...v2.22.1-dev.1) (2023-07-24)


### Bug Fixes

* print original instead of kebab cased names ([5eaad33](https://github.com/ReVanced/revanced-cli/commit/5eaad33dc1fbd24c36e1498f04e21d068e85f53e))

# [2.22.0](https://github.com/revanced/revanced-cli/compare/v2.21.5...v2.22.0) (2023-07-11)


### Features

* use new patch naming convention ([f6c221d](https://github.com/revanced/revanced-cli/commit/f6c221d72dc43ebea00e5eba6bfa02751ae8ad77))

# [2.22.0-dev.1](https://github.com/revanced/revanced-cli/compare/v2.21.5...v2.22.0-dev.1) (2023-07-10)


### Features

* use new patch naming convention ([e4908c7](https://github.com/revanced/revanced-cli/commit/e4908c71051a535f8ce3406427cebbb0941464df))

## [2.21.5](https://github.com/revanced/revanced-cli/compare/v2.21.4...v2.21.5) (2023-07-01)

## [2.21.5-dev.2](https://github.com/revanced/revanced-cli/compare/v2.21.5-dev.1...v2.21.5-dev.2) (2023-07-01)

## [2.21.5-dev.1](https://github.com/revanced/revanced-cli/compare/v2.21.4...v2.21.5-dev.1) (2023-06-27)

## [2.21.4](https://github.com/revanced/revanced-cli/compare/v2.21.3...v2.21.4) (2023-06-21)


### Bug Fixes

* remove duplicate options entries. ([d0fc886](https://github.com/revanced/revanced-cli/commit/d0fc8864286adc2677f91a319a11a90272c1001d))

## [2.21.4-dev.1](https://github.com/revanced/revanced-cli/compare/v2.21.3...v2.21.4-dev.1) (2023-06-18)


### Bug Fixes

* remove duplicate options entries. ([d0fc886](https://github.com/revanced/revanced-cli/commit/d0fc8864286adc2677f91a319a11a90272c1001d))

## [2.21.3](https://github.com/revanced/revanced-cli/compare/v2.21.2...v2.21.3) (2023-06-12)

## [2.21.3-dev.1](https://github.com/revanced/revanced-cli/compare/v2.21.2...v2.21.3-dev.1) (2023-06-07)

## [2.21.2](https://github.com/revanced/revanced-cli/compare/v2.21.1...v2.21.2) (2023-05-24)

## [2.21.2-dev.2](https://github.com/revanced/revanced-cli/compare/v2.21.2-dev.1...v2.21.2-dev.2) (2023-05-15)

## [2.21.2-dev.1](https://github.com/revanced/revanced-cli/compare/v2.21.1...v2.21.2-dev.1) (2023-05-07)

## [2.21.1](https://github.com/revanced/revanced-cli/compare/v2.21.0...v2.21.1) (2023-05-06)

## [2.21.1-dev.1](https://github.com/revanced/revanced-cli/compare/v2.21.0...v2.21.1-dev.1) (2023-05-06)

# [2.21.0](https://github.com/revanced/revanced-cli/compare/v2.20.2...v2.21.0) (2023-05-04)


### Bug Fixes

* **tests:** set order of tests ([2ef48af](https://github.com/revanced/revanced-cli/commit/2ef48af1b339ab729a05d69cb0c8c1ee1e3ab486))
* use working JADB dependency version ([#222](https://github.com/revanced/revanced-cli/issues/222)) ([da2c918](https://github.com/revanced/revanced-cli/commit/da2c91874d5623402febfcc0677ada3d648565e1))


### Features

* add appreciation message for new contributors ([6962fc2](https://github.com/revanced/revanced-cli/commit/6962fc2f4c0f0c96e88a823be64f8ebd1312ee17))

# [2.21.0-dev.1](https://github.com/revanced/revanced-cli/compare/v2.20.2...v2.21.0-dev.1) (2023-05-04)


### Bug Fixes

* **tests:** set order of tests ([2ef48af](https://github.com/revanced/revanced-cli/commit/2ef48af1b339ab729a05d69cb0c8c1ee1e3ab486))
* use working JADB dependency version ([#222](https://github.com/revanced/revanced-cli/issues/222)) ([da2c918](https://github.com/revanced/revanced-cli/commit/da2c91874d5623402febfcc0677ada3d648565e1))


### Features

* add appreciation message for new contributors ([6962fc2](https://github.com/revanced/revanced-cli/commit/6962fc2f4c0f0c96e88a823be64f8ebd1312ee17))

## [2.20.2](https://github.com/revanced/revanced-cli/compare/v2.20.1...v2.20.2) (2023-04-30)


### Bug Fixes

* correct spelling mistake ([31fb316](https://github.com/revanced/revanced-cli/commit/31fb3166d922ae1f568f52e44cbe726dd1c891a4))

## [2.20.2-dev.1](https://github.com/revanced/revanced-cli/compare/v2.20.1...v2.20.2-dev.1) (2023-04-03)


### Bug Fixes

* correct spelling mistake ([31fb316](https://github.com/revanced/revanced-cli/commit/31fb3166d922ae1f568f52e44cbe726dd1c891a4))

## [2.20.1](https://github.com/revanced/revanced-cli/compare/v2.20.0...v2.20.1) (2023-03-14)


### Bug Fixes

* correctly word option descriptions ([ac3a8f6](https://github.com/revanced/revanced-cli/commit/ac3a8f66f77a7218974465eebbfc78a536b76d51))

## [2.20.1-dev.1](https://github.com/revanced/revanced-cli/compare/v2.20.0...v2.20.1-dev.1) (2023-03-05)


### Bug Fixes

* correctly word option descriptions ([ac3a8f6](https://github.com/revanced/revanced-cli/commit/ac3a8f66f77a7218974465eebbfc78a536b76d51))

## [2.20.1-dev.1](https://github.com/revanced/revanced-cli/compare/v2.20.0...v2.20.1-dev.1) (2023-03-03)


### Bug Fixes

* correctly word option descriptions ([ac3a8f6](https://github.com/revanced/revanced-cli/commit/ac3a8f66f77a7218974465eebbfc78a536b76d51))

## [2.20.1-dev.1](https://github.com/revanced/revanced-cli/compare/v2.20.0...v2.20.1-dev.1) (2023-03-02)


### Bug Fixes

* correctly word option descriptions ([ac3a8f6](https://github.com/revanced/revanced-cli/commit/ac3a8f66f77a7218974465eebbfc78a536b76d51))

# [2.20.0](https://github.com/revanced/revanced-cli/compare/v2.19.0...v2.20.0) (2023-01-18)


### Bug Fixes

* bump patcher dependency version ([51c04b7](https://github.com/revanced/revanced-cli/commit/51c04b7b162ad2876bbeb248b7ccddd105b5076d))
* do not list compatible packages if patches do not define them ([31e4a41](https://github.com/revanced/revanced-cli/commit/31e4a41dd20f5fa62f840cd8e3b92fe0814eda87))


### Features

* connect to first device if given device was not found ([6485e47](https://github.com/revanced/revanced-cli/commit/6485e477a10bb89dfb2e40f3596d72b20bf23cc8))
* remove option `--with-descriptions` ([07a423b](https://github.com/revanced/revanced-cli/commit/07a423b19ec72e9f020aeb0222f4ced571036dbe))

# [2.20.0](https://github.com/revanced/revanced-cli/compare/v2.19.0...v2.20.0) (2023-01-17)


### Bug Fixes

* bump patcher dependency version ([51c04b7](https://github.com/revanced/revanced-cli/commit/51c04b7b162ad2876bbeb248b7ccddd105b5076d))
* do not list compatible packages if patches do not define them ([31e4a41](https://github.com/revanced/revanced-cli/commit/31e4a41dd20f5fa62f840cd8e3b92fe0814eda87))


### Features

* connect to first device if given device was not found ([6485e47](https://github.com/revanced/revanced-cli/commit/6485e477a10bb89dfb2e40f3596d72b20bf23cc8))
* remove option `--with-descriptions` ([07a423b](https://github.com/revanced/revanced-cli/commit/07a423b19ec72e9f020aeb0222f4ced571036dbe))

# [2.20.0-dev.3](https://github.com/revanced/revanced-cli/compare/v2.20.0-dev.2...v2.20.0-dev.3) (2023-01-15)


### Bug Fixes

* bump patcher dependency version ([51c04b7](https://github.com/revanced/revanced-cli/commit/51c04b7b162ad2876bbeb248b7ccddd105b5076d))

# [2.20.0-dev.2](https://github.com/revanced/revanced-cli/compare/v2.20.0-dev.1...v2.20.0-dev.2) (2023-01-15)


### Features

* connect to first device if given device was not found ([6485e47](https://github.com/revanced/revanced-cli/commit/6485e477a10bb89dfb2e40f3596d72b20bf23cc8))

# [2.20.0](https://github.com/revanced/revanced-cli/compare/v2.19.0...v2.20.0) (2023-01-02)


### Bug Fixes

* do not list compatible packages if patches do not define them ([31e4a41](https://github.com/revanced/revanced-cli/commit/31e4a41dd20f5fa62f840cd8e3b92fe0814eda87))


### Features

* remove option `--with-descriptions` ([07a423b](https://github.com/revanced/revanced-cli/commit/07a423b19ec72e9f020aeb0222f4ced571036dbe))

# [2.20.0](https://github.com/revanced/revanced-cli/compare/v2.19.0...v2.20.0) (2023-01-01)


### Bug Fixes

* do not list compatible packages if patches do not define them ([31e4a41](https://github.com/revanced/revanced-cli/commit/31e4a41dd20f5fa62f840cd8e3b92fe0814eda87))


### Features

* remove option `--with-descriptions` ([07a423b](https://github.com/revanced/revanced-cli/commit/07a423b19ec72e9f020aeb0222f4ced571036dbe))

# [2.20.0-dev.1](https://github.com/revanced/revanced-cli/compare/v2.19.0...v2.20.0-dev.1) (2023-01-01)


### Bug Fixes

* do not list compatible packages if patches do not define them ([31e4a41](https://github.com/revanced/revanced-cli/commit/31e4a41dd20f5fa62f840cd8e3b92fe0814eda87))


### Features

* remove option `--with-descriptions` ([07a423b](https://github.com/revanced/revanced-cli/commit/07a423b19ec72e9f020aeb0222f4ced571036dbe))

# [2.19.0](https://github.com/revanced/revanced-cli/compare/v2.18.2...v2.19.0) (2022-12-31)


### Features

* improve description of options ([#185](https://github.com/revanced/revanced-cli/issues/185)) ([b69e784](https://github.com/revanced/revanced-cli/commit/b69e784785f7f262f83b35c4f241c90036169fc7))

# [2.19.0-dev.1](https://github.com/revanced/revanced-cli/compare/v2.18.2...v2.19.0-dev.1) (2022-12-31)


### Features

* improve description of options ([#185](https://github.com/revanced/revanced-cli/issues/185)) ([b69e784](https://github.com/revanced/revanced-cli/commit/b69e784785f7f262f83b35c4f241c90036169fc7))

## [2.18.2](https://github.com/revanced/revanced-cli/compare/v2.18.1...v2.18.2) (2022-12-16)


### Bug Fixes

* exclude patcher dependency from minimizing ([d5794b9](https://github.com/revanced/revanced-cli/commit/d5794b94ca19c9287190a3b863c97a089893cc07))

## [2.18.2-dev.1](https://github.com/revanced/revanced-cli/compare/v2.18.1...v2.18.2-dev.1) (2022-12-16)


### Bug Fixes

* exclude patcher dependency from minimizing ([d5794b9](https://github.com/revanced/revanced-cli/commit/d5794b94ca19c9287190a3b863c97a089893cc07))

## [2.18.1](https://github.com/revanced/revanced-cli/compare/v2.18.0...v2.18.1) (2022-12-15)


### Bug Fixes

* don't log when package is incompatible and `exclusive` option is used ([ad81a1b](https://github.com/revanced/revanced-cli/commit/ad81a1b656586226f8b7b8d1123c52b0f3f2e6f7))

## [2.18.1-dev.1](https://github.com/revanced/revanced-cli/compare/v2.18.0...v2.18.1-dev.1) (2022-12-15)


### Bug Fixes

* don't log when package is incompatible and `exclusive` option is used ([ad81a1b](https://github.com/revanced/revanced-cli/commit/ad81a1b656586226f8b7b8d1123c52b0f3f2e6f7))

# [2.18.0](https://github.com/revanced/revanced-cli/compare/v2.17.0...v2.18.0) (2022-12-15)


### Bug Fixes

* start with uppercase in log message ([ae91b0d](https://github.com/revanced/revanced-cli/commit/ae91b0d597b107a152e1b630b8c16a795a7ca3b3))
* use correct prefix in log message ([1a67cd8](https://github.com/revanced/revanced-cli/commit/1a67cd81da5ed7f259b6dbaeb66a7eef4a113034))


### Features

* simplify log message ([74d73ca](https://github.com/revanced/revanced-cli/commit/74d73ca3a7ba2f5da872fe9a241629e1c143cd4e))

# [2.18.0](https://github.com/revanced/revanced-cli/compare/v2.17.0...v2.18.0) (2022-12-15)


### Bug Fixes

* start with uppercase in log message ([ae91b0d](https://github.com/revanced/revanced-cli/commit/ae91b0d597b107a152e1b630b8c16a795a7ca3b3))
* use correct prefix in log message ([1a67cd8](https://github.com/revanced/revanced-cli/commit/1a67cd81da5ed7f259b6dbaeb66a7eef4a113034))


### Features

* simplify log message ([74d73ca](https://github.com/revanced/revanced-cli/commit/74d73ca3a7ba2f5da872fe9a241629e1c143cd4e))

# [2.18.0-dev.1](https://github.com/revanced/revanced-cli/compare/v2.17.1-dev.2...v2.18.0-dev.1) (2022-12-15)


### Bug Fixes

* start with uppercase in log message ([ae91b0d](https://github.com/revanced/revanced-cli/commit/ae91b0d597b107a152e1b630b8c16a795a7ca3b3))
* use correct prefix in log message ([1a67cd8](https://github.com/revanced/revanced-cli/commit/1a67cd81da5ed7f259b6dbaeb66a7eef4a113034))


### Features

* simplify log message ([74d73ca](https://github.com/revanced/revanced-cli/commit/74d73ca3a7ba2f5da872fe9a241629e1c143cd4e))

## [2.17.1-dev.2](https://github.com/revanced/revanced-cli/compare/v2.17.1-dev.1...v2.17.1-dev.2) (2022-12-15)

## [2.17.1-dev.1](https://github.com/revanced/revanced-cli/compare/v2.17.0...v2.17.1-dev.1) (2022-12-15)

# [2.17.0](https://github.com/revanced/revanced-cli/compare/v2.16.1...v2.17.0) (2022-12-14)


### Bug Fixes

* invalid header when writing a `ZipFile` ([#169](https://github.com/revanced/revanced-cli/issues/169)) ([6e703eb](https://github.com/revanced/revanced-cli/commit/6e703eb8e8d7da0e52266c4965f37bc8aafb409c))


### Features

* improve missing compatibility annotation tracing log ([2c7eb72](https://github.com/revanced/revanced-cli/commit/2c7eb7274c713dfbcb53c5f3b6a9205c751914fa))
* trace logs when compatibility annotation is missing ([#166](https://github.com/revanced/revanced-cli/issues/166)) ([c590bf5](https://github.com/revanced/revanced-cli/commit/c590bf559c4d2d2667c2af0c0da23d4706fcd4b7))

# [2.17.0-dev.3](https://github.com/revanced/revanced-cli/compare/v2.17.0-dev.2...v2.17.0-dev.3) (2022-12-14)


### Features

* improve missing compatibility annotation tracing log ([2c7eb72](https://github.com/revanced/revanced-cli/commit/2c7eb7274c713dfbcb53c5f3b6a9205c751914fa))

# [2.17.0-dev.2](https://github.com/revanced/revanced-cli/compare/v2.17.0-dev.1...v2.17.0-dev.2) (2022-12-14)


### Bug Fixes

* invalid header when writing a `ZipFile` ([#169](https://github.com/revanced/revanced-cli/issues/169)) ([6e703eb](https://github.com/revanced/revanced-cli/commit/6e703eb8e8d7da0e52266c4965f37bc8aafb409c))

# [2.17.0-dev.1](https://github.com/revanced/revanced-cli/compare/v2.16.1...v2.17.0-dev.1) (2022-12-11)


### Features

* trace logs when compatibility annotation is missing ([#166](https://github.com/revanced/revanced-cli/issues/166)) ([c590bf5](https://github.com/revanced/revanced-cli/commit/c590bf559c4d2d2667c2af0c0da23d4706fcd4b7))

## [2.16.1](https://github.com/revanced/revanced-cli/compare/v2.16.0...v2.16.1) (2022-11-22)

# [2.16.0](https://github.com/revanced/revanced-cli/compare/v2.15.1...v2.16.0) (2022-11-20)


### Features

* do not warn on incompatible packages ([39e377b](https://github.com/revanced/revanced-cli/commit/39e377bc485e2892422e9712d30e6ff665856ac1))

## [2.15.1](https://github.com/revanced/revanced-cli/compare/v2.15.0...v2.15.1) (2022-11-18)

# [2.15.0](https://github.com/revanced/revanced-cli/compare/v2.14.0...v2.15.0) (2022-10-31)


### Bug Fixes

* **gitignore:** ignore `options.toml` ([#158](https://github.com/revanced/revanced-cli/issues/158)) ([7be9af0](https://github.com/revanced/revanced-cli/commit/7be9af0942de2a834b9e57403d46263b65f1a422))


### Features

* use `am` instead of `monkey` to launch the app ([#159](https://github.com/revanced/revanced-cli/issues/159)) ([6a35cf7](https://github.com/revanced/revanced-cli/commit/6a35cf7ea46a4474120626ce03d28490cc96bf07))

# [2.14.0](https://github.com/revanced/revanced-cli/compare/v2.13.0...v2.14.0) (2022-10-05)


### Bug Fixes

* escape quotation mark in string ([6e21d81](https://github.com/revanced/revanced-cli/commit/6e21d81964e8160e06ffda7051dd484e4aaaa432))


### Features

* handle unmounting deleted files ([#148](https://github.com/revanced/revanced-cli/issues/148)) ([3a733e5](https://github.com/revanced/revanced-cli/commit/3a733e513717799ca0e32327e5b8be043680c556))
* unmount all occurrences in `/proc/mounts` ([#131](https://github.com/revanced/revanced-cli/issues/131)) ([4f4e1f9](https://github.com/revanced/revanced-cli/commit/4f4e1f9834bf28d9be2efd4fd7bae19951b85258))

# [2.13.0](https://github.com/revanced/revanced-cli/compare/v2.12.0...v2.13.0) (2022-10-01)


### Features

* check, if input file exists ([b6dff6d](https://github.com/revanced/revanced-cli/commit/b6dff6d832de4a513a6d86b0a59b2458eddd23c2))

# [2.12.0](https://github.com/revanced/revanced-cli/compare/v2.11.2...v2.12.0) (2022-09-26)


### Features

* remove unused option `-r` ([467d838](https://github.com/revanced/revanced-cli/commit/467d8387e646c88d24a30406a5b2e84065ef4d54))

## [2.11.2](https://github.com/revanced/revanced-cli/compare/v2.11.1...v2.11.2) (2022-09-23)

## [2.11.1](https://github.com/revanced/revanced-cli/compare/v2.11.0...v2.11.1) (2022-09-21)

# [2.11.0](https://github.com/revanced/revanced-cli/compare/v2.10.2...v2.11.0) (2022-09-20)


### Features

* section `acknowledgements` for issue templates ([0e3ecc3](https://github.com/revanced/revanced-cli/commit/0e3ecc3a51540b71072ae0be0eb94d115a5b1f92))

## [2.10.2](https://github.com/revanced/revanced-cli/compare/v2.10.1...v2.10.2) (2022-09-18)

## [2.10.1](https://github.com/revanced/revanced-cli/compare/v2.10.0...v2.10.1) (2022-09-09)

# [2.10.0](https://github.com/revanced/revanced-cli/compare/v2.9.10...v2.10.0) (2022-09-08)


### Features

* Patch Options CLI implementation ([#132](https://github.com/revanced/revanced-cli/issues/132)) ([3f5345a](https://github.com/revanced/revanced-cli/commit/3f5345af6e45bfb6c91d52fc089ab18d81fdc998))

## [2.9.10](https://github.com/revanced/revanced-cli/compare/v2.9.9...v2.9.10) (2022-09-08)


### Bug Fixes

* don't print same patch multiple times ([f4b0469](https://github.com/revanced/revanced-cli/commit/f4b04698d8c1717824e86f91da5e01c5021612da))

## [2.9.9](https://github.com/revanced/revanced-cli/compare/v2.9.8...v2.9.9) (2022-09-08)

## [2.9.8](https://github.com/revanced/revanced-cli/compare/v2.9.7...v2.9.8) (2022-09-08)


### Bug Fixes

* broken deprecation message ([e3e74ac](https://github.com/revanced/revanced-cli/commit/e3e74ac0e9a844f9d717a499bca09e575dd90435))

## [2.9.7](https://github.com/revanced/revanced-cli/compare/v2.9.6...v2.9.7) (2022-09-08)

## [2.9.6](https://github.com/revanced/revanced-cli/compare/v2.9.5...v2.9.6) (2022-09-07)

## [2.9.5](https://github.com/revanced/revanced-cli/compare/v2.9.4...v2.9.5) (2022-09-01)


### Bug Fixes

* mount bind revanced.apk from magisk's mirror ([372470c](https://github.com/revanced/revanced-cli/commit/372470c77b82e8601ca523e87a2cfd44f79d0e31))

## [2.9.4](https://github.com/revanced/revanced-cli/compare/v2.9.3...v2.9.4) (2022-08-31)

## [2.9.3](https://github.com/revanced/revanced-cli/compare/v2.9.2...v2.9.3) (2022-08-14)

## [2.9.2](https://github.com/revanced/revanced-cli/compare/v2.9.1...v2.9.2) (2022-08-07)

## [2.9.1](https://github.com/revanced/revanced-cli/compare/v2.9.0...v2.9.1) (2022-08-04)


### Reverts

* feat: remove extra zipalign step ([c3d8fec](https://github.com/revanced/revanced-cli/commit/c3d8fecad0ed9d583b9f1f79bc271e0535d87be2))

# [2.9.0](https://github.com/revanced/revanced-cli/compare/v2.8.3...v2.9.0) (2022-08-03)


### Features

* remove extra zipalign step ([#106](https://github.com/revanced/revanced-cli/issues/106)) ([c8e793e](https://github.com/revanced/revanced-cli/commit/c8e793efab8eed39b2cb564bee80ef6e0b2a7d03))

## [2.8.3](https://github.com/revanced/revanced-cli/compare/v2.8.2...v2.8.3) (2022-08-03)

## [2.8.2](https://github.com/revanced/revanced-cli/compare/v2.8.1...v2.8.2) (2022-08-02)

## [2.8.1](https://github.com/revanced/revanced-cli/compare/v2.8.0...v2.8.1) (2022-08-02)


### Bug Fixes

* remove requirement for solution [skip ci] ([#108](https://github.com/revanced/revanced-cli/issues/108)) ([0ce680a](https://github.com/revanced/revanced-cli/commit/0ce680a6f1de139434edd80876a168ff695c2d79))

# [2.8.0](https://github.com/revanced/revanced-cli/compare/v2.7.1...v2.8.0) (2022-07-31)


### Features

* `frameworkFolderLocation` patcher option ([bc17298](https://github.com/revanced/revanced-cli/commit/bc17298a807ce035b8baa1f7c30e1392ca4ee43b))

## [2.7.1](https://github.com/revanced/revanced-cli/compare/v2.7.0...v2.7.1) (2022-07-21)


### Bug Fixes

* align every file ([96ec6a0](https://github.com/revanced/revanced-cli/commit/96ec6a0384e4c710c36b99ccf88bb14c5addc7af))

# [2.7.0](https://github.com/revanced/revanced-cli/compare/v2.6.0...v2.7.0) (2022-07-21)


### Features

* `--custom-aapt2-binary` option ([#104](https://github.com/revanced/revanced-cli/issues/104)) ([d8dbffd](https://github.com/revanced/revanced-cli/commit/d8dbffd7a7ebc583476d368d2ae78853cbb40382))

# [2.6.0](https://github.com/revanced/revanced-cli/compare/v2.5.3...v2.6.0) (2022-07-18)


### Features

* more efficient zipalign ([a942a57](https://github.com/revanced/revanced-cli/commit/a942a57364777d6ab6fcb11e6171cc4b496c8540))

## [2.5.3](https://github.com/revanced/revanced-cli/compare/v2.5.2...v2.5.3) (2022-07-11)


### Bug Fixes

* Log not showing in CLI  ([#80](https://github.com/revanced/revanced-cli/issues/80)) ([d9c5a17](https://github.com/revanced/revanced-cli/commit/d9c5a179c529c386f66df4fa63cd321d396836b5)), closes [#79](https://github.com/revanced/revanced-cli/issues/79)

## [2.5.2](https://github.com/revanced/revanced-cli/compare/v2.5.1...v2.5.2) (2022-07-10)


### Bug Fixes

* `defaultExclude` unused ([2015c2a](https://github.com/revanced/revanced-cli/commit/2015c2a1dcc0290af237e2eb1ab4aaf5fc84d382))

## [2.5.1](https://github.com/revanced/revanced-cli/compare/v2.5.0...v2.5.1) (2022-07-10)


### Bug Fixes

* Make clear what the --exclusive command actually does ([a26b0ea](https://github.com/revanced/revanced-cli/commit/a26b0ea64dcd0757b5b49c09137959751874d955))

# [2.5.0](https://github.com/revanced/revanced-cli/compare/v2.4.0...v2.5.0) (2022-07-10)


### Bug Fixes

* null exception when resource patching is disabled ([#85](https://github.com/revanced/revanced-cli/issues/85)) ([125fa06](https://github.com/revanced/revanced-cli/commit/125fa06ca6bac66f790e4aba66887464189b3a63))
* remove `excludePatches` check ([eb83cab](https://github.com/revanced/revanced-cli/commit/eb83cabfff6fbc28dd892f15b8e28278e284caa9))


### Features

* `--exclusive` switch ([#78](https://github.com/revanced/revanced-cli/issues/78)) ([8e91c12](https://github.com/revanced/revanced-cli/commit/8e91c12c5e3864c369005ef9fe7d9db668e86701))
* `--uninstall` switch ([#84](https://github.com/revanced/revanced-cli/issues/84)) ([131100e](https://github.com/revanced/revanced-cli/commit/131100ef0043924a9c11eb9886b4f6b0373690d9))

# [2.4.0](https://github.com/revanced/revanced-cli/compare/v2.3.3...v2.4.0) (2022-07-10)


### Bug Fixes

* wrong label in additional items [skip ci] ([cd3ded1](https://github.com/revanced/revanced-cli/commit/cd3ded1fbdb0c8eb7485912d5cbd6a2dd7455658))


### Features

* better output for excluded patches ([#77](https://github.com/revanced/revanced-cli/issues/77)) ([ac7c7a9](https://github.com/revanced/revanced-cli/commit/ac7c7a9a1a5c08322e3b206780d4f31104d8b570))
* issue templates [skip ci] ([bac8c67](https://github.com/revanced/revanced-cli/commit/bac8c67d6f7bc10a38bb98a2f6e3f5cf6fa2e3e1))

## [2.3.3](https://github.com/revanced/revanced-cli/compare/v2.3.2...v2.3.3) (2022-07-09)

## [2.3.2](https://github.com/revanced/revanced-cli/compare/v2.3.1...v2.3.2) (2022-07-05)


### Bug Fixes

* fix noSuchMethodError ([00fec25](https://github.com/revanced/revanced-cli/commit/00fec2508a3421b7b5a246254e0cb08850eab6ea))

## [2.3.1](https://github.com/revanced/revanced-cli/compare/v2.3.0...v2.3.1) (2022-07-04)

# [2.3.0](https://github.com/revanced/revanced-cli/compare/v2.2.0...v2.3.0) (2022-07-03)


### Features

* separate logger to stdout & stderr ([#63](https://github.com/revanced/revanced-cli/issues/63)) ([0ddc2b5](https://github.com/revanced/revanced-cli/commit/0ddc2b54b739dae3e8ccc983bab73fc84e72be0a))

# [2.2.0](https://github.com/revanced/revanced-cli/compare/v2.1.0...v2.2.0) (2022-07-03)


### Features

* separate options for `--list` ([#60](https://github.com/revanced/revanced-cli/issues/60)) ([52b3161](https://github.com/revanced/revanced-cli/commit/52b316150de397ebdee979caf51d4cb20961cf70))

# [2.1.0](https://github.com/revanced/revanced-cli/compare/v2.0.5...v2.1.0) (2022-07-03)


### Features

* `--include` option ([#76](https://github.com/revanced/revanced-cli/issues/76)) ([57a1e7c](https://github.com/revanced/revanced-cli/commit/57a1e7c27fb0c4292e08332b88ccd57d69fa02c6))

## [2.0.5](https://github.com/revanced/revanced-cli/compare/v2.0.4...v2.0.5) (2022-06-29)

## [2.0.4](https://github.com/revanced/revanced-cli/compare/v2.0.3...v2.0.4) (2022-06-28)

## [2.0.3](https://github.com/revanced/revanced-cli/compare/v2.0.2...v2.0.3) (2022-06-27)


### Bug Fixes

* wrong keystore output path ([20fa179](https://github.com/revanced/revanced-cli/commit/20fa17957e3e454b9755bc7b9b473b6c578cc593))

## [2.0.2](https://github.com/revanced/revanced-cli/compare/v2.0.1...v2.0.2) (2022-06-27)


### Bug Fixes

* wrong separator when using `ZipFileSystemUtils` ([20e15de](https://github.com/revanced/revanced-cli/commit/20e15defc2b90aa5e79bad41c097bd0db8d5e12a))

## [2.0.1](https://github.com/revanced/revanced-cli/compare/v2.0.0...v2.0.1) (2022-06-26)

# [2.0.0](https://github.com/revanced/revanced-cli/compare/v1.11.1...v2.0.0) (2022-06-26)


### Code Refactoring

* migrate from `Signature` to `Fingerprint` ([88852a4](https://github.com/revanced/revanced-cli/commit/88852a45ac90ad9419c18f0cb3395745e62eadbf))


### BREAKING CHANGES

* Not backwards compatible, since a lot of classes where renamed.

## [1.11.1](https://github.com/revanced/revanced-cli/compare/v1.11.0...v1.11.1) (2022-06-25)


### Bug Fixes

* update patcher version ([499ce0a](https://github.com/revanced/revanced-cli/commit/499ce0a6fb1993ad48ffdd9c9e8307e8d0c179c6))

# [1.11.0](https://github.com/revanced/revanced-cli/compare/v1.10.2...v1.11.0) (2022-06-23)


### Features

* improve logging ([df85fa3](https://github.com/revanced/revanced-cli/commit/df85fa37ef067681a027e6fe9212c8a065d4981b))

## [1.10.2](https://github.com/revanced/revanced-cli/compare/v1.10.1...v1.10.2) (2022-06-22)


### Bug Fixes

* keystore file not found exception ([#57](https://github.com/revanced/revanced-cli/issues/57)) ([5b8537e](https://github.com/revanced/revanced-cli/commit/5b8537e6b7922f1b44058c3a4fc2f56cb4e15e89))

## [1.10.1](https://github.com/revanced/revanced-cli/compare/v1.10.0...v1.10.1) (2022-06-22)


### Bug Fixes

* show actual version in CLI ([1dcdbc9](https://github.com/revanced/revanced-cli/commit/1dcdbc9fe9e3ad2fe232ad3baa76d186817532a4))

# [1.10.0](https://github.com/revanced/revanced-cli/compare/v1.9.3...v1.10.0) (2022-06-22)


### Bug Fixes

* add callback for addFiles ([87ffaa4](https://github.com/revanced/revanced-cli/commit/87ffaa4bdb25cb85c6ba7195f5d3b13b09a5f912))


### Features

* add logging back ([4a23cb6](https://github.com/revanced/revanced-cli/commit/4a23cb69bc385158d47b6ea8d3da54b0566a242c))

## [1.9.3](https://github.com/revanced/revanced-cli/compare/v1.9.2...v1.9.3) (2022-06-22)


### Bug Fixes

* use absolute file path for key store ([d335846](https://github.com/revanced/revanced-cli/commit/d335846202b991e130882e9ce0ab268deb2e27ab))

## [1.9.2](https://github.com/revanced/revanced-cli/compare/v1.9.1...v1.9.2) (2022-06-22)


### Bug Fixes

* update patcher version ([0df936e](https://github.com/revanced/revanced-cli/commit/0df936e99b01358a88a628af63eebb5ac92cae76))

## [1.9.1](https://github.com/revanced/revanced-cli/compare/v1.9.0...v1.9.1) (2022-06-22)


### Bug Fixes

* add back in: option to specify keystore file path ([c94471f](https://github.com/revanced/revanced-cli/commit/c94471f4643e44b2b472ff0d826db0d2743bdc86))
* remove logger from Signer.kt ([51e091c](https://github.com/revanced/revanced-cli/commit/51e091ce4021418508044029aa5af6aa7d5162a3))


### Reverts

* "feat: use of `java.util.logging.Logger`" ([2c8a106](https://github.com/revanced/revanced-cli/commit/2c8a10615192635202ddc137fc02f175c5914d8f))

# [1.9.0](https://github.com/revanced/revanced-cli/compare/v1.8.0...v1.9.0) (2022-06-22)


### Features

* migrate logger to `slf4j` ([6c4c192](https://github.com/revanced/revanced-cli/commit/6c4c1924ee9ae75af3449749a6a82b7ae5572129))

# [1.8.0](https://github.com/revanced/revanced-cli/compare/v1.7.1...v1.8.0) (2022-06-22)


### Features

* add option to specify keystore file path ([9331594](https://github.com/revanced/revanced-cli/commit/9331594706404df871d170110da753cde5058d02))
* use of `java.util.logging.Logger` ([07f6bdf](https://github.com/revanced/revanced-cli/commit/07f6bdf33069da4e11fc40090feb726433de703e))

## [1.7.1](https://github.com/revanced/revanced-cli/compare/v1.7.0...v1.7.1) (2022-06-22)


### Bug Fixes

* migrate to changes of patcher ([b30c737](https://github.com/revanced/revanced-cli/commit/b30c7375a7ea61184be5dca19062ee74d1f97692))
* wrong variable inverted ([f694542](https://github.com/revanced/revanced-cli/commit/f694542d64ccb06bfa4d042f26b6b7192d1e912e))

# [1.7.0](https://github.com/revanced/revanced-cli/compare/v1.6.3...v1.7.0) (2022-06-21)


### Features

* show description when listing patches ([af32572](https://github.com/revanced/revanced-cli/commit/af32572f29d0f0a45ee5c3e01ba4cf1f91fe2f10))

## [1.6.3](https://github.com/revanced/revanced-cli/compare/v1.6.2...v1.6.3) (2022-06-21)


### Bug Fixes

* update patcher version ([80c11fe](https://github.com/revanced/revanced-cli/commit/80c11fef734bdba9026e91f95ee0a4a522cbefab))

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
