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

import cnames.structs.gpiod_chip
import cnames.structs.gpiod_line
import io.ktgp.gpio.native.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr

internal class Gpiod(n: Int, private val consumer: String) : Gpio {
  private val chip: CPointer<gpiod_chip> = gpiod_chip_open("/dev/gpiochip$n")
    ?: error("Can not access specified GPIO chip: $n")

  override fun input(pin: Int, activeLow: Boolean, bias: Bias): Input {
    val line = getLine(pin)

    if (gpiod_line_request_input_flags(line, consumer, flags(activeLow, bias)) != 0) {
      error("Can not request input from GPIO line $pin")
    }

    return object : GpiodInput(line) {
      override fun close() {
        gpiod_line_release(line)
      }
    }
  }

  override fun output(pin: Int, defaultState: PinState, activeLow: Boolean): Output {
    val line = getLine(pin)

    if (gpiod_line_request_output_flags(line, consumer, flags(activeLow), defaultState.value) != 0) {
      error("Can not request output from GPIO line $pin")
    }

    return object : GpiodOutput(line) {
      override fun close() {
        gpiod_line_release(line)
      }
    }
  }

  override fun listen(pin: Int, activeLow: Boolean, bias: Bias, accept: (Event) -> Boolean) {
    val line = getLine(pin)

    if (gpiod_line_request_both_edges_events_flags(line, consumer, flags(activeLow, bias)) < 0) {
      error("Can not request events from GPIO line $pin")
    }

    try {
      memScoped {
        val event = alloc<gpiod_line_event>()

        while (true) {
          if (gpiod_line_event_wait(line, null) < 0) {
            error("Can not wait for event on GPIO line $pin")
          }

          if (gpiod_line_event_read(line, event.ptr) < 0) {
            error("Failed to read event from GPIO line $pin")
          }

          val eventKt = event.run {
            val type = when (event_type.toUInt()) {
              GPIOD_LINE_EVENT_RISING_EDGE -> Event.Type.RISING_EDGE
              GPIOD_LINE_EVENT_FALLING_EDGE -> Event.Type.FALLING_EDGE
              else -> error("Unexpected event on GPIO line $pin")
            }

            Event(type, ts.tv_sec, ts.tv_nsec)
          }

          if (!accept(eventKt)) {
            break
          }
        }
      }
    } finally {
      gpiod_line_release(line)
    }
  }

  override fun close() {
    gpiod_chip_close(chip)
  }

  private fun flags(activeLow: Boolean): Int {
    if (activeLow) {
      return GPIOD_LINE_REQUEST_FLAG_ACTIVE_LOW.toInt()
    }

    return 0
  }

  private fun flags(activeLow: Boolean, bias: Bias): Int {
    if (bias != Bias.DISABLE) {
      error("Internal bias is not supported")
    }

    return flags(activeLow)
  }

  private fun getLine(n: Int): CPointer<gpiod_line> {
    return gpiod_chip_get_line(chip, n.toUInt())
      ?: error("Can not get GPIO line $n")
  }
}
