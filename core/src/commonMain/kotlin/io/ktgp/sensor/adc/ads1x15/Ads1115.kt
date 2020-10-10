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

package io.ktgp.sensor.adc.ads1x15

import io.ktgp.i2c.I2cDevice
import io.ktgp.sensor.adc.Reading
import io.ktgp.util.toUByteArray

public class Ads1115(private val device: I2cDevice) {

  public fun read(channel: Int, gain: Gain = PGA_4_096V): Reading {
    val mux = when (channel) {
      0 -> ADS1X15_REG_CONFIG_MUX_SINGLE_0
      1 -> ADS1X15_REG_CONFIG_MUX_SINGLE_1
      2 -> ADS1X15_REG_CONFIG_MUX_SINGLE_2
      3 -> ADS1X15_REG_CONFIG_MUX_SINGLE_3
      else -> error("Can not read from channel $channel")
    }

    val config = ADS1X15_REG_CONFIG_CQUE_NONE or
      ADS1X15_REG_CONFIG_CLAT_NONLAT or
      ADS1X15_REG_CONFIG_CPOL_ACTVLOW or
      ADS1X15_REG_CONFIG_CMODE_TRAD or
      ADS1X15_REG_CONFIG_DR_1600SPS or
      ADS1X15_REG_CONFIG_MODE_SINGLE or
      ADS1X15_REG_CONFIG_OS_SINGLE or
      gain.pga or
      mux

    device.write(ADS1X15_REG_POINTER_CONFIG, config.toUByteArray())

    @Suppress("ControlFlowWithEmptyBody")
    while (conversionComplete() == 0) {
    }

    return gain.reading(device.read(ADS1X15_REG_POINTER_CONVERT, 2U).toInt())
  }

  private fun conversionComplete(): Int {
    return device.read(ADS1X15_REG_POINTER_CONFIG, 2U).toInt() and 0x8000
  }

  private fun UByteArray.toInt() = toInt(get(1), get(0))

  private fun toInt(low: UByte, high: UByte): Int {
    val value = (high.toInt() shl 8) or low.toInt()
    return if (value and 0x8000 != 0) {
      value - (1 shl 16)
    } else {
      value
    }
  }

  public companion object {
    public val PGA_6_144V: Gain = Gain(6.144, 0x0000U)
    public val PGA_4_096V: Gain = Gain(4.096, 0x0200U)
    public val PGA_2_048V: Gain = Gain(2.048, 0x0400U)
    public val PGA_1_024V: Gain = Gain(1.024, 0x0600U)
    public val PGA_0_512V: Gain = Gain(0.512, 0x0800U)
    public val PGA_0_256V: Gain = Gain(0.256, 0x0A00U)
  }
}
