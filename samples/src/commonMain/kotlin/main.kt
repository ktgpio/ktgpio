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

import io.ktgp.readLine
import io.ktgp.sample.*

fun main() {
  val sample = getSample()
  println("Running $sample...")
  sample.run()
}

private fun getSample(): Sample {
  val samples = arrayOf(
    Blink(),
    Distance(),
    Dht(),
    I2c(),
    Adc(),
    Lcd(),
    DigitalTube(),
    Display(),
  )

  do {
    samples.forEachIndexed { index, sample ->
      println("$index:   $sample")
    }

    val read = readLine()?.toIntOrNull()
    if (read != null && read >= 0 && read < samples.size) {
      return samples[read]
    }
  } while (true)
}
