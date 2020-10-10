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

import io.ktgp.Gpio
import io.ktgp.sensor.environment.Hcsr04
import io.ktgp.use
import io.ktgp.util.sleep
import kotlin.math.roundToInt

class Distance : Sample {
  override fun run() {
    Gpio().use { gpio ->
      Hcsr04(23, 24, gpio).use { sensor ->
        while (true) {
          val distance = sensor.measure()
          println("Measured Distance = ${distance.asDistance()}")
          sleep(1000)
        }
      }
    }
  }

  private fun Double.asDistance(): String {
    val distance = (this * 10).roundToInt()
    val distanceCm = distance / 10
    val distanceMm = distance % 10

    return "$distanceCm.${distanceMm}cm"
  }
}
