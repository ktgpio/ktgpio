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

package io.ktgp.display.st7735

internal const val ST7735_TFTWIDTH = 80
internal const val ST7735_TFTHEIGHT = 160

internal const val ST7735_COLS = 132
internal const val ST7735_ROWS = 162

internal const val ST7735_NOP: UByte = 0x00U
internal const val ST7735_SWRESET: UByte = 0x01U
internal const val ST7735_RDDID: UByte = 0x04U
internal const val ST7735_RDDST: UByte = 0x09U

internal const val ST7735_SLPIN: UByte = 0x10U
internal const val ST7735_SLPOUT: UByte = 0x11U
internal const val ST7735_PTLON: UByte = 0x12U
internal const val ST7735_NORON: UByte = 0x13U

internal const val ST7735_INVOFF: UByte = 0x20U
internal const val ST7735_INVON: UByte = 0x21U

internal const val ST7735_DISPOFF: UByte = 0x28U
internal const val ST7735_DISPON: UByte = 0x29U

internal const val ST7735_CASET: UByte = 0x2AU
internal const val ST7735_RASET: UByte = 0x2BU
internal const val ST7735_RAMWR: UByte = 0x2CU
internal const val ST7735_RAMRD: UByte = 0x2EU

internal const val ST7735_PTLAR: UByte = 0x30U
internal const val ST7735_MADCTL: UByte = 0x36U
internal const val ST7735_COLMOD: UByte = 0x3AU

internal const val ST7735_FRMCTR1: UByte = 0xB1U
internal const val ST7735_FRMCTR2: UByte = 0xB2U
internal const val ST7735_FRMCTR3: UByte = 0xB3U
internal const val ST7735_INVCTR: UByte = 0xB4U
internal const val ST7735_DISSET5: UByte = 0xB6U

internal const val ST7735_PWCTR1: UByte = 0xC0U
internal const val ST7735_PWCTR2: UByte = 0xC1U
internal const val ST7735_PWCTR3: UByte = 0xC2U
internal const val ST7735_PWCTR4: UByte = 0xC3U
internal const val ST7735_PWCTR5: UByte = 0xC4U
internal const val ST7735_VMCTR1: UByte = 0xC5U

internal const val ST7735_RDID1: UByte = 0xDAU
internal const val ST7735_RDID2: UByte = 0xDBU
internal const val ST7735_RDID3: UByte = 0xDCU
internal const val ST7735_RDID4: UByte = 0xDDU

internal const val ST7735_GMCTRP1: UByte = 0xE0U
internal const val ST7735_GMCTRN1: UByte = 0xE1U

internal const val ST7735_PWCTR6: UByte = 0xFCU

internal const val INITR_GREENTAB: UShort = 0x00U
internal const val INITR_REDTAB: UShort = 0x01U
internal const val INITR_BLACKTAB: UShort = 0x02U

internal const val MADCTL_MY: UByte = 0x80U
internal const val MADCTL_MX: UByte = 0x40U
internal const val MADCTL_MV: UByte = 0x20U
internal const val MADCTL_ML: UByte = 0x10U
internal const val MADCTL_RGB: UByte = 0x00U
internal const val MADCTL_BGR: UByte = 0x08U
internal const val MADCTL_MH: UByte = 0x04U

internal class CommandData(val command: UByte, val data: Array<UByte>? = null, val delayMillis: Long = 0)

internal val initSequence
  get() = arrayOf(
    CommandData(ST7735_SWRESET, delayMillis = 150),
    CommandData(ST7735_SLPOUT, delayMillis = 500),
    CommandData(
      ST7735_FRMCTR1,
      arrayOf(
        0x01U,
        0x2CU,
        0x2DU,
      )
    ),
    CommandData(
      ST7735_FRMCTR2,
      arrayOf(
        0x01U,
        0x2CU,
        0x2DU,
      )
    ),
    CommandData(
      ST7735_FRMCTR3,
      arrayOf(
        0x01U,
        0x2CU,
        0x2DU,
        0x01U,
        0x2CU,
        0x2DU,
      )
    ),
    CommandData(
      ST7735_INVCTR,
      arrayOf(
        0x07U,
      )
    ),
    CommandData(
      ST7735_PWCTR1,
      arrayOf(
        0xA2U,
        0x02U,
        0x84U,
      )
    ),
    CommandData(
      ST7735_PWCTR2,
      arrayOf(
        0xC5U,
      )
    ),
    CommandData(
      ST7735_PWCTR3,
      arrayOf(
        0x0AU,
        0x00U
      )
    ),
    CommandData(
      ST7735_PWCTR4,
      arrayOf(
        0x8AU,
        0x2AU
      )
    ),
    CommandData(
      ST7735_PWCTR5,
      arrayOf(
        0x8AU,
        0xEEU
      )
    ),
    CommandData(
      ST7735_VMCTR1,
      arrayOf(
        0x0EU
      )
    ),
    CommandData(
      ST7735_COLMOD,
      arrayOf(
        0x05U
      )
    ),
    CommandData(
      ST7735_GMCTRP1,
      arrayOf(
        0x02U,
        0x1CU,
        0x07U,
        0x12U,
        0x37U,
        0x32U,
        0x29U,
        0x2DU,
        0x29U,
        0x25U,
        0x2BU,
        0x39U,
        0x00U,
        0x01U,
        0x03U,
        0x10U,
      )
    ),
    CommandData(
      ST7735_GMCTRN1,
      arrayOf(
        0x03U,
        0x1DU,
        0x07U,
        0x06U,
        0x2EU,
        0x2CU,
        0x29U,
        0x2DU,
        0x2EU,
        0x2EU,
        0x37U,
        0x3FU,
        0x00U,
        0x00U,
        0x02U,
        0x10U,
      )
    ),
    CommandData(ST7735_NORON, delayMillis = 10),
    CommandData(ST7735_DISPON, delayMillis = 100),
  )
