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

package io.ktgp.sensor.environment

import io.ktgp.gpio.Gpio
import io.ktgp.gpio.PinState
import io.ktgp.nanotime
import io.ktgp.use
import io.ktgp.util.sleep
import kotlin.math.min

public abstract class Dht internal constructor(private val pin: Int, private val gpio: Gpio) {

  private val hiLevel: ULong = 51U

  public fun measure(): Reading {
    val pulses = getPulses()
    if (pulses.size < 80) {
      error("Not enough data to perform measurement")
    }

    val binary = UIntArray(5) {
      pulsesToBinary(pulses, it * 16, it * 16 + 16)
    }

    val checksum = binary.asSequence().take(4).sum()

    if (checksum and 0xFFU != binary[4]) {
      error("Checksum validation failed")
    }

    return getReading(binary)
  }

  protected abstract fun getReading(binary: UIntArray): Reading

  private fun pulsesToBinary(pulses: UIntArray, start: Int, end: Int): UInt {
    var binary = 0
    var hi = false
    for (i in start until end) {
      if (hi) {
        val bit = if (pulses[i] > hiLevel) 1 else 0
        binary = (binary shl 1) or bit
      }

      hi = !hi
    }

    return binary.toUInt()
  }

  private fun getPulses(): UIntArray {
    gpio.output(pin, PinState.LOW).use { output ->
      output.setState(PinState.HIGH)
      sleep(100)
      output.setState(PinState.LOW)
      sleep(13)
    }

    val time = nanotime()
    val transitions = gpio.input(pin).use { input ->
      val transitions = mutableListOf<Long>()
      var state = PinState.HIGH
      while (nanotime() - time < 250_000_000) {
        val newState = input.getState()

        if (newState != state) {
          state = newState
          transitions.add(nanotime())
        }
      }

      transitions.takeLast(82)
    }

    return UIntArray(transitions.size - 2) { i ->
      min(65535, (transitions[i + 1] - transitions[i]) / 1000).toUInt()
    }
  }

  public data class Reading internal constructor(val temperature: Double, val humidity: Double)
}

public class Dht11(pin: Int, gpio: Gpio) : Dht(pin, gpio) {
  override fun getReading(binary: UIntArray): Reading {
    return Reading(binary[2].toDouble(), binary[0].toDouble())
  }
}

public class Dht22(pin: Int, gpio: Gpio) : Dht(pin, gpio) {
  override fun getReading(binary: UIntArray): Reading {
    val humidity = ((binary[0] shl 8) or binary[1]).toInt() / 10.0
    val sign = if (binary[2] and 0x80U != 0U) -1 else 1
    val temperature = sign * ((((binary[2] and 0x7FU) shl 8) or binary[3]).toInt() / 10.0)

    return Reading(temperature, humidity)
  }
}
