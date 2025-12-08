/*
 *
 * BSD Zero-Clause License
 *
 * Copyright (C) 2025 Xavier Mercerweiss <mercerweissx@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose
 * with or without fee is hereby granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT,
 * OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package net.xmercerweiss.mdc;

import java.time.Duration;
import java.time.temporal.*;


/**
 * Custom {@link java.time.temporal.TemporalUnit} values for internal use
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-08
 */
enum CustomUnit
  implements TemporalUnit
{
  // Enumerated Constants
  QUARTERS;

  // Class Constants
  private static final long APPROX_QUARTER_TO_NANOS =
    13   // Weeks
    * 7  // Days
    * 24 // Hours
    * 60 // Minutes
    * 60 // Seconds
    * 1_000_000_000L;

  // Error Methods
  private static UnsupportedOperationException unsupportedError()
  {
    return new UnsupportedOperationException(
      "CustomUnit does not implement this operation"
    );
  }

  // Override Methods
  @Override
  public Duration getDuration()
  {
    return switch (this)
    {
      case QUARTERS -> Duration.ofNanos(APPROX_QUARTER_TO_NANOS);
    };
  }

  @Override
  public boolean isDurationEstimated()
  {
    return true;
  }

  @Override
  public boolean isDateBased()
  {
    return false;
  }

  @Override
  public boolean isTimeBased()
  {
    return false;
  }

  @Override
  public <R extends Temporal> R addTo(R temporal, long amount)
  {
    throw unsupportedError();
  }

  @Override
  public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive)
  {
    throw unsupportedError();
  }
}
