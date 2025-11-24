package net.xmercerweiss.mdc;

import java.io.Serializable;
import java.util.Locale;
import java.time.temporal.*;
import java.time.chrono.Era;
import java.time.format.TextStyle;


public enum ModernDigitalEra
  implements Era, Serializable
{
  // Enumerated Constants
  BEFORE_EPOCH(0, "Before Epoch", "BE", "-"),
  SINCE_EPOCH(1, "Since Epoch", "SE", "+");

  // Class Constants
  private static final String INV_FIELD_ERR_MSG =
    "ModernDigitalEra given invalid TemporalField; must be ChronoField.ERA";

  private static final String ADJUST_INTO_ERR_MSG =
    "ModernDigitalEra.adjustInto() not supported; call ModernDigitalDate.with(ERA, thisEra.getValue()) instead";

  // Instance Fields
  final int VALUE;
  final String FULL_NAME;
  final String SHORT_NAME;
  final String NARROW_NAME;

  // Constructors
  private ModernDigitalEra(int value, String fullName, String shortName, String narrowName)
  {
    VALUE = value;
    FULL_NAME = fullName;
    SHORT_NAME = shortName;
    NARROW_NAME = narrowName;
  }

  // Public Override Methods
  @Override
  public int getValue()
  {
    return this.VALUE;
  }

  @Override
  public boolean isSupported(TemporalField field)
  {
    return field == ChronoField.ERA;
  }

  @Override
  public ValueRange range(TemporalField field)
  {
    if (isSupported(field))
      return ValueRange.of(0, 1);
    else throw new UnsupportedTemporalTypeException(INV_FIELD_ERR_MSG);
  }

  @Override
  public int get(TemporalField field)
  {
    if (isSupported(field))
      return getValue();
    else throw new UnsupportedTemporalTypeException(INV_FIELD_ERR_MSG);
  }

  @Override
  public long getLong(TemporalField field)
  {
    return get(field);
  }

  @Override
  public <R> R query(TemporalQuery<R> query)
  {
    return query.queryFrom(this);
  }

  @Override
  public Temporal adjustInto(Temporal temporal)
  {
    throw new UnsupportedOperationException(ADJUST_INTO_ERR_MSG);
  }

  @Override
  public String getDisplayName(TextStyle style, Locale locale)
  {
    return getDisplayName(style);
  }

  // Public Methods
  public String getDisplayName(TextStyle style)
  {
    return switch (style)
    {
      case FULL, FULL_STANDALONE -> FULL_NAME;
      case SHORT, SHORT_STANDALONE -> SHORT_NAME;
      case NARROW, NARROW_STANDALONE -> NARROW_NAME;
    };
  }

  public String getDisplayName()
  {
    return getDisplayName(TextStyle.SHORT);
  }
}
