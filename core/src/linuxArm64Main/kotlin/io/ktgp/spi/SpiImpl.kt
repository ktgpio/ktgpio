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

package io.ktgp.spi

import io.ktgp.spi.native.*
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.math.min

internal class SpiImpl(device: String) : Spi {

  private var mode: UByte = 0U
  private val file: Int = open("/dev/spidev$device", O_RDWR, 0)
  private val bitsPerWord: UByte
  private val blockSize: Int

  init {
    if (file < 0) {
      error("Can not open spi device")
    }

    bitsPerWord = memScoped {
      val bpw = alloc<UByteVar>()
      if (ioctl(file, SPI_IOC_RD_BITS_PER_WORD, bpw.ptr) == -1) {
        error("Can not get SPI_IOC_RD_BITS_PER_WORD from SPI device")
      }

      bpw.value
    }

    mode = memScoped {
      val mode = alloc<UByteVar>()
      if (ioctl(file, SPI_IOC_RD_MODE, mode.ptr) == -1) {
        error("Can not get SPI_IOC_RD_MODE from SPI device")
      }

      mode.value
    }

    val blockSizeControlFile = fopen(BLOCK_SIZE_CONTROL_FILE, "r")
    blockSize = if (blockSizeControlFile != NULL) {
      try {
        memScoped {
          val value = alloc<IntVar>()
          if (fscanf(blockSizeControlFile, "%d", value.ptr) == 1 && value.value > 0) {
            if (value.value <= XFER3_MAX_BLOCK_SIZE) {
              value.value
            } else {
              XFER3_MAX_BLOCK_SIZE
            }
          } else {
            XFER3_DEFAULT_BLOCK_SIZE
          }
        }
      } finally {
        fclose(blockSizeControlFile)
      }
    } else {
      XFER3_DEFAULT_BLOCK_SIZE
    }
  }

  override fun transfer(settings: Spi.Settings, byte: UByte) {
    val res = memScoped {
      val tx = allocArray<UByteVar>(1)
      tx[0] = byte
      val spiTransfer = alloc<spi_ioc_transfer>() {
        tx_buf = tx.toLong().toULong()
        rx_buf = 0U
        len = 1U
        delay_usecs = 0U
        speed_hz = settings.speedHz
        bits_per_word = settings.bitsPerWord
      }

      ioctl(file, spiIocMessage(1), spiTransfer)
    }

    if (res < 0) {
      error("SPI transaction failed: $res")
    }
  }

  override fun transfer(settings: Spi.Settings, bytes: UByteArray) {
    val length = bytes.size
    val bufSize = min(length, blockSize)
    memScoped {
      val txBuf = allocArray<UByteVar>(bufSize)
      val rxBuf = allocArray<UByteVar>(bufSize)
      val spiTransfer = alloc<spi_ioc_transfer>()

      bytes.usePinned { pinned ->
        var blockStart = 0
        while (blockStart < length) {
          val blockSize = if (blockStart + bufSize > length) {
            length - blockStart
          } else {
            bufSize
          }

          memcpy(txBuf, pinned.addressOf(blockStart), blockSize.toULong())

          spiTransfer.apply {
            tx_buf = txBuf.toLong().toULong()
            rx_buf = rxBuf.toLong().toULong()
            len = blockSize.toUInt()
            delay_usecs = 0U
            speed_hz = settings.speedHz
            bits_per_word = settings.bitsPerWord
          }

          val res = ioctl(file, spiIocMessage(1), spiTransfer)

          if (res < 0) {
            error("SPI transaction failed: $res")
          }

          blockStart += blockSize
        }
      }

      if (mode.toInt() and SPI_CS_HIGH != 0) {
        if (read(file, rxBuf, 0) < 0) {
          error("SPI transaction failed")
        }
      }
    }
  }

  override fun getBitsPerWord(): UByte = bitsPerWord

  override fun getBlockSize(): Int = blockSize

  override fun setMode(mode: UByte) {
    val tmp = this.mode and (SPI_CPHA or SPI_CPOL).inv().toUByte() or mode
    if (doSetMode(tmp)) {
      this.mode = tmp
    }
  }

  override fun getMode(): UByte {
    return mode
  }

  override fun setLsbFirst(value: Boolean) {
    val tmp = if (value) {
      this.mode.toInt() or SPI_LSB_FIRST
    } else {
      this.mode.toInt() and SPI_LSB_FIRST.inv()
    }.toUByte()

    if (doSetMode(tmp)) {
      this.mode = tmp
    }
  }

  override fun close() {
    close(file)
  }

  private fun doSetMode(mode: UByte): Boolean {
    return memScoped {
      val modeVar = alloc<UByteVar> {
        value = mode
      }

      if (ioctl(file, SPI_IOC_WR_MODE, modeVar.ptr) == -1) {
        error("Can not set SPI_IOC_WR_MODE $mode")
      }

      if (ioctl(file, SPI_IOC_RD_MODE, modeVar.ptr) == -1) {
        error("Can not get SPI_IOC_RD_MODE from SPI device")
      }

      modeVar.value != mode
    }
  }

  private companion object {
    private const val BLOCK_SIZE_CONTROL_FILE = "/sys/module/spidev/parameters/bufsiz"
    private const val XFER3_MAX_BLOCK_SIZE = 65535
    private const val SPIDEV_MAXPATH = 4096
    private const val XFER3_DEFAULT_BLOCK_SIZE = SPIDEV_MAXPATH
  }
}
