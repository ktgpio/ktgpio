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

package io.ktgp

import io.ktgp.gpio.Gpio
import io.ktgp.gpio.Gpiod
import io.ktgp.i2c.I2c
import io.ktgp.i2c.SMBus
import io.ktgp.spi.Spi
import io.ktgp.spi.SpiImpl
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.system.getTimeNanos

public actual fun println(message: Any?) {
  kotlin.io.println(message)
}

public actual fun sleep(sec: Long, nanos: Long) {
  val spec = cValue<timespec> {
    tv_sec = sec
    tv_nsec = nanos
  }
  nanosleep(spec, null)
}

public actual fun nanotime(): Long = getTimeNanos()

public actual fun readFile(path: String): ByteArray {
  val f = fopen(path, "rb")
  try {
    fseek(f, 0, SEEK_END)
    val size = ftell(f)
    fseek(f, 0, SEEK_SET)

    return memScoped {
      val buff = allocArray<ByteVar>(size)
      fread(buff, 1, size.toULong(), f)
      buff.readBytes(size.toInt())
    }
  } finally {
    fclose(f)
  }
}

public actual fun readLine(): String? {
  return kotlin.io.readLine()
}

public actual fun Gpio(n: Int, consumer: String): Gpio = Gpiod(n, consumer)
public actual fun Spi(device: String): Spi = SpiImpl(device)
public actual fun I2c(n: Int): I2c = SMBus(n)
