/*
 * Copyright © 2020 Pavel Kakolin
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

group = "io.ktgp"

ext {
  set("ideaActive", System.getProperty("idea.active") != null)
}

plugins {
  kotlin("multiplatform") version "1.4.10" apply false
  `maven-publish`
}

buildscript {
  dependencies {
    classpath("org.jmailen.gradle:kotlinter-gradle:3.2.0")
  }
}

subprojects {
  group = "io.ktgp"

  apply(plugin="maven-publish")

  publishing {
    repositories {
      maven {
        name = "Bintray"
        url = uri("https://api.bintray.com/maven/ktgpio/ktgpio/core/;publish=1")
        credentials {
          username = System.getenv("BINTRAY_USER")
          password = System.getenv("BINTRAY_API_KEY")
        }
      }
    }
  }
}
