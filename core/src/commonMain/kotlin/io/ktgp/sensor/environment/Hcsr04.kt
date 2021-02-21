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

package io.ktgp.sensor.environment

import io.ktgp.Closeable
import io.ktgp.gpio.Gpio
import io.ktgp.gpio.Input
import io.ktgp.gpio.PinState
import io.ktgp.nanotime
import io.ktgp.sleep

public class Hcsr04(triggerPin: Int, echoPin: Int, gpio: Gpio) : Closeable {

  private val trigger = gpio.output(triggerPin)
  private val echo = gpio.input(echoPin)

  public fun measure(): Double {
    trigger.setState(PinState.HIGH)
    sleep(sec = 0, nanos = 10000)
    trigger.setState(PinState.LOW)

    val startTime = waitUntil(echo, PinState.LOW)
    val endTime = waitUntil(echo, PinState.HIGH)

    return (endTime - startTime) * 343.0 / 10000000 / 2.0
  }

  @Suppress("ControlFlowWithEmptyBody")
  private fun waitUntil(input: Input, state: PinState): Long {
    while (input.getState() == state) {
    }
    return nanotime()
  }

  override fun close() {
    trigger.close()
    echo.close()
  }
}
