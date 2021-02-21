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

package io.ktgp.display.tm1637

import io.ktgp.Closeable
import io.ktgp.gpio.DigitalIo
import io.ktgp.gpio.Gpio
import io.ktgp.gpio.PinState
import io.ktgp.sleep
import kotlin.math.min

public class Tm1637(private val config: Config, gpio: Gpio) : Closeable {

  private val clk = gpio.output(config.clkPin)
  private val dio = DigitalIo(config.dioPin, gpio)

  public var on: Boolean = true
    set(value) {
      if (field != value) {
        field = value
        cmd()
        ctrl()
      }
    }

  public var brightness: UByte = 7U
    set(value) {
      if (value !in 0U..7U) {
        error("Brightness is out of range")
      }

      if (field != value) {
        field = value
        cmd()
        ctrl()
      }
    }

  init {
    clk.setState(PinState.LOW)
    dio.setState(PinState.LOW)
  }

  public fun displayString(s: String, colon: Boolean = false, position: UByte = 0U) {
    val length = min(s.length, (config.segments - position.toInt()))
    val segments = encodeString(s, length)
    if (segments.size > 1 && colon) {
      segments[1] = segments[1] or 0x80U
    }

    write(segments, position)
  }

  public fun displayTemperature(num: Int) {
    val text = when {
      num < -1 -> "lo"
      num > 99 -> "hi"
      else -> num.toString().padStart(2)
    }

    displayString(text)
    write(ubyteArrayOf(segments[38], segments[12]), 2U)
  }

  private fun start() {
    clk.setState(PinState.HIGH)
    dio.setState(PinState.HIGH)
    dio.setState(PinState.LOW)
    clk.setState(PinState.LOW)
  }

  private fun stop() {
    clk.setState(PinState.LOW)
    dio.setState(PinState.LOW)
    clk.setState(PinState.HIGH)
    dio.setState(PinState.HIGH)
  }

  private fun ctrl() {
    start()
    val on = if (on) TM1637_DSP_ON else TM1637_DSP_OFF
    writeByte(TM1637_CMD3 or on or brightness)
    stop()
  }

  private fun cmd() {
    start()
    writeByte(TM1637_CMD1)
    stop()
  }

  private fun write(segments: UByteArray, pos: UByte) {
    if (pos !in 0U..5U) {
      error("Position is out of range")
    }
    cmd()
    start()
    writeByte(TM1637_CMD2 or pos)
    segments.forEach(::writeByte)
    stop()
    ctrl()
  }

  private fun encodeChar(c: Char): UByte {
    return when (val ord = c.toInt()) {
      32 -> segments[36]
      42 -> segments[38]
      45 -> segments[37]
      in 65..90 -> segments[ord - 55]
      in 97..122 -> segments[ord - 87]
      in 48..57 -> segments[ord - 48]
      else -> error("Character is out of rang")
    }
  }

  private fun encodeString(s: String, length: Int) = UByteArray(length) {
    encodeChar(s[it])
  }

  private fun writeByte(byte: UByte) {
    var b: UByte = byte
    for (i in 0..7) {
      clk.setState(PinState.LOW)
      dio.setState(if (b and 1U == 0U.toUByte()) PinState.LOW else PinState.HIGH)
      b = (b.toUInt() shr 1).toUByte()
      clk.setState(PinState.HIGH)
    }

    clk.setState(PinState.LOW)
    dio.setState(PinState.HIGH)
    clk.setState(PinState.HIGH)

    while (dio.getState() == PinState.HIGH) {
      sleep(0, TM1637_DELAY_NANOS)
      if (dio.getState() == PinState.HIGH) {
        dio.setState(PinState.LOW)
      }
    }
  }

  override fun close() {
    on = false
    clk.close()
    dio.close()
  }

  private companion object {
    private val segments = ubyteArrayOf(
      0x3FU, 0x06U, 0x5BU, 0x4FU, 0x66U, 0x6DU, 0x7DU, 0x07U, 0x7FU, 0x6FU, 0x77U, 0x7CU, 0x39U,
      0x5EU, 0x79U, 0x71U, 0x3DU, 0x76U, 0x06U, 0x1EU, 0x76U, 0x38U, 0x55U, 0x54U, 0x3FU, 0x73U,
      0x67U, 0x50U, 0x6DU, 0x78U, 0x3EU, 0x1CU, 0x2AU, 0x76U, 0x6EU, 0x5BU, 0x00U, 0x40U, 0x63U
    )
  }
}
