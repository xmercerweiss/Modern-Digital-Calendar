package net.xmercerweiss.mdc;

import java.io.Serializable;
import java.util.Locale;
import java.time.temporal.*;
import java.time.chrono.Era;
import java.time.format.TextStyle;


/**
 * The eras of the <a href="https://github.com/xmercerweiss/Modern-Digital-Calendar/blob/main/README.md">Modern Digital Calendar system</a>
 * as enumerated constants
 * <br><br>
 * The Modern Digital Calendar only has 2 era, Before Epoch and Since Epoch, with the epoch being 1970 ISO. The epoch
 * is year 0, and is always considered Since Epoch for simplicity. The year 2000 ISO is 30 SE, and 1900 ISO is 70 BE.
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-02
 */
public enum ModernDigitalEra
  implements Era, Serializable
{
  // Enumerated Constants
  BEFORE_EPOCH(0, "Before Epoch", "BE", "B"),
  SINCE_EPOCH(1, "Since Epoch", "SE", "S");

  // Error Methods
  private static UnsupportedTemporalTypeException invalidFieldError()
  {
    return new UnsupportedTemporalTypeException(
      "ModernDigitalEra given invalid TemporalField; must be ChronoField.ERA"
    );
  }

  private static UnsupportedOperationException unsupportedQueryError()
  {
    return new UnsupportedOperationException(
      "ModernDigitalEra does not support TemporalQueries; call ModernDigitalEra.getValue()"
    );
  }

  // Static Methods
  /**
   * Obtains the {@link java.time.chrono.Era} enum represented by a given integer value
   * @param value A signed 32-bit integer
   * @return BE if value < 0, SE if value >= 0
   */
  public static Era ofValue(int value)
  {
    return value <= 0 ? BEFORE_EPOCH : SINCE_EPOCH;
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

  // Override Methods

  /**
   * Returns the numeric value of this {@code Era}
   * @return A signed 32-bit integer; 0 for BE, 1 for SE
   */
  @Override
  public int getValue()
  {
    return this.VALUE;
  }

  /**
   * Determines whether the given {@link java.time.temporal.TemporalField} is supported by
   * this {@code Era}; only {@link java.time.temporal.ChronoField#ERA} is supported
   * @param field A {@link java.time.temporal.TemporalField}, may be null
   * @return {@code true} if the given field is {@link java.time.temporal.ChronoField#ERA}
   */
  @Override
  public boolean isSupported(TemporalField field)
  {
    return field == ChronoField.ERA;
  }

  /**
   * Obtains a {@link java.time.temporal.ValueRange} of valid values for the given field, if supported
   * @param field A {@link java.time.temporal.TemporalField}, must be {@link net.xmercerweiss.mdc.ModernDigitalEra#isSupported supported} and not null
   * @return A {@link java.time.temporal.ValueRange}
   */
  @Override
  public ValueRange range(TemporalField field)
  {
    if (isSupported(field))
      return ValueRange.of(0, 1);
    else throw invalidFieldError();
  }

  /**
   * Returns the numeric value of the given field, if supported, for this {@code Era}
   * @param field A {@link java.time.temporal.TemporalField}, must be {@link net.xmercerweiss.mdc.ModernDigitalEra#isSupported supported} and not null
   * @return A signed 32-bit integer
   */
  @Override
  public int get(TemporalField field)
  {
    if (isSupported(field))
      return getValue();
    else throw invalidFieldError();
  }

  /**
   * Returns the numeric value of the given field, if supported, for this {@code Era} as a long
   * @param field A {@link java.time.temporal.TemporalField}, must be {@link net.xmercerweiss.mdc.ModernDigitalEra#isSupported supported} and not null
   * @return A signed 64-bit integer
   */
  @Override
  public long getLong(TemporalField field)
  {
    return get(field);
  }

  /**
   * This {@code Era} does not support {@code TemporalQueries} methods
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public <R> R query(TemporalQuery<R> query)
  {
    throw unsupportedQueryError();
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalEra#getDisplayName(TextStyle)}
   * @param style The {@link java.time.format.TextStyle style} of the output, not null
   * @param locale The locale to use; has no effect on output
   * @return A {@code String} suitable for user presentation
   */
  @Override
  public String getDisplayName(TextStyle style, Locale locale)
  {
    return getDisplayName(style);
  }

  // Public Methods
  /**
   * Gets the textual representation of this {@code Era} suitable for
   * presentation for the user
   * <br><br>
   * The returned {@code String} will differ based on the {@link java.time.format.TextStyle} given.
   * <table>
   *   <tr>
   *     <td>TextStyle</td> <td>Display Name (SE)</td>
   *   </tr>
   *   <tr>
   *     <td>{@code FULL}</td> <td><em>Since Epoch</em></td>
   *   </tr>
   *   <tr>
   *     <td>{@code SHORT}</td> <td><em>SE</em></td>
   *   </tr>
   *   <tr>
   *     <td>{@code NARROW}</td> <td><em>S</em></td>
   *   </tr>
   * </table>
   * @param style The {@link java.time.format.TextStyle style} of the output, not null
   * @return A {@code String} suitable for user presentation
   */
  public String getDisplayName(TextStyle style)
  {
    return switch (style)
    {
      case FULL, FULL_STANDALONE -> FULL_NAME;
      case SHORT, SHORT_STANDALONE -> SHORT_NAME;
      case NARROW, NARROW_STANDALONE -> NARROW_NAME;
    };
  }

  /**
   * Equivalent to {@code getDisplayName(SHORT)}
   * @return SE for {@code SINCE_EPOCH}, BE for {@code BEFORE_EPOCH}
   */
  public String getDisplayName()
  {
    return getDisplayName(TextStyle.SHORT);
  }
}
