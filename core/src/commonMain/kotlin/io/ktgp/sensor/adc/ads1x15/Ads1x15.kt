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

package io.ktgp.sensor.adc.ads1x15

internal const val ADS1X15_REG_CONFIG_PGA_MASK: UShort = 0x0E00U

internal const val ADS1X15_REG_CONFIG_MUX_SINGLE_0: UShort = 0x4000U
internal const val ADS1X15_REG_CONFIG_MUX_SINGLE_1: UShort = 0x5000U
internal const val ADS1X15_REG_CONFIG_MUX_SINGLE_2: UShort = 0x6000U
internal const val ADS1X15_REG_CONFIG_MUX_SINGLE_3: UShort = 0x7000U

internal const val ADS1X15_REG_CONFIG_CQUE_NONE: UShort = 0x0003U
internal const val ADS1X15_REG_CONFIG_CLAT_NONLAT: UShort = 0x0000U

internal const val ADS1X15_REG_CONFIG_CPOL_ACTVLOW: UShort = 0x0000U
internal const val ADS1X15_REG_CONFIG_CPOL_ACTVHI: UShort = 0x0008U

internal const val ADS1X15_REG_CONFIG_CMODE_TRAD: UShort = 0x0000U

internal const val ADS1X15_REG_CONFIG_DR_MASK: UShort = 0x00E0U
internal const val ADS1X15_REG_CONFIG_DR_128SPS: UShort = 0x0000U
internal const val ADS1X15_REG_CONFIG_DR_250SPS: UShort = 0x0020U
internal const val ADS1X15_REG_CONFIG_DR_490SPS: UShort = 0x0040U
internal const val ADS1X15_REG_CONFIG_DR_920SPS: UShort = 0x0060U
internal const val ADS1X15_REG_CONFIG_DR_1600SPS: UShort = 0x0080U
internal const val ADS1X15_REG_CONFIG_DR_2400SPS: UShort = 0x00A0U
internal const val ADS1X15_REG_CONFIG_DR_3300SPS: UShort = 0x00C0U

internal const val ADS1X15_REG_CONFIG_MODE_MASK: UShort = 0x0100U
internal const val ADS1X15_REG_CONFIG_MODE_CONTIN: UShort = 0x0000U
internal const val ADS1X15_REG_CONFIG_MODE_SINGLE: UShort = 0x0100U

internal const val ADS1X15_REG_CONFIG_OS_MASK: UShort = 0x8000U
internal const val ADS1X15_REG_CONFIG_OS_SINGLE: UShort = 0x8000U
internal const val ADS1X15_REG_CONFIG_OS_BUSY: UShort = 0x0000U
internal const val ADS1X15_REG_CONFIG_OS_NOTBUSY: UShort = 0x8000U

internal const val ADS1X15_REG_POINTER_MASK: UByte = 0x03U
internal const val ADS1X15_REG_POINTER_CONVERT: UByte = 0x00U
internal const val ADS1X15_REG_POINTER_CONFIG: UByte = 0x01U
internal const val ADS1X15_REG_POINTER_LOWTHRESH: UByte = 0x02U
internal const val ADS1X15_REG_POINTER_HITHRESH: UByte = 0x03U
