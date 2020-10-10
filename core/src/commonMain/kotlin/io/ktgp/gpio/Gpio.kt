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

package io.ktgp.gpio

import io.ktgp.Closeable

public interface Gpio : Closeable {
  public fun input(
    pin: Int,
    activeLow: Boolean = false,
    bias: Bias = Bias.DISABLE
  ): Input

  public fun output(
    pin: Int,
    defaultState: PinState = PinState.LOW,
    activeLow: Boolean = false
  ): Output

  public fun listen(
    pin: Int,
    activeLow: Boolean = false,
    bias: Bias = Bias.DISABLE,
    accept: (Event) -> Boolean
  )
}

public interface Output : Closeable {
  public fun setState(state: PinState)
}

public interface Input : Closeable {
  public fun getState(): PinState
}

public data class Event internal constructor(
  public val type: Type,
  public val sec: Long,
  public val nSec: Long
) {
  public enum class Type {
    RISING_EDGE,
    FALLING_EDGE
  }
}

public enum class Bias {
  DISABLE,
  PULL_DOWN,
  PULL_UP
}

public enum class PinState(public val value: Int) {
  LOW(0),
  HIGH(1);
}
