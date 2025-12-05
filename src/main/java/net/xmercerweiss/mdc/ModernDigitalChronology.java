package net.xmercerweiss.mdc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.time.*;
import java.time.chrono.*;
import java.time.temporal.*;
import java.time.format.TextStyle;

import static java.util.Map.entry;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.*;


/**
 * The {@code Chronology} class implementing the <a href="https://github.com/xmercerweiss/Modern-Digital-Calendar/blob/main/README.md">Modern Digital Calendar system</a>
 * <br><br>
 * Exclusively implements datekeeping, meaning attempting to work with time-based classes (such as
 * {@link java.time.LocalTime}, {@link java.time.LocalDateTime}, or {@link java.time.Duration}) or values
 * (such as {@link java.time.temporal.ChronoUnit#HOURS}) will typically throw an {@code UnsupportedOperationException}
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-02
 */
public class ModernDigitalChronology
  extends AbstractChronology
  implements Serializable
{
  // Class Constants
  public static final ModernDigitalChronology INSTANCE = new ModernDigitalChronology();

  public static final int ISO_YEAR_OFFSET = 1970;

  public static final int DAYS_PER_WEEK = 7;
  public static final int WEEKS_PER_MONTH = 4;
  public static final int DAYS_PER_MONTH = DAYS_PER_WEEK * WEEKS_PER_MONTH;
  public static final int NON_LEAP_MONTHS_PER_YEAR = 13;
  public static final int WEEKS_PER_YEAR = WEEKS_PER_MONTH * NON_LEAP_MONTHS_PER_YEAR;
  public static final int WEEKS_PER_QUARTER = WEEKS_PER_YEAR / 4;
  public static final int NON_LEAP_DAYS_PER_YEAR = NON_LEAP_MONTHS_PER_YEAR * DAYS_PER_MONTH;
  public static final int DAYS_PER_COMMON_YEAR = NON_LEAP_DAYS_PER_YEAR + 1;
  public static final int DAYS_PER_LEAP_YEAR = NON_LEAP_DAYS_PER_YEAR + 2;

  // The difference, in days, between an Epoch Day and a Modified Julian Day (MJD)
  // The MJD is the number of full days since midnight on November 17th 1858
  // January 1st 1970 is MJD 40,587
  public static final int MJD_EPOCH_OFFSET_IN_DAYS = 40587;

  private static final String ID = "ModernDigital";
  private static final String DISPLAY_NAME = "Modern Digital Calendar";

  private static final Map<TemporalField,ChronoField> FIELD_TO_INTERNAL_FIELD = Map.ofEntries(
    entry(ERA, ERA),
    entry(YEAR_OF_ERA, YEAR_OF_ERA),
    entry(YEAR, YEAR),
    entry(MONTH_OF_YEAR, MONTH_OF_YEAR),
    entry(ALIGNED_WEEK_OF_YEAR, ALIGNED_WEEK_OF_YEAR),
    entry(ALIGNED_WEEK_OF_MONTH, ALIGNED_WEEK_OF_MONTH),
    entry(DAY_OF_YEAR, DAY_OF_YEAR),
    entry(DAY_OF_MONTH, DAY_OF_MONTH),
    entry(DAY_OF_WEEK, DAY_OF_WEEK),
    entry(ALIGNED_DAY_OF_WEEK_IN_MONTH, DAY_OF_WEEK),
    entry(ALIGNED_DAY_OF_WEEK_IN_YEAR, DAY_OF_WEEK),
    entry(EPOCH_DAY, EPOCH_DAY)
  );

  private static final Map<TemporalUnit,ChronoField> UNIT_TO_INTERNAL_FIELD = Map.ofEntries(
    entry(ERAS,ERA),
    entry(YEARS,YEAR_OF_ERA),
    entry(MONTHS,MONTH_OF_YEAR),
    entry(WEEKS,ALIGNED_WEEK_OF_YEAR),
    entry(DAYS,DAY_OF_MONTH)
  );

  // Error Methods

  // Only the Chronology has package-private error methods, such that they may be used
  // elsewhere in the package. All other classes have private error methods following this
  // same pattern

  static ClassCastException invalidEraError()
  {
    return new ClassCastException(
      "ModernDigitalChronology given invalid Era; must be instance of ModernDigitalEra"
    );
  }

  static UnsupportedTemporalTypeException invalidFieldError()
  {
    return new UnsupportedTemporalTypeException(

      "ModernDigitalChronology given invalid TemporalField; is field supported by chronology?"
    );
  }

  static DateTimeException invalidDateError()
  {
    return new DateTimeException(
      "ModernDigitalChronology attempted to create a date with invalid values in one or more fields"
    );
  }

  static UnsupportedOperationException noTimeOperationsError()
  {
    return new UnsupportedOperationException(
      "ModernDigitalChronology only implements datekeeping and can only work with purely date-related objects; use LocalTime for timekeeping"
    );
  }

  // Constructors
  private ModernDigitalChronology()
  {
    // Private no-arg constructor for Singleton instance, intentionally empty
  }

  // Override Methods
  /**
   * Gets the ID of this {@code Chronology}
   * @return A unique human-readable {@code String}, not null
   */
  @Override
  public String getId()
  {
    return ID;
  }

  /**
   * Gets the calendar type of this {@code Chronology}, a unique identifier
   * defined by the Unicode Locale Data Markup Language (LDML) specifications.
   * Given that this is a custom calendar, no such identifier exists
   * @return null
   */
  @Override
  public String getCalendarType()
  {
    return null;
  }

  /**
   * Gets the textual representation of this {@code Chronology} suitable for
   * presentation for the user. Always returns the same value regardless of
   * arguments, as is done by the calendars implemented in {@code java.time.chrono}
   * @param style The style of the text required; has no effect
   * @param locale The locale to use; has no effect
   * @return A {@code String} reading {@code "Modern Digital Calendar"}
   */
  @Override
  public String getDisplayName(TextStyle style, Locale locale)
  {
    // The Chronology implementations provided by java.time.chrono disregard
    // the Locale and TextStyle parameters of this method, always returning a
    // phrase such as "Buddhist Calendar". This method provides the same
    // incomplete implementation for consistency
    return DISPLAY_NAME;
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalDate#of(Era, int, int, int)}
   * @param era A {@link net.xmercerweiss.mdc.ModernDigitalEra}, not null
   * @param yearOfEra The year of the era, must be >= 0
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A new {@link net.xmercerweiss.mdc.ModernDigitalDate}
   * @throws DateTimeException If given an invalid date
   */
  @Override
  public ChronoLocalDate date(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    return ModernDigitalDate.of(era, yearOfEra, monthOfYear, dayOfMonth);
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalDate#of(int, int, int)}
   * @param prolepticYear The number of years before (< 0) or since (>= 0) 1970 ISO
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A new {@link net.xmercerweiss.mdc.ModernDigitalDate}
   * @throws DateTimeException If given an invalid date
   */
  @Override
  public ChronoLocalDate date(int prolepticYear, int monthOfYear, int dayOfMonth)
  {
    return ModernDigitalDate.of(prolepticYear, monthOfYear, dayOfMonth);
  }

  /**
   * Creates a new {@link net.xmercerweiss.mdc.ModernDigitalDate} representing an
   * ordinal day of the given year and era
   * @param era A {@link net.xmercerweiss.mdc.ModernDigitalEra}, not null
   * @param yearOfEra The year of the era, must be >= 0
   * @param dayOfYear The day of the year, within [1, 366]
   * @return A new {@link net.xmercerweiss.mdc.ModernDigitalDate}
   * @throws DateTimeException If given an invalid date
   */
  @Override
  public ChronoLocalDate dateYearDay(Era era, int yearOfEra, int dayOfYear)
  {
    return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalDate#ofYearDay(int, int)}
   * @param prolepticYear The number of years before (< 0) or since (>= 0) 1970 ISO
   * @param dayOfYear The day of the year, within [1, 366]
   * @return A new {@link net.xmercerweiss.mdc.ModernDigitalDate}
   * @throws DateTimeException If given an invalid date
   */
  @Override
  public ChronoLocalDate dateYearDay(int prolepticYear, int dayOfYear)
  {
    return ModernDigitalDate.ofYearDay(prolepticYear, dayOfYear);
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalDate#ofEpochDay(long)}
   * @param epochDay The number of days before (< 0) or since (>= 0) 1970-01-01 ISO
   * @return A new {@link net.xmercerweiss.mdc.ModernDigitalDate}
   */
  @Override
  public ChronoLocalDate dateEpochDay(long epochDay)
  {
    return ModernDigitalDate.ofEpochDay(epochDay);
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalDate#now()}
   * @return A new {@link net.xmercerweiss.mdc.ModernDigitalDate}
   */
  @Override
  public ChronoLocalDate dateNow()
  {
    return ModernDigitalDate.now();
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalDate#from(TemporalAccessor)}
   * @param temporal A {@link java.time.temporal.TemporalAccessor}, must be a non-null {@link java.time.chrono.ChronoLocalDate}
   * @return A new {@link net.xmercerweiss.mdc.ModernDigitalDate}
   * @throws UnsupportedOperationException If {@code temporal} isn't a {@link java.time.chrono.ChronoLocalDate}
   */
  @Override
  public ChronoLocalDate date(TemporalAccessor temporal)
  {
    return ModernDigitalDate.from(temporal);
  }

  /**
   * Determines whether the given proleptic year is a leap year
   * @param prolepticYear A number of years before (< 0) or since (>= 0) 1970 ISO
   * @return {@code true} if the given year is a leap year, {@code false} otherwise
   */
  @Override
  public boolean isLeapYear(long prolepticYear)
  {
    int isoYear = (int) Math.abs(prolepticYear + ISO_YEAR_OFFSET);
    return (isoYear % 400 == 0) || (isoYear % 100 != 0 && isoYear % 4 == 0);
  }

  /**
   * Calculates the number of years before (< 0) or since (>= 0) 1970 ISO using the era-year
   * @param era A {@link net.xmercerweiss.mdc.ModernDigitalEra}, not null
   * @param yearOfEra The year of the era, must be >= 0
   * @return The proleptic year
   * @throws DateTimeException If the given year or era is invalid
   */
  @Override
  public int prolepticYear(Era era, int yearOfEra)
  {
    ValueRange yearRange = range(YEAR_OF_ERA);
    if (
      yearRange.isValidValue(yearOfEra) &&
      era instanceof ModernDigitalEra mde
    )
      return switch (mde)
      {
        case BEFORE_EPOCH -> -1 * yearOfEra;
        case SINCE_EPOCH -> yearOfEra;
      };
    else throw invalidEraError();
  }

  /**
   * Determines the era of the given proleptic year
   * @param prolepticYear A number of years before (< 0) or since (>= 0) 1970 ISO
   * @return A {@link net.xmercerweiss.mdc.ModernDigitalEra}
   */
  @Override
  public Era eraOf(int prolepticYear)
  {
    return prolepticYear < 0 ?
      ModernDigitalEra.BEFORE_EPOCH : ModernDigitalEra.SINCE_EPOCH;
  }

  /**
   * @return An unmodifiable list of {@link net.xmercerweiss.mdc.ModernDigitalEra ModernDigitalEras}
   */
  @Override
  public List<Era> eras()
  {
    return Arrays.stream(ModernDigitalEra.values())
      .map(e -> (Era) e)
      .toList();
  }

  /**
   * Gets the range of valid values for a given {@link java.time.temporal.ChronoField} in the context of this {@code Chronology}
   * @param field A supported {@link java.time.temporal.ChronoField}, not null
   * @return A {@link java.time.temporal.ValueRange} of valid values, if the given field is supported
   */
  @Override
  public ValueRange range(ChronoField field)
  {
    return range((TemporalField) field);
  }

  /**
   * Equivalent to {@link java.time.Period#of(int, int, int)}
   * @param years A number of years, may be negative
   * @param months A number of months, may be negative
   * @param days A number of days, may be negative
   * @return A new {@link java.time.Period}
   */
  @Override
  public ChronoPeriod period(int years, int months, int days)
  {
    return Period.of(years, months, days);
  }

  /**
   * This {@code Chronology} does not support time-based methods; see class description
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public ChronoLocalDate dateNow(ZoneId zone)
  {
    throw noTimeOperationsError();
  }

  /**
   * This {@code Chronology} does not support time-based methods; see class description
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public ChronoLocalDate dateNow(Clock clock)
  {
    throw noTimeOperationsError();
  }

  /**
   * This {@code Chronology} does not support time-based methods; see class description
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public long epochSecond(int prolepticYear, int monthOfYear, int dayOfMonth, int hour, int minute, int second, ZoneOffset zoneOffset)
  {
    throw noTimeOperationsError();
  }

  /**
   * This {@code Chronology} does not support time-based methods; see class description
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public long epochSecond(Era era, int yearOfEra, int monthOfYear, int dayOfMonth, int hour, int minute, int second, ZoneOffset zoneOffset)
  {
    throw noTimeOperationsError();
  }

  /**
   * This {@code Chronology} does not support time-based methods; see class description
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor temporal)
  {
    throw noTimeOperationsError();
  }

  /**
   * This {@code Chronology} does not support time-based methods; see class description
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor temporal)
  {
    throw noTimeOperationsError();
  }

  /**
   * This {@code Chronology} does not support time-based methods; see class description
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant instant, ZoneId zone)
  {
    throw noTimeOperationsError();
  }

  // Public Methods
  /**
   * Determines whether the given {@link java.time.temporal.TemporalField} is supported by this {@code Chronology}
   * @param field A {@link java.time.temporal.TemporalField}, not null
   * @return {@code true} if dates of this {@code Chronology} may be prompted to return values of the given field
   */
  public boolean isSupported(TemporalField field)
  {
    return FIELD_TO_INTERNAL_FIELD.containsKey(field);
  }

  /**
   * Determines whether the given {@link java.time.temporal.TemporalUnit} is supported by this {@code Chronology}
   * @param unit A {@link java.time.temporal.TemporalUnit}, not null
   * @return {@code true} if dates of this {@code Chronology} may be prompted to return values of the given unit
   */
  public boolean isSupported(TemporalUnit unit)
  {
    return UNIT_TO_INTERNAL_FIELD.containsKey(unit);
  }

  /**
   * Obtains a {@link java.time.temporal.ValueRange} of valid values for the given field, if supported
   * @param field A {@link java.time.temporal.TemporalField}, must be {@link net.xmercerweiss.mdc.ModernDigitalChronology#isSupported supported}
   *              and not null
   * @return A {@link java.time.temporal.ValueRange}
   * @throws UnsupportedTemporalTypeException If given an unsupported field
   */
  public ValueRange range(TemporalField field)
  {
    if (isSupported(field))
      return switch (toInternalField(field))
      {
        case ERA -> ValueRange.of(0, 1);
        case YEAR_OF_ERA -> ValueRange.of(0, Integer.MAX_VALUE);
        case YEAR -> ValueRange.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
        case MONTH_OF_YEAR -> ValueRange.of(0, NON_LEAP_MONTHS_PER_YEAR);
        // Leap days belong to "month 0" of year
        case ALIGNED_WEEK_OF_YEAR -> ValueRange.of(0, WEEKS_PER_YEAR);
        // Leap days belong to "week 0" of year
        case DAY_OF_YEAR -> ValueRange.of(1, DAYS_PER_COMMON_YEAR, DAYS_PER_LEAP_YEAR);
        case DAY_OF_MONTH -> ValueRange.of(1, 2, DAYS_PER_MONTH);
        // The "0th" month (the non-month to which leap days belong) has a max of 2 days
        case DAY_OF_WEEK -> ValueRange.of(0, DAYS_PER_WEEK);
        // Leap days belong to "day 0" of week
        case EPOCH_DAY -> ValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE);
        default -> throw invalidFieldError();
      };
    else throw invalidFieldError();
  }

  /**
   * Calculates the epoch day of the given date
   * @param era A {@link net.xmercerweiss.mdc.ModernDigitalEra}, not null
   * @param yearOfEra The year of the era, must be >= 0
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A signed 64-bit integer number of days before (< 0) or since (>= 0) the epoch
   * @throws DateTimeException If the given year, month, or day is invalid
   */
  public long epochDay(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    return epochDay(prolepticYear(era, yearOfEra), monthOfYear, dayOfMonth);
  }

  /**
   * Calculates the epoch day of the given date
   * @param prolepticYear The number of years before (< 0) or since (>= 0) 1970 ISO
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A signed 64-bit integer number of days before (< 0) or since (>= 0) the epoch
   * @throws DateTimeException If the given month or day is invalid
   */
  public long epochDay(int prolepticYear, int monthOfYear, int dayOfMonth)
  {
    int isoYear = prolepticYear + ISO_YEAR_OFFSET;
    int dayOfYear = ordinalDayOfYear(monthOfYear, dayOfMonth);
    LocalDate isoDate = LocalDate.ofYearDay(isoYear, dayOfYear);
    return isoDate.toEpochDay();
  }

  /**
   * Calculates the ordinal (1st, 2nd, 3rd...) day of the year from a month and day
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A signed 32-bit integer within [1, 366]
   * @throws DateTimeException If the given month or day is invalid
   */
  public int ordinalDayOfYear(int monthOfYear, int dayOfMonth)
  {
    ValueRange monthRange = range(MONTH_OF_YEAR);
    ValueRange dayRange = range(DAY_OF_MONTH);
    if (!monthRange.isValidValue(monthOfYear) || !dayRange.isValidValue(dayOfMonth))
      throw invalidDateError();
    if (monthOfYear == 0 && dayOfMonth <= dayRange.getSmallestMaximum())
      return NON_LEAP_DAYS_PER_YEAR + dayOfMonth;
    else if (monthOfYear > 0)
      return ((monthOfYear - 1) * DAYS_PER_MONTH) + dayOfMonth;
    else throw invalidDateError();
  }

  /**
   * Calculates the ordinal (1st, 2nd, 3rd...) value of the weekday to which a given
   * day of a given month belongs
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A signed 32-bit integer within [0, 7], 0 for leap days as they belong to no weekday
   * @throws DateTimeException If the given month or day is invalid
   */
  public int ordinalDayOfWeek(int monthOfYear, int dayOfMonth)
  {
    int dayOfYear = ordinalDayOfYear(monthOfYear, dayOfMonth);
    return ordinalDayOfWeek(dayOfYear);
  }

  /**
   * Calculates the ordinal (1st, 2nd, 3rd...) value of the weekday to which a given
   * day belongs
   * @param dayOfYear The day of the year, within [1, 366]
   * @return A signed 32-bit integer within [0, 7], 0 for leap days as they belong to no weekday
   * @throws DateTimeException If the given day is invalid
   */
  public int ordinalDayOfWeek(int dayOfYear)
  {
    ValueRange dayOfYearRange = range(DAY_OF_YEAR);
    if (!dayOfYearRange.isValidValue(dayOfYear))
      throw invalidDateError();
    else return dayOfYear > NON_LEAP_DAYS_PER_YEAR ?
      0 : ((dayOfYear - 1) % DAYS_PER_WEEK) + 1;
  }

  /**
   * Calculates the ordinal (1st, 2nd, 3rd...) week of the year from a month and day
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A signed 32-bit integer within [0, 52], 0 for leap days as they belong to no week
   * @throws DateTimeException If the given month or day is invalid
   */
  public int ordinalWeekOfYear(int monthOfYear, int dayOfMonth)
  {
    return ordinalWeekOfYear(
      ordinalDayOfYear(monthOfYear, dayOfMonth)
    );
  }

  /**
   * Calculates the ordinal (1st, 2nd, 3rd...) week of the year from a day of the year
   * @param dayOfYear The day of the year, within [1, 366]
   * @return A signed 32-bit integer within [0, 52], 0 for leap days as they belong to no week
   * @throws DateTimeException If the given day is invalid
   */
  public int ordinalWeekOfYear(int dayOfYear)
  {
    ValueRange dayOfYearRange = range(DAY_OF_YEAR);
    if (!dayOfYearRange.isValidValue(dayOfYear))
      throw invalidDateError();
    else if (dayOfYear > NON_LEAP_DAYS_PER_YEAR)
      return 0;
    else
      return 1 + ((dayOfYear - 1) / DAYS_PER_WEEK);
  }

  /**
   * Converts a proleptic year to the year of its respective era
   * @param prolepticYear A number of years before (< 0) or since (>= 0) 1970 ISO
   * @return A signed 32-bit integer within [0, infinity)
   */
  public int prolepticToEraYear(int prolepticYear)
  {
    return Math.abs(prolepticYear);
  }

  // Package Private Methods
  TemporalField toInternalField(TemporalField field)
  {
    TemporalField internal = FIELD_TO_INTERNAL_FIELD.get(field);
    if (internal == null)
      throw invalidFieldError();
    else return internal;
  }

  TemporalField toInternalField(TemporalUnit unit)
  {

    TemporalField internal = UNIT_TO_INTERNAL_FIELD.get(unit);
    if (internal == null)
      throw invalidFieldError();
    else return internal;
  }

}
