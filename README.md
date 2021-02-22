# GPIO on a Raspberry Pi with Kotlin/Native

## Getting started

### Supported targets

`ktgpio` only supports `arm64` and `armhf` linux targets. It was tested
and is confirmed to work on
[Raspberry Pi OS armhf](https://downloads.raspberrypi.org/raspios_armhf/)
and [Raspberry Pi OS arm64](https://downloads.raspberrypi.org/raspios_arm64/).

For the list of supported hosts refer to Kotlin/Native
[release notes](https://github.com/JetBrains/kotlin-native/blob/master/RELEASE_NOTES.md#supported-platforms).

### Target preparation

`ktgpio` is dynamically linked with `libgpiod` and `libi2c`. If your target
is running Raspberry Pi OS or any other Debian-like system install them by 
running

```shell script
apt-get install libgpiod2 libi2c0 
```

### Using template repository

The easiest way to get started is creating repository from 
[ktgpio-samples](https://github.com/ktgpio/ktgpio-samples/generate)
template. Please note that build configuration there only supports
`linux_arm32_hfp` and `linux_arm64` Raspberry Pi OS targets. If you
are targeting different OS, make sure you're providing correct
`libgpiod` and `libi2c` libraries to the build. Refer to
[build.gradle.kts](https://github.com/ktgpio/ktgpio-samples/blob/main/build.gradle.kts)
and [native-libs.gradle.kts](https://github.com/ktgpio/ktgpio-samples/blob/main/gradle/native-libs.gradle.kts)
in the template repository for examples, and to the next section for details.

### Starting from scratch

Here is a minimal `build.gradle.kts` example:

```kotlin
plugins {
  kotlin("multiplatform") version "1.4.30"
}

repositories {
  mavenCentral()
}

kotlin {
  val native = linuxArm32Hfp("native")

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("io.ktgp:core:0.0.6")
        implementation(kotlin("stdlib"))
      }
    }
  }

  configure(listOf(native)) {
    binaries.executable()
  }
}
```

Applications using `ktgpio` should be dynamically linked against
`libgpiod` and `libi2c`. Make sure `.so` files for your target
architecture are visible to the linker. If you are cross-compiling,
you can manually copy them from your target machine and specify path
to them using `linkerOpts` property of a `NativeBinary`:

```kotlin
binaries.all {
  linkerOpts.add("-Llibs/")
}
```
