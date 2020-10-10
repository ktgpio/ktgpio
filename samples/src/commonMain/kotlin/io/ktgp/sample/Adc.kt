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
import io.ktgp.I2c
import io.ktgp.gpio.PinState
import io.ktgp.sensor.adc.ads1x15.Ads1115
import io.ktgp.use
import io.ktgp.util.sleep

class Adc : Sample {
  override fun run() {
    I2c(1).use { i2c ->
      Gpio().use { gpio ->
        gpio.output(6).use { output ->
          val ads = Ads1115(i2c.device(0x48U))
          repeat(10) {
            output.setState(if (it % 2 == 0) PinState.HIGH else PinState.LOW)
            println(ads.read(0, Ads1115.PGA_6_144V))
            sleep(1000)
          }
        }
      }
    }
  }
}
