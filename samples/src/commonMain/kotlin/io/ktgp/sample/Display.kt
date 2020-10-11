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
import io.ktgp.Spi
import io.ktgp.display.Orientation
import io.ktgp.display.st7735.Config
import io.ktgp.display.st7735.St7735
import io.ktgp.graphics.Color
import io.ktgp.use
import io.ktgp.util.sleep

class Display : Sample {
  override fun run() {
    val config = Config(
      dcPin = 23,
      rstPin = 24,
      backlightPin = 26,
      width = 128,
      height = 160,
    )

    Spi("0.0").use { spi ->
      spi.setMode(0U)
      spi.setLsbFirst(false)
      Gpio().use { gpio ->
        St7735(config, gpio, spi).use(::test)
      }
    }
  }

  private fun test(display: St7735) {
    println(
      """
        orientation:    ${display.orientation}
        width:          ${display.width}
        height:         ${display.height}
      """.trimIndent()
    )

    val rectSize = 20

    val graphics = display.getGraphics()
    graphics.fillRect(
      x = 0,
      y = 0,
      w = display.width,
      h = display.height,
      color = Color.BLACK
    )

    display.blink(5, 500)
    display.backlight = true

    repeat(5) {
      Orientation.values().forEach { value ->
        display.orientation = value
        graphics.fillRect(
          x = 0,
          y = 0,
          w = rectSize,
          h = rectSize,
          color = Color.RED
        )
        graphics.fillRect(
          x = 0,
          y = display.height - rectSize,
          w = rectSize,
          h = rectSize,
          color = Color.GREEN
        )
        graphics.fillRect(
          x = display.width - rectSize,
          y = 0,
          w = rectSize,
          h = rectSize,
          color = Color.BLUE
        )
        graphics.fillRect(
          x = display.width - rectSize,
          y = display.height - rectSize,
          w = rectSize,
          h = rectSize,
          color = Color.WHITE
        )
        sleep(1000)
      }
    }
  }
}
