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
  BEFORE_EPOCH(0, "Before Epoch", "BE", "B"),
  SINCE_EPOCH(1, "Since Epoch", "SE", "S");

  // Error Methods
  static UnsupportedTemporalTypeException invalidFieldError()
  {
    return new UnsupportedTemporalTypeException(
      "ModernDigitalEra given invalid TemporalField; must be ChronoField.ERA"
    );
  }

  // Instance Fields
  final int VALUE;
  final String FULL_NAME;
  final String SHORT_NAME;
  final String NARROW_NAME;

  // Constructors
  ModernDigitalEra(int value, String fullName, String shortName, String narrowName)
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
    else throw invalidFieldError();
  }

  @Override
  public int get(TemporalField field)
  {
    if (isSupported(field))
      return getValue();
    else throw invalidFieldError();
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
