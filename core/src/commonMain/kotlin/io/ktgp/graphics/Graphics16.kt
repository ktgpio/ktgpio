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

package io.ktgp.graphics

public class Graphics16 internal constructor(private val display: PixelOutput) : Graphics {
  override fun hLine(x: Int, y: Int, w: Int, color: Color) {
    val color16 = color.toRgb565()
    display.setWindow(x, y, x + w - 1, y)
    display.putData(
      UByteArray(w * 2) { i ->
        if (i % 2 == 0) color16.b1 else color16.b2
      }
    )
  }

  override fun vLine(x: Int, y: Int, h: Int, color: Color) {
    val color16 = color.toRgb565()
    display.setWindow(x, y, x, y + h - 1)
    display.putData(
      UByteArray(h * 2) { i ->
        if (i % 2 == 0) color16.b1 else color16.b2
      }
    )
  }

  override fun fillRect(x: Int, y: Int, w: Int, h: Int, color: Color) {
    val color16 = color.toRgb565()
    display.setWindow(x, y, x + w - 1, y + h - 1)
    display.putData(
      UByteArray(w * h * 2) { i ->
        if (i % 2 == 0) color16.b1 else color16.b2
      }
    )
  }
}
