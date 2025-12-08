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

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.time.*;
import java.time.chrono.*;
import java.time.temporal.*;
import java.time.format.*;
import static java.util.Map.entry;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.*;

import net.xmercerweiss.mdc.utils.*;
import static net.xmercerweiss.mdc.CustomField.*;


/**
 * Represents a specific date within the <a href="https://github.com/xmercerweiss/Modern-Digital-Calendar/blob/main/README.md">Modern Digital Calendar system</a>
 * <br><br>
 * Note that attempting to work with time-based values using this class is an error; see {@link net.xmercerweiss.mdc.ModernDigitalChronology}
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-08
 */
public class ModernDigitalDate
  implements ChronoLocalDate, Serializable
{
  // Class Constants
  private static final ModernDigitalChronology CHRONO = ModernDigitalChronology.INSTANCE;

  private static final char TOGGLE_NON_FMT_CHAR = '\'';
  private static final char TERM_CHAR = ')';
  private static final char START_IGNORE_CHAR = '[';
  private static final char STOP_IGNORE_CHAR = ']';

  private static final String VALUE_META_FMT = "%%0%sd";
  private static final String DISPLAY_FMT = "'%s' G y-MM-dd".formatted(CHRONO.getId());

  private static final Map<String,TemporalField> FIELD_NAME_TO_FIELD = Map.ofEntries(
    entry("Era", ERA),
    entry("Year", YEAR),
    entry("YearOfEra", YEAR_OF_ERA),
    entry("DayOfYear", DAY_OF_YEAR),
    entry("MonthOfYear", MONTH_OF_YEAR),
    entry("DayOfMonth", DAY_OF_MONTH),
    entry("ModifiedJulianDay", MODIFIED_JULIAN_DAY),
    entry("QuarterOfYear", QUARTER_OF_YEAR),
    entry("WeekOfWeekBasedYear", ALIGNED_WEEK_OF_YEAR),
    entry("WeekOfMonth", ALIGNED_WEEK_OF_MONTH),
    entry("AlignedWeekOfMonth", ALIGNED_WEEK_OF_MONTH),
    entry("DayOfWeek", DAY_OF_WEEK)
  );

  private static final Map<Long,String> MONTH_VALUE_TO_FULL_NAME = Map.ofEntries(
    entry(0L, "Leap"),
    entry(1L, "Unitary"),
    entry(2L, "Duotary"),
    entry(3L, "Tertiary"),
    entry(4L, "Quartuary"),
    entry(5L, "Pentuary"),
    entry(6L, "Hextuary"),
    entry(7L, "September"),
    entry(8L, "October"),
    entry(9L, "November"),
    entry(10L, "December"),
    entry(11L, "Hendecember"),
    entry(12L, "Dodecember"),
    entry(13L, "Tredecember")
  );

  private static final Map<Long,String> MONTH_VALUE_TO_SHORT_NAME = Map.ofEntries(
    entry(0L, "Leap"),
    entry(1L, "Uni"),
    entry(2L, "Duo"),
    entry(3L, "Ter"),
    entry(4L, "Qua"),
    entry(5L, "Pen"),
    entry(6L, "Hex"),
    entry(7L, "Sep"),
    entry(8L, "Oct"),
    entry(9L, "Nov"),
    entry(10L, "Dec"),
    entry(11L, "Hen"),
    entry(12L, "Dod"),
    entry(13L, "Tred")
  );

  private static final Map<Long,String> WEEKDAY_VALUE_TO_NAME = Map.ofEntries(
    entry(0L, "None"),
    entry(1L, "Monday"),
    entry(2L, "Tuesday"),
    entry(3L, "Wednesday"),
    entry(4L, "Thursday"),
    entry(5L, "Friday"),
    entry(6L, "Saturday"),
    entry(7L, "Sunday")
  );

  private static final Map<Long,String> QUARTER_VALUE_TO_FULL_NAME = Map.ofEntries(
    entry(0L, "No quarter"),
    entry(1L, "1st quarter"),
    entry(2L, "2nd quarter"),
    entry(3L, "3rd quarter"),
    entry(4L, "4th quarter")
  );

  private static final Map<Long,String> QUARTER_VALUE_TO_SHORT_NAME = Map.ofEntries(
    entry(0L, "Q0"),
    entry(1L, "Q1"),
    entry(2L, "Q2"),
    entry(3L, "Q3"),
    entry(4L, "Q4")
  );

  private static final Set<String> IGNORED_ARG_IDS = Set.of(
    "ParseCaseSensitive"
  );

  // Error Methods
  private static UnsupportedOperationException invalidAdjustmentError()
  {
    return new UnsupportedOperationException(
      "ModernDigitalDates do not support the use of .with() or TemporalAdjusters"
    );
  }
  private static UnsupportedOperationException invalidQueryError()
  {
    return new UnsupportedOperationException(
      "ModernDigitalDate given invalid TemporalQuery; only pass date-only queries from TemporalQueries"
    );
  }

  private static IllegalArgumentException invalidFormatError()
  {
    return new IllegalArgumentException(
      "ModernDigitalDate attempted to parse invalid date format"
    );
  }

  private static IllegalArgumentException invalidTemporalError()
  {
    return new IllegalArgumentException(
      "ModernDigitalDate passed non-ChronoLocalDate temporal object; all temporal objects must be dates"
    );
  }

  private static IllegalArgumentException invalidAmountError()
  {
    return new IllegalArgumentException(
      "ModernDigitalDate passed TemporalAmount other than ChronoPeriod; can only accept date-based amounts of time"
    );
  }

  // Static Methods
  /**
   * Creates a new {@code ModernDigitalDate} instance representing the specified date
   * @param era A {@link net.xmercerweiss.mdc.ModernDigitalEra}, not null
   * @param yearOfEra The year of the era, must be >= 0
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A new {@code ModernDigitalDate}
   * @throws DateTimeException If given an invalid date
   */
  public static ModernDigitalDate of(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    return new ModernDigitalDate(era, yearOfEra, monthOfYear, dayOfMonth);
  }

  /**
   * Creates a new {@code ModernDigitalDate} instance representing the specified date
   * @param prolepticYear The number of years before (< 0) or since (>= 0) 1970 ISO
   * @param monthOfYear The month of the year, 0 for leap days
   * @param dayOfMonth The day of the month, within [1, 28]
   * @return A new {@code ModernDigitalDate}
   * @throws DateTimeException If given an invalid date
   */
  public static ModernDigitalDate of(int prolepticYear, int monthOfYear, int dayOfMonth)
  {
    Era era = CHRONO.eraOf(prolepticYear);
    int yearOfEra = CHRONO.prolepticToEraYear(prolepticYear);
    return ModernDigitalDate.of(era, yearOfEra, monthOfYear, dayOfMonth);
  }

  /**
   * Creates a new {@code ModernDigitalDate} instance representing the given ordinal (1st, 2nd, 3rd...) day of the specified proleptic year
   * @param prolepticYear The number of years before (< 0) or since (>= 0) 1970 ISO
   * @param dayOfYear The ordinal day of the year
   * @return A new {@code ModernDigitalDate}
   * @throws DateTimeException If given an invalid date
   */
  public static ModernDigitalDate ofYearDay(int prolepticYear, int dayOfYear)
  {
    int monthOfYear = 1 + (dayOfYear / CHRONO.DAYS_PER_MONTH);
    int dayOfMonth = dayOfYear % CHRONO.DAYS_PER_MONTH;
    return ModernDigitalDate.of(prolepticYear, monthOfYear, dayOfMonth);
  }

  /**
   * Creates a new {@code ModernDigitalDate} instance representing the date occurring exactly a given number of days
   * before or since 1970-01-01 ISO
   * @param epochDay The number of days before (< 0) or since (>= 0) 1970-01-01 ISO
   * @return A new {@code ModernDigitalDate}
   */
  public static ModernDigitalDate ofEpochDay(long epochDay)
  {
    return new ModernDigitalDate(epochDay);
  }

  /**
   * Creates a new {@code ModernDigitalDate} instance representing the current date at runtime
   * @return A new {@code ModernDigitalDate}
   */
  public static ModernDigitalDate now()
  {
    LocalDate today = LocalDate.now();
    return ModernDigitalDate.ofEpochDay(today.toEpochDay());
  }

  /**
   * Creates a new {@code ModernDigitalDate} instance representing the same date as the given {@link java.time.chrono.ChronoLocalDate}
   * @param temporal A {@link java.time.temporal.TemporalAccessor}, must be a non-null {@link java.time.chrono.ChronoLocalDate}
   * @return A new {@code ModernDigitalDate}
   */
  public static ModernDigitalDate from(TemporalAccessor temporal)
  {
    if (temporal instanceof ChronoLocalDate date)
      return ModernDigitalDate.ofEpochDay(date.toEpochDay());
    else
      throw CHRONO.noTimeOperationsError();
  }

  // Instance Fields
  private final Era ERA_ENUM;
  private final Map<TemporalField,Long> FIELDS;

  private final Map<String, BiFunction<TemporalField,String[],String>>
    ARG_ID_TO_RENDERER = Map.ofEntries(
      entry("Text", this::renderFormattedText),
      entry("Localized", this::renderFormattedValue),
      entry("Value", this::renderFormattedValue),
      entry("ReducedValue", this::renderFormattedValue)
    );


  // Constructors
  private ModernDigitalDate(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    validateFields(era, yearOfEra, monthOfYear, dayOfMonth);
    ERA_ENUM = yearOfEra == 0 ? ModernDigitalEra.SINCE_EPOCH : era;
    long weekOfYear = CHRONO.ordinalWeekOfYear(monthOfYear, dayOfMonth);
    FIELDS = Map.ofEntries(
      entry(
        ERA,
        ERA_ENUM.getLong(ERA)
      ),
      entry(
        YEAR_OF_ERA,
        (long) yearOfEra
      ),
      entry(
        YEAR,
        (long) CHRONO.prolepticYear(ERA_ENUM, yearOfEra)
      ),
      entry(
        MONTH_OF_YEAR,
        (long) monthOfYear
      ),
      entry(
        ALIGNED_WEEK_OF_YEAR,
        weekOfYear
      ),
      entry(
        ALIGNED_WEEK_OF_MONTH,
        ((weekOfYear - 1) % CHRONO.WEEKS_PER_MONTH) + 1
      ),
      entry(
        DAY_OF_YEAR,
        (long) CHRONO.ordinalDayOfYear(monthOfYear, dayOfMonth)
      ),
      entry(
        DAY_OF_MONTH,
        (long) dayOfMonth
      ),
      entry(
        DAY_OF_WEEK,
        (long) CHRONO.ordinalDayOfWeek(monthOfYear, dayOfMonth)
      ),
      entry(
        EPOCH_DAY,
        CHRONO.epochDay(ERA_ENUM, yearOfEra, monthOfYear, dayOfMonth)
      )
    );
  }

  private ModernDigitalDate(long epochDay)
  {
    HashMap<TemporalField,Long> calculatedFields = epochDayToFields(epochDay);
    int prolepticYear = Math.toIntExact(calculatedFields.get(YEAR));
    int monthOfYear = Math.toIntExact(calculatedFields.get(MONTH_OF_YEAR));
    int dayOfMonth = Math.toIntExact(calculatedFields.get(DAY_OF_MONTH));
    long weekOfYear = CHRONO.ordinalWeekOfYear(monthOfYear, dayOfMonth);
    ERA_ENUM = CHRONO.eraOf(prolepticYear);
    calculatedFields.put(
      ERA,
      ERA_ENUM.getLong(ERA)
    );
    calculatedFields.put(
      YEAR_OF_ERA,
      (long) CHRONO.prolepticToEraYear(prolepticYear)
    );
    calculatedFields.put(
      ALIGNED_WEEK_OF_YEAR,
      weekOfYear
    );
    calculatedFields.put(
      ALIGNED_WEEK_OF_MONTH,
      ((weekOfYear - 1) % CHRONO.WEEKS_PER_MONTH) + 1
    );
    calculatedFields.put(
      DAY_OF_WEEK,
      (long) CHRONO.ordinalDayOfWeek(monthOfYear, dayOfMonth)
    );
    FIELDS = Map.copyOf(calculatedFields);
  }

  // Override Methods
  /**
   * Determines whether the given {@link java.time.temporal.TemporalField} is supported by this {@code Date}
   * @param field A {@link java.time.temporal.TemporalField}, not null
   * @return {@code true} if this {@code Date} may be prompted to return a value of the given field
   */
  @Override
  public boolean isSupported(TemporalField field)
  {
    return CHRONO.isSupported(field);
  }

  /**
   * Determines whether the given {@link java.time.temporal.TemporalUnit} is supported by this {@code Date}
   * @param unit A {@link java.time.temporal.TemporalUnit}, not null
   * @return {@code true} if this {@code Date} may be prompted to return a value of the given unit
   */
  @Override
  public boolean isSupported(TemporalUnit unit)
  {
    return FIELDS.containsKey(
      CHRONO.toInternalField(unit)
    );
  }

  /**
   * Compares this {@code Date} to another {@link java.time.chrono.ChronoLocalDate Date}
   * @param that A {@link java.time.chrono.ChronoLocalDate}, not null
   * @return {@code true} if this {@code Date} comes after the given date
   */
  @Override
  public boolean isAfter(ChronoLocalDate that)
  {
    return this.toEpochDay() > that.toEpochDay();
  }

  /**
   * Compares this {@code Date} to another {@link java.time.chrono.ChronoLocalDate Date}
   * @param that A {@link java.time.chrono.ChronoLocalDate}, not null
   * @return {@code true} if this {@code Date} is equal to the given date
   */
  @Override
  public boolean isEqual(ChronoLocalDate that)
  {
    return this.toEpochDay() == that.toEpochDay();
  }

  /**
   * Compares this {@code Date} to another {@link java.time.chrono.ChronoLocalDate Date}
   * @param that A {@link java.time.chrono.ChronoLocalDate}, not null
   * @return {@code true} if this {@code Date} comes before the given date
   */
  @Override
  public boolean isBefore(ChronoLocalDate that)
  {
    return this.toEpochDay() < that.toEpochDay();
  }

  /**
   * Determines whether this {@code Date} falls on a leap year
   * @return {@code true} if this date occurs within a leap year
   */
  @Override
  public boolean isLeapYear()
  {
    return CHRONO.isLeapYear(FIELDS.get(YEAR));
  }

  /**
   * Gets the {@code Chronology} of this {@code Date}, the Modern Digital Calendar system
   * @return The singleton instance of {@link net.xmercerweiss.mdc.ModernDigitalChronology}
   */
  @Override
  public Chronology getChronology()
  {
    return CHRONO;
  }

  /**
   * Gets the era this {@code Date} occurs within
   * @return A {@link net.xmercerweiss.mdc.ModernDigitalEra}
   */
  @Override
  public Era getEra()
  {
    return ERA_ENUM;
  }

  /**
   * Obtains the value of a given {@link java.time.temporal.TemporalField} within this {@code Date}, if supported
   * @param field A {@link java.time.temporal.TemporalField}, must be supported and non-null
   * @return The value of the given field for this {@code Date} as a signed 32-bit integer
   * @throws UnsupportedTemporalTypeException If given an unsupported field
   */
  @Override
  public int get(TemporalField field)
  {
    return (int) getLong(field);
  }

  /**
   * Obtains the value of a given {@link java.time.temporal.TemporalField} within this {@code Date} as a long, if supported
   * @param field A {@link java.time.temporal.TemporalField}, must be supported and non-null
   * @return The value of the given field for this {@code Date} as a signed 64-bit integer
   * @throws UnsupportedTemporalTypeException If given an unsupported field
   */
  @Override
  public long getLong(TemporalField field)
  {
    if (isSupported(field))
      return FIELDS.get(field);
    else throw CHRONO.invalidFieldError();
  }

  /**
   * Obtains the difference, in days, between this {@code Date} and 1970-01-01 ISO
   * @return A signed 64-bit integer, < 0 if this date comes before the epoch, > 0 if this date comes after the epoch,
   *         and 0 if this date <em>is</em> the epoch
   */
  @Override
  public long toEpochDay()
  {
    return FIELDS.get(EPOCH_DAY);
  }

  /**
   * Obtains the number of days of the month this {@code Date} falls within. Given the nature of
   * the Modern Digital Calendar, this value will always be either 28 or the number of leap days within the year of this
   * date
   * @return A signed 32-bit integer, always either 28 <em>or</em> the number of leap days in the year of this date
   */
  @Override
  public int lengthOfMonth()
  {
    int month = getMonth();
    if (month == 0)
      return lengthOfYear() - CHRONO.NON_LEAP_DAYS_PER_YEAR;
    else
      return CHRONO.DAYS_PER_MONTH;
  }

  /**
   * Obtains the number of days in the year of this date
   * @return A signed 32-bit integer; always either 365 or 366
   */
  @Override
  public int lengthOfYear()
  {
    return isLeapYear() ?
      CHRONO.DAYS_PER_LEAP_YEAR : CHRONO.DAYS_PER_COMMON_YEAR;
  }

  /**
   * Equivalent to {@link net.xmercerweiss.mdc.ModernDigitalChronology#range(TemporalField)}
   * @param field A {@link java.time.temporal.TemporalField}, must be
   *              {@link net.xmercerweiss.mdc.ModernDigitalChronology#isSupported supported} and not null
   * @return A {@link java.time.temporal.ValueRange}
   * @throws UnsupportedTemporalTypeException If given an unsupported field
   */
  @Override
  public ValueRange range(TemporalField field)
  {
    return CHRONO.range(field);
  }

  /**
   * <strong>NOTE:</strong> This method may return inconsistent results. Notes discussing this can be found at {@code docs/MISC.md#ChronoPeriods}
   * <br><br>
   * Returns a new {@link ModernDigitalDate} instance representing this {@code Date}
   * plus a given {@link java.time.chrono.ChronoPeriod} of time
   * @param amount A {@link java.time.chrono.ChronoPeriod}, not null
   * @return A new {@link ModernDigitalDate} instance
   * @throws NullPointerException If given a period of null
   */
  @Override
  public ChronoLocalDate plus(TemporalAmount amount)
  {
    if (amount instanceof ChronoPeriod period)
    {
      return calculateDateAfter(period);
    }
    else
      throw invalidAmountError();
  }

  /**
   * <strong>NOTE:</strong> This method may return inconsistent results. Notes discussing this can be found at {@code docs/MISC.md#ChronoPeriods}
   * <br><br>
   * Returns a new {@link ModernDigitalDate} instance representing this {@code Date}
   * plus a given number of a given {@link java.time.temporal.TemporalUnit}
   * @param value A signed 64-bit (truncated to 32-bit) integer number of units
   * @param unit Any {@link java.time.temporal.ChronoUnit} greater than or equal to {@code DAYS}, except {@code FOREVER}; not null
   * @return A new {@link ModernDigitalDate} instance
   * @throws UnsupportedTemporalTypeException If given a null or invalid unit
   */
  @Override
  public ChronoLocalDate plus(long value, TemporalUnit unit)
  {
    return plus(
      CHRONO.unitToPeriod(MathUtils.narrowLong(value), unit)
    );
  }

  /**
   * <strong>NOTE:</strong> This method may return inconsistent results. Notes discussing this can be found at {@code docs/MISC.md#ChronoPeriods}
   * <br><br>
   * Returns a new {@link ModernDigitalDate} instance representing this {@code Date}
   * minus a given {@link java.time.chrono.ChronoPeriod} of time
   * @param amount A {@link java.time.chrono.ChronoPeriod}, not null
   * @return A new {@link ModernDigitalDate} instance
   * @throws NullPointerException If given a period of null
   */
  @Override
  public ChronoLocalDate minus(TemporalAmount amount)
  {
    if (amount instanceof ChronoPeriod period)
    {
      return calculateDateAfter(period.multipliedBy(-1));
    }
    else
      throw invalidAmountError();
  }

  /**
   * <strong>NOTE:</strong> This method may return inconsistent results. Notes discussing this can be found at {@code docs/MISC.md#ChronoPeriods}
   * <br><br>
   * Returns a new {@link ModernDigitalDate} instance representing this {@code Date}
   * minus a given number of a given {@link java.time.temporal.TemporalUnit}
   * @param value A signed 64-bit (truncated to 32-bit) integer number of units
   * @param unit Any {@link java.time.temporal.ChronoUnit} greater than or equal to {@code DAYS}, except {@code FOREVER}; not null
   * @return A new {@link ModernDigitalDate} instance
   * @throws UnsupportedTemporalTypeException If given a null or invalid unit
   */
  @Override
  public ChronoLocalDate minus(long value, TemporalUnit unit)
  {
    return minus(
      CHRONO.unitToPeriod(MathUtils.narrowLong(value), unit)
    );
  }

  /**
   * <strong>NOTE:</strong> This method may return inconsistent results. Notes discussing this can be found at {@code docs/MISC.md#ChronoPeriods}
   * <br><br>
   * Equivalent to...
   * <pre>{@code
   * ModernDigitalChronology.periodToUnit(
   *   thisDate.until(temporal),
   *   unit
   * );
   * }</pre>
   * @param temporal Must be a valid {@link java.time.chrono.ChronoLocalDate}, not null
   * @param unit Any {@link java.time.temporal.ChronoUnit ChronoUnits} greater than or equal to {@code DAYS}, not null
   * @return A signed 64-bit integer number of the given unit
   * @throws NullPointerException If given a date of null
   * @throws UnsupportedTemporalTypeException If given a null or invalid unit
   */
  @Override
  public long until(Temporal temporal, TemporalUnit unit)
  {
    if (temporal instanceof ChronoLocalDate date)
    {
      ChronoPeriod period = until(date);
      return CHRONO.periodToUnit(period, unit);
    }
    else
      throw invalidTemporalError();
  }

  /**
   * <strong>NOTE:</strong> This method may return inconsistent results. Notes discussing this can be found at {@code docs/MISC.md#ChronoPeriods}
   * <br><br>
   * Calculates the difference between this {@code Date} and the given {@link java.time.chrono.ChronoLocalDate}
   * as a {@link java.time.chrono.ChronoPeriod}. Will be negative if the given date is before this {@code Date}
   * @param date A valid {@link java.time.chrono.ChronoLocalDate}, not null
   * @return A new {@link java.time.chrono.ChronoPeriod}
   * @throws NullPointerException If given a date of null
   */
  @Override
  public ChronoPeriod until(ChronoLocalDate date)
  {
    if (date == null)
      throw CHRONO.nullDateError();
    ModernDigitalDate that = date instanceof ModernDigitalDate ?
      (ModernDigitalDate) date : ModernDigitalDate.ofEpochDay(date.toEpochDay());
    ModernDigitalDate start = this.isBefore(that) ? this : that;
    ModernDigitalDate end = start == this ? that : this;
    int signum = start == this ? 1 : -1;
    Period correction = Period.ZERO;
    if (start.isLeapDay())
    {
      // Set start to the last non-leap day of the year
      // and store the difference to be subtracted later
      correction = correction.minusDays(start.getDayOfMonth());
      start = ModernDigitalDate.of(start.getYear(), 13, 28);
    }
    if (end.isLeapDay())
    {
      // Set end to the last non-leap day of the year
      // and store the difference to be added later
      correction = correction.plusDays(end.getDayOfMonth());
      end = ModernDigitalDate.of(end.getYear(), 13, 28);
    }
    return calculatePeriodBetween(start, end)
      .plus(correction) // Correct for leap days if necessary, see above
      .multipliedBy(signum); // if this date comes after the given date, invert the period
  }

  /**
   * Formats this date using the pattern of the given formatter
   * <br><br>
   * Note that calling {@code formatter.format(date)} will typically result in an error,
   * as this method <em>does not</em> actually utilize the given formatter, rather it simply
   * extracts its pattern and produces its output using its own implementation
   * @param formatter A {@link java.time.format.DateTimeFormatter}, not null
   * @return A {@code String} representation of this date matching the given formatter
   */
  @Override
  public String format(DateTimeFormatter formatter)
  {
    try
    {
      String fmt = formatter.toString();
      return formatterStringToOutput(fmt);
    }
    catch (Exception e)
    {
      throw invalidFormatError();
    }
  }

  /**
   * Only allows querying via date-only queries of {@link java.time.temporal.TemporalQueries},
   * <strong>use of this method is highly discouraged</strong>
   * @param query Must be {@link java.time.temporal.TemporalQueries#chronology()}
   *              or {@link java.time.temporal.TemporalQueries#localDate()}
   * @return The queried value
   * @throws UnsupportedOperationException If given an unsupported query
   */
  @Override
  public <R> R query(TemporalQuery<R> query)
  {
    try
    {
      if (query == TemporalQueries.chronology())
        return (R) CHRONO;
      else if (query == TemporalQueries.localDate())
        return (R) LocalDate.ofEpochDay(this.toEpochDay());
      else
        throw invalidQueryError();
    }
    catch (ClassCastException e)
    {
      throw invalidQueryError();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(ChronoLocalDate that)
  {
    if (that == null)
      throw CHRONO.nullDateError();
    else if (this.toEpochDay() == that.toEpochDay())
      return CHRONO == that.getChronology() ? 0 : -1;
    else
      return (int) (this.toEpochDay() - that.toEpochDay());
  }

  @Override
  public boolean equals(Object other)
  {
    if (other == null)
      return false;
    else if (other instanceof ModernDigitalDate that)
      return this.isEqual(that);
    else
      return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return format(DISPLAY_FMT);
  }

  /**
   * This {@code Date} does not support the use of .with() or TemporalAdjusters
   * @throws UnsupportedOperationException Always
   */
  @Override
  public ChronoLocalDate with(TemporalAdjuster adjuster)
  {
    throw invalidAdjustmentError();
  }

  /**
   * This {@code Date} does not support the use of .with()
   * @throws UnsupportedOperationException Always
   */
  @Override
  public ChronoLocalDate with(TemporalField field, long newValue)
  {
    throw invalidAdjustmentError();
  }

  /**
   * The {@code Chronology} of this {@code Date} does not support time-based methods; see {@link net.xmercerweiss.mdc.ModernDigitalChronology}
   * @throws UnsupportedOperationException Always
   */
  @Override
  public ChronoLocalDateTime<?> atTime(LocalTime localTime)
  {
    throw CHRONO.noTimeOperationsError();
  }

  // Public Methods
  /**
   * @return {@code true} if this date is a leap day
   */
  public boolean isLeapDay()
  {
    return getLong(MONTH_OF_YEAR) == 0;
  }

  /**
   * Gets the year of the era of this {@code Date}
   * @return A signed 32-bit integer within [0, infinity)
   */
  public int getYearOfEra()
  {
    return get(YEAR_OF_ERA);
  }

  /**
   * Gets the proleptic year of this {@code Date}
   * @return A signed 32-bit integer
   */
  public int getYear()
  {
    return get(YEAR);
  }

  /**
   * Gets the month of this {@code Date}
   * @return A signed 32-bit integer within [0, 13], 0 for leap days
   */
  public int getMonth()
  {
    return get(MONTH_OF_YEAR);
  }

  /**
   * Gets the ordinal (1st, 2nd, 3rd...) week of the year of this {@code Date}
   * @return A signed 32-bit integer within [0, 52], 0 for leap days
   */
  public int getWeekOfYear()
  {
    return get(ALIGNED_WEEK_OF_YEAR);
  }

  /**
   * Gets the ordinal (1st, 2nd, 3rd...) day of the year of this {@code Date}
   * @return A signed 32-bit integer within [1, 366]
   */
  public int getDayOfYear()
  {
    return get(DAY_OF_YEAR);
  }

  /**
   * Gets the day of the month of this {@code Date}
   * @return A signed 32-bit integer within [1, 28]
   */
  public int getDayOfMonth()
  {
    return get(DAY_OF_MONTH);
  }

  /**
   * Gets the numeric value for the day of the week of this {@code Date}
   * <br><br>
   * Following the conventions of ISO 8601, Monday has a value of 1 and Sunday has a value of 7
   * Leap days belong to no week and therefore have a value of 0
   * @return A signed 32-bit integer within [0, 7]
   */
  public int getDayOfWeek()
  {
    return get(DAY_OF_WEEK);
  }

  /**
   * Gets the numeric value for the quarter of the year of this {@code Date}
   * @return A signed 32-bit integer within [0, 4], 0 for leap days, which belong to no quarter
   */
  public int getQuarter()
  {
    int weekOfYear = getWeekOfYear();
    if (weekOfYear == 0)
      return 0;
    else
      return ((weekOfYear - 1) / CHRONO.WEEKS_PER_QUARTER) + 1;
  }

  /**
   * Formats this date using the given pattern
   * @param pattern A formatting pattern as defined by {@link java.time.format.DateTimeFormatter}
   * @return A {@code String} representation of this date matching the given pattern
   */
  public String format(String pattern)
  {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return format(formatter);
  }

  // Private Methods
  private void validateFields(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
      if (!(era instanceof ModernDigitalEra))
        throw CHRONO.invalidEraError();
      if (!range(YEAR_OF_ERA).isValidValue(yearOfEra))
        throw CHRONO.invalidDateError(yearOfEra, YEAR_OF_ERA);
      if (!range(MONTH_OF_YEAR).isValidValue(monthOfYear))
        throw CHRONO.invalidDateError(monthOfYear, MONTH_OF_YEAR);
      if (!range(DAY_OF_MONTH).isValidValue(dayOfMonth))
        throw CHRONO.invalidDateError(dayOfMonth, DAY_OF_MONTH);
      if ((monthOfYear == 0 && dayOfMonth > range(DAY_OF_MONTH).getSmallestMaximum()) ||
      (monthOfYear == 0 && dayOfMonth > 1 && !CHRONO.isLeapYear(CHRONO.prolepticYear(era, yearOfEra))))
        throw CHRONO.invalidDateError();
  }

  private HashMap<TemporalField,Long> epochDayToFields(long epochDay)
  {
    HashMap<TemporalField,Long> fields = new HashMap<>();
    LocalDate isoDate = LocalDate.ofEpochDay(epochDay);
    long prolepticYear = isoDate.getYear() - CHRONO.ISO_YEAR_OFFSET;
    long dayOfYear = isoDate.getDayOfYear();
    long monthOfYear = dayOfYear > CHRONO.NON_LEAP_DAYS_PER_YEAR ? 0 :
      1 + ((dayOfYear - 1) / CHRONO.DAYS_PER_MONTH);
    long dayOfMonth = monthOfYear == 0 ? dayOfYear - CHRONO.NON_LEAP_DAYS_PER_YEAR :
      1 + ((dayOfYear - 1) % CHRONO.DAYS_PER_MONTH);
    fields.put(EPOCH_DAY, epochDay);
    fields.put(YEAR, prolepticYear);
    fields.put(DAY_OF_YEAR, dayOfYear);
    fields.put(MONTH_OF_YEAR, monthOfYear);
    fields.put(DAY_OF_MONTH, dayOfMonth);
    return fields;
  }

  private Period calculatePeriodBetween(ModernDigitalDate start, ModernDigitalDate end)
  {
    int totalMonths = CHRONO.MONTHS_PER_YEAR * (end.getYear() - start.getYear());
    totalMonths += end.getMonth() - start.getMonth();
    int days = end.getDayOfMonth() - start.getDayOfMonth();
    if (days < 0)
    {
      totalMonths -= 1;
      days += CHRONO.DAYS_PER_MONTH;
      if (end.getMonth() == 1)
        days += CHRONO.isLeapYear(end.getYear() - 1) ? 2 : 1;
    }
    return Period.of(
      totalMonths / CHRONO.MONTHS_PER_YEAR,
      totalMonths % CHRONO.MONTHS_PER_YEAR,
      days
    );
  }

  private ModernDigitalDate calculateDateAfter(ChronoPeriod period)
  {
    if (period == null)
      throw CHRONO.nullPeriodError();
    int monthOfYear = getMonth();
    int dayOfMonth = getDayOfMonth();
    int correction = 0;
    if (monthOfYear == 0)
    {
      correction += dayOfMonth;
      dayOfMonth = 28;
      monthOfYear = 13;
    }
    long totalMonths = CHRONO.MONTHS_PER_YEAR * period.get(YEARS)
      + period.get(MONTHS) + monthOfYear;
    int year = getYear();
    int month;
    if (totalMonths <= 0)
    {
      year += (int) (totalMonths / CHRONO.MONTHS_PER_YEAR) - 1;
      month = (int) (totalMonths % CHRONO.MONTHS_PER_YEAR) + CHRONO.MONTHS_PER_YEAR;
    }
    else
    {
      year += (int) (totalMonths - 1) / CHRONO.MONTHS_PER_YEAR;
      month = (int) ((totalMonths - 1) % CHRONO.MONTHS_PER_YEAR) + 1;
    }
    return ModernDigitalDate.ofEpochDay(
      CHRONO.epochDay(year, month, dayOfMonth)
        + period.get(DAYS)
        + correction
    );
  }

  private String formatterStringToOutput(String fmt)
  {
    StringBuilder out = new StringBuilder();
    StringBuilder arg = new StringBuilder();
    boolean nonFormat = false;
    boolean ignore = false;
    for (char c : fmt.toCharArray())
      if (c == START_IGNORE_CHAR)
        ignore = true;
      else if (c == STOP_IGNORE_CHAR)
        ignore = false;
      else if (ignore)
        continue;
      else if (c == TOGGLE_NON_FMT_CHAR)
        nonFormat = !nonFormat;
      else if (nonFormat)
        out.append(c);
      else
      {
        arg.append(c);
        if (c == TERM_CHAR)
        {
          String rendered = renderFormatArg(arg.toString());
          arg.delete(0, arg.length());
          out.append(rendered);
        }
      }
    return out.toString();
  }

  private String renderFormatArg(String arg)
  {
    for (
      Entry<String,BiFunction<TemporalField,String[],String>> pair :
      ARG_ID_TO_RENDERER.entrySet()
    ) {
      String argId = pair.getKey();
      BiFunction<TemporalField,String[],String> renderer = pair.getValue();
      if (arg.startsWith(argId))
      {
        String[] settings = arg.substring(
          argId.length() + 1,
          arg.length() - 1
        ).split(",");
        TemporalField field = FIELD_NAME_TO_FIELD.get(settings[0]);
        if (field == null)
          throw invalidFormatError();
        settings = Arrays.copyOfRange(settings, 1, settings.length);
        return renderer.apply(field, settings);
      }
    }
    for (String ignored : IGNORED_ARG_IDS)
      if (arg.startsWith(ignored))
        return "";
    throw invalidFormatError();
  }

  private String renderFormattedText(TemporalField field, String... settings)
  {
    long fieldValue = getFormatFieldValue(field);
    TextStyle style = TextStyle.FULL;
    try
    {
      style = TextStyle.valueOf(settings[0]);
    }
    catch (Exception _) {}
    return switch (field)
    {
      case ERA -> ERA_ENUM.getDisplayName(style, null);
      case QUARTER_OF_YEAR -> getStyledQuarterName(fieldValue, style);
      case MONTH_OF_YEAR -> getStyledMonthName(fieldValue, style);
      case DAY_OF_WEEK -> getStyledWeekdayName(fieldValue, style);
      default -> throw invalidFormatError();
    };
  }

  private String renderFormattedValue(TemporalField field, String... settings)
  {
    long fieldValue = getFormatFieldValue(field);
    int fieldWidth = -1;
    try
    {
      fieldWidth = Integer.parseInt(settings[0]);
    }
    catch (Exception _) {}
    if (fieldWidth <= 0)
      return String.valueOf(fieldValue);
    else
      return VALUE_META_FMT
        .formatted(fieldWidth)
        .formatted(fieldValue);
  }

  private long getFormatFieldValue(TemporalField field)
  {
    if (isSupported(field))
      return getLong(field);
    else return switch (field)
    {
      case MODIFIED_JULIAN_DAY -> toEpochDay() + CHRONO.MJD_EPOCH_OFFSET_IN_DAYS;
      case QUARTER_OF_YEAR -> getQuarter();
      default -> throw invalidFormatError();
    };
  }

  private String getStyledMonthName(long value, TextStyle style)
  {
    return switch (style)
    {
      case NARROW, NARROW_STANDALONE -> MONTH_VALUE_TO_SHORT_NAME.get(value).substring(0, 1);
      case SHORT, SHORT_STANDALONE -> MONTH_VALUE_TO_SHORT_NAME.get(value);
      default -> MONTH_VALUE_TO_FULL_NAME.get(value);
    };
  }

  private String getStyledWeekdayName(long value, TextStyle style)
  {
    String fullName = WEEKDAY_VALUE_TO_NAME.get(value);
    return switch (style)
    {
      case FULL, FULL_STANDALONE -> fullName;
      case SHORT, SHORT_STANDALONE -> fullName.substring(0, 3);
      case NARROW, NARROW_STANDALONE -> fullName.substring(0, 1);
    };
  }

  private String getStyledQuarterName(long value, TextStyle style)
  {
    return switch (style)
    {
      case FULL, FULL_STANDALONE -> QUARTER_VALUE_TO_FULL_NAME.get(value);
      case SHORT, SHORT_STANDALONE -> QUARTER_VALUE_TO_SHORT_NAME.get(value);
      case NARROW, NARROW_STANDALONE -> String.valueOf(value);
    };
  }

}
