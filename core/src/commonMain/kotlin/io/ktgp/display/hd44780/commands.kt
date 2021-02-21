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

package io.ktgp.display.hd44780

internal const val LCD_CLEARDISPLAY: UByte = 0x01U
internal const val LCD_RETURNHOME: UByte = 0x02U
internal const val LCD_ENTRYMODESET: UByte = 0x04U
internal const val LCD_DISPLAYCONTROL: UByte = 0x08U
internal const val LCD_CURSORSHIFT: UByte = 0x10U
internal const val LCD_FUNCTIONSET: UByte = 0x20U
internal const val LCD_SETCGRAMADDR: UByte = 0x40U
internal const val LCD_SETDDRAMADDR: UByte = 0x80U

internal const val LCD_ENTRYRIGHT: UByte = 0x00U
internal const val LCD_ENTRYLEFT: UByte = 0x02U
internal const val LCD_ENTRYSHIFTINCREMENT: UByte = 0x01U
internal const val LCD_ENTRYSHIFTDECREMENT: UByte = 0x00U

internal const val LCD_DISPLAYON: UByte = 0x04U
internal const val LCD_DISPLAYOFF: UByte = 0x00U
internal const val LCD_CURSORON: UByte = 0x02U
internal const val LCD_CURSOROFF: UByte = 0x00U
internal const val LCD_BLINKON: UByte = 0x01U
internal const val LCD_BLINKOFF: UByte = 0x00U

internal const val LCD_DISPLAYMOVE: UByte = 0x08U
internal const val LCD_CURSORMOVE: UByte = 0x00U
internal const val LCD_MOVERIGHT: UByte = 0x04U
internal const val LCD_MOVELEFT: UByte = 0x00U

internal const val LCD_8BITMODE: UByte = 0x10U
internal const val LCD_4BITMODE: UByte = 0x00U
internal const val LCD_2LINE: UByte = 0x08U
internal const val LCD_1LINE: UByte = 0x00U
internal const val LCD_5x10DOTS: UByte = 0x04U
internal const val LCD_5x8DOTS: UByte = 0x00U

internal const val LCD_BACKLIGHT: UByte = 0x08U
internal const val LCD_NOBACKLIGHT: UByte = 0x00U

internal const val En: UByte = 0b00000100U
internal const val Rw: UByte = 0b00000010U
internal const val Rs: UByte = 0b00000001U
