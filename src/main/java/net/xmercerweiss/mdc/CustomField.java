package net.xmercerweiss.mdc;

import java.time.temporal.*;


/**
 * Custom {@link java.time.temporal.TemporalField} values for internal use
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-02
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
