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

group = "io.ktgp"

ext {
  set("ideaActive", System.getProperty("idea.active") != null)
}

plugins {
  kotlin("multiplatform") version "1.4.30" apply false
  signing
  `maven-publish`
}

buildscript {
  dependencies {
    classpath("org.jmailen.gradle:kotlinter-gradle:3.3.0")
  }
}

val javadocTask = task<Zip>("javadoc") {
  from(".")
  include("README.md")
  archiveFileName.set("javadoc.jar")
  destinationDirectory.set(buildDir)
}

subprojects {
  group = "io.ktgp"

  apply(plugin="maven-publish")
  apply(plugin="signing")

  tasks.named("publish") { dependsOn(javadocTask) }
  tasks.named("publishToMavenLocal") { dependsOn(javadocTask) }

  publishing {
    publications {
      all {
        if (this is MavenPublication) {
          artifact(javadocTask.outputs.files.singleFile) {
            classifier = "javadoc"
            extension = "jar"
          }
          pom {
            name.set("ktgpio")
            description.set("GPIO on a Raspberry Pi with Kotlin/Native")
            url.set("https://ktgp.io/")
            licenses {
              license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
              }
            }
            developers {
              developer {
                name.set("Pavel Kakolin")
                email.set("appr@appr.me")
                organization.set("ktgpio")
                organizationUrl.set("https://github.com/ktgpio")
              }
            }
            scm {
              connection.set("scm:git:https://github.com/ktgpio/ktgpio.git")
              developerConnection.set("scm:git:ssh://git@github.com:ktgpio/ktgpio.git")
              url.set("https://github.com/ktgpio/ktgpio/tree/main")
            }
          }
        }
      }
    }

    repositories {
      maven {
        name = "ossrh"
        url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        credentials {
          username = System.getenv("OSSRH_USER")
          password = System.getenv("OSSRH_PASSWORD")
        }
      }
    }
  }

  val signingKey: String? = System.getenv("GPG_KEY")
  val signingPassword: String? = System.getenv("GPG_PASSWORD")
  if (signingKey != null && signingPassword != null) {
    signing {
      useInMemoryPgpKeys(signingKey, signingPassword)
      sign(publishing.publications)
    }
  }
}
