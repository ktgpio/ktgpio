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

package io.ktgp.i2c

import io.ktgp.i2c.native.*
import kotlinx.cinterop.*
import platform.posix.O_RDWR
import platform.posix.ioctl
import platform.posix.open

internal class SMBus(n: Int) : I2c {

  private val file: Int = open("/dev/i2c-$n", O_RDWR)
  private var address: UInt = 0U

  init {
    if (file < 0) {
      error("Can not open I2C device")
    }
  }

  override fun device(address: UInt): I2cDevice {
    return object : I2cDevice {
      override fun write(value: UByte) {
        selectDevice(address)
        if (i2c_smbus_write_byte(file, value) != 0) {
          error("I2C transaction failed")
        }
      }

      override fun write(command: UByte, values: UByteArray) {
        selectDevice(address)
        values.usePinned { pinned ->
          if (i2c_smbus_write_i2c_block_data(file, command, values.size.toUByte(), pinned.addressOf(0)) != 0) {
            error("I2C transaction failed")
          }
        }
      }

      override fun read(command: UByte, length: UByte): UByteArray {
        selectDevice(address)
        return memScoped {
          val buff = allocArray<UByteVar>(length.toInt())
          if (i2c_smbus_read_i2c_block_data(file, command, length, buff) == length.toInt()) {
            UByteArray(length.toInt()) {
              buff[it]
            }
          } else {
            error("I2C transaction failed")
          }
        }
      }
    }
  }

  override fun probe(address: UInt): Boolean {
    selectDevice(address)
    return if (address in 0x30U..0x37U || address in 0x50U..0x5FU) {
      i2c_smbus_read_byte(file)
    } else {
      i2c_smbus_write_quick(file, I2C_SMBUS_WRITE)
    } >= 0
  }

  override fun close() {
    platform.posix.close(file)
  }

  private fun selectDevice(address: UInt) {
    if (this.address != address) {
      if (ioctl(file, I2C_SLAVE, address) < 0) {
        this.address = 0U
        error("Can not communicate with I2C device at specified address")
      } else {
        this.address = address
      }
    }
  }
}
