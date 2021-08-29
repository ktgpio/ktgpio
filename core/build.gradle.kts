/*
 * Copyright © 2021 Pavel Kakolin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

plugins {
  kotlin("multiplatform")
  id("org.jmailen.kotlinter")
}

repositories {
  mavenCentral()
}

val ideaActive = rootProject.ext["ideaActive"] == true

kotlin {
  explicitApi()

  val targets = if (ideaActive) {
    listOf(linuxArm64("posix"))
  } else {
    listOf(linuxArm32Hfp(), linuxArm64())
  }

  sourceSets.all {
    languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(kotlin("stdlib"))
      }
    }

    if (!ideaActive) {
      val posixMain by creating {
        dependsOn(commonMain)
      }
      val linuxArm64Main by getting {
        dependsOn(posixMain)
      }
      val linuxArm32HfpMain by getting {
        dependsOn(posixMain)
      }
    } else {
      val linuxArm64Main by creating {
        dependsOn(commonMain)
      }
      val posixMain by getting {
        dependsOn(linuxArm64Main)
      }
    }

    configure(targets) {
      compilations["main"].apply {
        cinterops.create("gpiod") {
          includeDirs("src/nativeInterop/cinterop/headers/include/")
        }

        cinterops.create("i2c") {
          includeDirs("src/nativeInterop/cinterop/headers/include/")
        }

        cinterops.create("spi") {
          includeDirs("src/nativeInterop/cinterop/headers/include/")
        }
      }
    }
  }
}
