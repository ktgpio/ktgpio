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

package io.ktgp.display.st7735

import io.ktgp.Closeable
import io.ktgp.display.Orientation
import io.ktgp.display.WithBacklight
import io.ktgp.gpio.Gpio
import io.ktgp.gpio.PinState
import io.ktgp.graphics.Graphics
import io.ktgp.graphics.Graphics16
import io.ktgp.graphics.PixelOutput
import io.ktgp.spi.Spi
import io.ktgp.util.sleep
import io.ktgp.util.toUByteArray

public class St7735(private val config: Config, gpio: Gpio, private val device: Spi) :
  PixelOutput,
  WithBacklight,
  Closeable {

  private val dc = config.dcPin.let(gpio::output)
  private val rst = config.rstPin?.let(gpio::output)
  private val backlightPin = config.backlightPin?.let(gpio::output)
  private val spiSettings = Spi.Settings(
    speedHz = config.spiSpeedHz.toUInt(),
    bitsPerWord = device.getBitsPerWord()
  )

  override var orientation: Orientation = Orientation.LANDSCAPE
    set(value) {
      if (field != value) {
        field = value
        applyOrientation()
      }
    }

  override val width: Int
    get() = if (orientation.portrait) config.width else config.height

  override val height: Int
    get() = if (orientation.portrait) config.height else config.width

  override var backlight: Boolean = false
    get() = backlightPin != null && field
    set(value) {
      if (field != value) {
        field = value
        backlightPin?.setState(if (value) PinState.HIGH else PinState.LOW)
      }
    }

  public var invert: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        applyInvert()
      }
    }

  init {
    reset()
    init()
  }

  override fun setWindow(x0: Int, y0: Int, x1: Int, y1: Int) {
    applyWindow(x0, x1, y0, y1)
    command(ST7735_RAMWR)
  }

  override fun putData(data: UByteArray) {
    data(data)
  }

  private fun init() {
    initSequence.forEach { commandData ->
      command(commandData.command)
      commandData.data?.let { data ->
        data(data.toUByteArray())
      }
      if (commandData.delayMillis > 0) sleep(commandData.delayMillis)
    }
    applyInvert()
    applyOrientation()
    applyWindow(0, 0, config.width - 1, config.height - 1)
  }

  private fun reset() {
    rst?.run {
      rst.setState(PinState.HIGH)
      sleep(500)
      rst.setState(PinState.LOW)
      sleep(500)
      rst.setState(PinState.HIGH)
      sleep(500)
    }
  }

  private fun applyInvert() {
    command(if (invert) ST7735_INVON else ST7735_INVOFF)
  }

  private fun applyOrientation() {
    command(ST7735_MADCTL)
    data(
      when (orientation) {
        Orientation.PORTRAIT -> MADCTL_MX or MADCTL_MY or MADCTL_RGB
        Orientation.LANDSCAPE -> MADCTL_MY or MADCTL_MV or MADCTL_RGB
        Orientation.PORTRAIT_FLIPPED -> MADCTL_RGB
        Orientation.LANDSCAPE_FLIPPED -> MADCTL_MX or MADCTL_MV or MADCTL_RGB
      }
    )
  }

  private fun applyWindow(x0: Int, x1: Int, y0: Int, y1: Int) {
    command(ST7735_CASET)
    data(
      toUByteArray(
        (x0 + config.offsetLeft).toUShort(),
        (x1 + config.offsetLeft).toUShort()
      )
    )
    command(ST7735_RASET)
    data(
      toUByteArray(
        (y0 + config.offsetTop).toUShort(),
        (y1 + config.offsetTop).toUShort()
      )
    )
  }

  private fun command(command: UByte) {
    send(command, isCommand = true)
  }

  private fun data(data: UByte) {
    send(data, isCommand = false)
  }

  private fun data(data: UByteArray) {
    send(data, isCommand = false)
  }

  private fun send(data: UByte, isCommand: Boolean) {
    dc.setState(if (isCommand) PinState.LOW else PinState.HIGH)
    device.transfer(settings = spiSettings, data)
  }

  private fun send(data: UByteArray, isCommand: Boolean) {
    dc.setState(if (isCommand) PinState.LOW else PinState.HIGH)
    device.transfer(settings = spiSettings, data)
  }

  public fun getGraphics(): Graphics {
    return Graphics16(this)
  }

  override fun close() {
    backlightPin?.close()
    rst?.close()
    dc.close()
  }
}
