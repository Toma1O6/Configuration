# Configuration

## Dependency setup
Download latest mod jar from GitHub Releases and put the downloaded file into project_root/libs.

To add gradle dependency add this to your `build.gradle` file
```
dependencies {
  compile files('libs/configuration-MC_VERSION-MOD_VERSION.jar')
}
```
Replace _MC_VERSION_ with your minecraft version, e.g. 1.16.5

Replace _MOD_VERSION_ with version of the file you have downloaded, e.g. 1.0.0

_This might change in the future if I decide to host this file online_
