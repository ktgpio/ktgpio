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
import io.ktgp.use

class I2c : Sample {
  override fun run() {
    I2c(1).use { i2c ->
      val first = 0x03U
      val last = 0x77U
      println("     0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f")
      for (i in 0U until 128U step 16) {
        print(i.toString(16).padStart(2, '0') + ": ")
        for (j in 0U until 16U) {
          val address = i + j

          if (address < first || i + j > last) {
            print("   ")
            continue
          }

          val r = try {
            if (i2c.probe(address)) address.toString(16) else "--"
          } catch (t: Throwable) {
            "UU"
          }
          print("$r ")
        }
        println()
      }
    }
  }
}
