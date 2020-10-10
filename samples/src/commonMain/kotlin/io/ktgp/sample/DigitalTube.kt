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
import io.ktgp.display.tm1637.Config
import io.ktgp.display.tm1637.Tm1637
import io.ktgp.use
import io.ktgp.util.sleep

class DigitalTube : Sample {
  override fun run() {
    Gpio().use { gpio ->
      val config = Config(clkPin = 21, dioPin = 20)
      Tm1637(config, gpio).use { display ->
        for (i in 1 downTo 0) {
          for (j in 59 downTo 0) {
            display.displayString("${i.toPaddedString()}${j.toPaddedString()}", j % 2 == 0)
            sleep(100)
          }
        }
        display.on = false
        sleep(2000)
        display.on = true
        display.displayTemperature(24)
        sleep(1000)
        for (i in 7 downTo 0) {
          display.brightness = i.toUByte()
          sleep(500)
        }
      }
    }
  }

  private fun Int.toPaddedString() = toString(10).padStart(2, '0')
}
