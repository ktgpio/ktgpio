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
import io.ktgp.gpio.PinState
import io.ktgp.readLine
import io.ktgp.use
import io.ktgp.util.sleep

class Blink : Sample {
  private val delayMs = 1000L

  override fun run() {
    Gpio().use { gpio ->
      do {
        print("Pin number: ")
        val line = readLine()
        if (line != null && line != "q") {
          line.toIntOrNull()?.let { lineNumber ->
            if (lineNumber in 0..27) {
              gpio.output(lineNumber).use { output ->
                var state = PinState.LOW
                repeat(5) {
                  state = if (state == PinState.LOW) PinState.HIGH else PinState.LOW
                  output.setState(state)
                  sleep(delayMs)
                }
              }
            }
          }
        } else {
          break
        }
      } while (true)
    }
  }
}
