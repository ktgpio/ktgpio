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

package io.ktgp.display.hd44780

import io.ktgp.Closeable
import io.ktgp.display.WithBacklight
import io.ktgp.i2c.I2cDevice
import io.ktgp.util.sleep

public class Hd4478(private val config: Config, private val device: I2cDevice) : WithBacklight, Closeable {

  public var blink: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        applyDisplayControl()
      }
    }

  public var cursor: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        applyDisplayControl()
      }
    }

  override var backlight: Boolean = false
    set(value) {
      if (field != value) {
        field = value
        applyDisplayControl()
      }
    }

  public var on: Boolean = true
    set(value) {
      if (field != value) {
        field = value
        applyDisplayControl()
      }
    }

  init {
    write(0x03U)
    write(0x03U)
    write(0x03U)
    write(0x02U)

    write(LCD_FUNCTIONSET or config.functionSet())
    applyDisplayControl()
    write(LCD_CLEARDISPLAY)
    write(LCD_ENTRYMODESET or LCD_ENTRYLEFT)
    sleep(200)
  }

  private fun applyDisplayControl() {
    val value = (if (on) LCD_DISPLAYON else LCD_DISPLAYOFF) or
      (if (cursor) LCD_CURSORON else LCD_CURSOROFF) or
      (if (blink) LCD_BLINKON else LCD_BLINKOFF)

    write(LCD_DISPLAYCONTROL or value)
  }

  private fun write(cmd: UByte, mode: UByte = 0x00U) {
    write4(mode or (cmd and 0xF0U))
    write4(mode or ((cmd.toInt() shl 4).toUByte() and 0xF0U))
  }

  private fun write4(data: UByte) {
    val backlight = backlight()
    device.write(data or backlight)
    device.write(data or En or backlight)
    sleep(5)
    device.write((data and En.inv()) or backlight)
    sleep(1)
  }

  public fun clear() {
    write(LCD_CLEARDISPLAY)
  }

  public fun displayString(s: String) {
    s.forEach { char ->
      write(char.code.toUByte(), Rs)
    }
  }

  public fun setCursorPosition(line: UByte, column: UByte) {
    if (line > config.lines - 1U) {
      error("Line index out of bounds")
    }

    if (line > config.columns - 1U) {
      error("Column index out of bounds")
    }

    val rowOffset = when (line.toUInt()) {
      0U -> 0x00U
      1U -> 0x40U
      2U -> 0x14U
      3U -> 0x54U
      else -> error("Displays with more than 4 lines are not supported")
    }

    write(LCD_SETDDRAMADDR or (column + rowOffset).toUByte())
  }

  override fun close() {
    backlight = false
    on = false
    applyDisplayControl()
  }

  private fun backlight() = if (backlight) LCD_BACKLIGHT else LCD_NOBACKLIGHT

  private fun Config.functionSet(): UByte {
    val lines = when (lines.toInt()) {
      1 -> LCD_1LINE
      else -> LCD_2LINE
    }

    val font = when (font) {
      Font.FONT_5_8 -> LCD_5x8DOTS
      Font.FONT_5_10 -> LCD_5x10DOTS
    }

    return LCD_4BITMODE or lines or font
  }
}
