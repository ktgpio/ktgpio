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

package io.ktgp.gpio

internal class DigitalIo(private val pin: Int, private val gpio: Gpio) : Input, Output {

  private var input: Input? = null
  private var output: Output? = null

  override fun setState(state: PinState) {
    output().setState(state)
  }

  override fun getState(): PinState {
    return input().getState()
  }

  private fun output(): Output {
    if (input != null) {
      input!!.close()
      input = null
    }

    if (output == null) {
      output = gpio.output(pin)
    }

    return output!!
  }

  private fun input(): Input {
    if (output != null) {
      output!!.close()
      output = null
    }

    if (input == null) {
      input = gpio.input(pin)
    }

    return input!!
  }

  override fun close() {
    input?.close()
    output?.close()
    input = null
    output = null
  }
}
