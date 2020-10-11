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

package io.ktgp.sample

import io.ktgp.Spi
import io.ktgp.spi.Spi
import io.ktgp.use

class Spi : Sample {
  override fun run() {
    Spi("0.0").use { spi ->
      val testSpeed = 500000U
      val testMode: UByte = 0x02U

      val initialSpeed = spi.maxSpeed
      dump(spi)
      println("\nSetting speed to $testSpeed...")
      spi.maxSpeed = testSpeed
      dump(spi)
      println("\nSetting speed to initial value")
      spi.maxSpeed = initialSpeed
      dump(spi)

      val initialMode = spi.getMode()
      println("\nSetting mode to $testMode...")
      spi.setMode(testMode)
      dump(spi)
      println("\nSetting mode to initial value")
      spi.setMode(initialMode)
      dump(spi)
    }
  }

  private fun dump(spi: Spi) {
    println(
      """
        bitsPerWord:    ${spi.getBitsPerWord()}
        block size:     ${spi.getBlockSize()}
        mode:           ${spi.getMode()}
        max speed:      ${spi.maxSpeed} Hz
      """.trimIndent()
    )
  }
}
