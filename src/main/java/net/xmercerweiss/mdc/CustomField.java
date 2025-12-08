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

import java.time.temporal.*;


/**
 * Custom {@link java.time.temporal.TemporalField} values for internal use
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-08
 */
enum CustomField
  implements TemporalField
{
  // Enumerated Constants
  MODIFIED_JULIAN_DAY,
  QUARTER_OF_YEAR;

  // Error Methods
  private static UnsupportedOperationException unsupportedError()
  {
    return new UnsupportedOperationException(
      "CustomField does not implement this operation"
    );
  }

  @Override
  public TemporalUnit getBaseUnit()
  {
    return switch (this)
    {
      case MODIFIED_JULIAN_DAY -> ChronoUnit.DAYS;
      case QUARTER_OF_YEAR -> CustomUnit.QUARTERS;
    };
  }

  @Override
  public TemporalUnit getRangeUnit()
  {
    return switch (this)
    {
      case MODIFIED_JULIAN_DAY -> ChronoUnit.FOREVER;
      case QUARTER_OF_YEAR -> ChronoUnit.YEARS;
    };
  }

  @Override
  public ValueRange range()
  {
    return switch (this)
    {
      case MODIFIED_JULIAN_DAY -> ValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE);
      case QUARTER_OF_YEAR -> ValueRange.of(1, 4);
    };
  }

  @Override
  public boolean isDateBased()
  {
    return true;
  }

  @Override
  public boolean isTimeBased()
  {
    return false;
  }

  @Override
  public boolean isSupportedBy(TemporalAccessor temporal)
  {
    return false;
  }

  @Override
  public ValueRange rangeRefinedBy(TemporalAccessor temporal)
  {
    throw unsupportedError();
  }

  @Override
  public long getFrom(TemporalAccessor temporal)
  {
    throw unsupportedError();
  }

  @Override
  public <R extends Temporal> R adjustInto(R temporal, long newValue)
  {
    throw unsupportedError();
  }
}
