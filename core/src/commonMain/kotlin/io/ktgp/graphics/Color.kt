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

package io.ktgp.graphics

public open class Color(public val r: UByte, public val g: UByte, public val b: UByte) {
  public fun toRgb565(): Color16 {
    val rb = (r.toInt() and 0xF8) shl 8
    val rg = (g.toInt() and 0xFC) shl 3
    val bb = b.toInt() shr 3
    return Color16((rb or rg or bb).toUShort())
  }

  public companion object {
    public val BLACK: Color = Color(0U, 0U, 0U)
    public val WHITE: Color = Color(0xFFU, 0xFFU, 0xFFU)
    public val RED: Color = Color(0xFFU, 0U, 0U)
    public val GREEN: Color = Color(0U, 0xFFU, 0U)
    public val BLUE: Color = Color(0U, 0U, 0xFFU)
  }
}
