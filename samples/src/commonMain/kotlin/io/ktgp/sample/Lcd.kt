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

import io.ktgp.I2c
import io.ktgp.display.hd44780.Config
import io.ktgp.display.hd44780.Font
import io.ktgp.display.hd44780.Hd4478
import io.ktgp.use
import io.ktgp.util.sleep

class Lcd : Sample {
  override fun run() {
    I2c(1).use {
      val config = Config(lines = 2U, columns = 16U, font = Font.FONT_5_8)
      Hd4478(config, it.device(0x27U)).use { lcd ->
        lcd.blink(5, 500)
        lcd.backlight = true

        lcd.cursor = true
        for (line in 0 until config.lines.toInt()) {
          for (column in 0 until config.columns.toInt()) {
            lcd.setCursorPosition(line.toUByte(), column.toUByte())
            sleep(250)
          }
        }
        lcd.setCursorPosition(0U, 0U)

        lcd.displayString("Hello,")
        lcd.setCursorPosition(1U, (config.columns - 6U).toUByte())
        lcd.displayString("World!")
        sleep(5000)
      }
    }
  }
}
