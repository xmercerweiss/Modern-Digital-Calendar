package net.xmercerweiss.mdc;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.time.*;
import java.time.chrono.*;
import java.time.temporal.*;
import java.time.format.DateTimeFormatter;

import static java.util.Map.entry;
import static java.time.temporal.ChronoField.*;

import static net.xmercerweiss.mdc.CustomField.*;


/**
 * Represents a specific date within the <a href="https://github.com/xmercerweiss/Modern-Digital-Calendar/blob/main/README.md">Modern Digital Calendar system</a>
 * <br><br>
 * Note that attempting to work with time-based values using this class is an error; see {@link net.xmercerweiss.mdc.ModernDigitalChronology}
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-02
 */
public class ModernDigitalDate
  implements ChronoLocalDate, Serializable
{
  // Class Constants
  private static final ModernDigitalChronology CHRONO = ModernDigitalChronology.INSTANCE;

  private static final String DISPLAY_FMT = "yyyy-MM-dd MDC";

  private static final char IGNORE_CHAR = '\'';
  private static final char TERM_CHAR = ')';
  private static final String TEXT_ARG_NAME = "Text";
  private static final String LOCALIZED_TEXT_ARG_NAME = "Localized";
  private static final String VALUE_ARG_NAME = "ReducedValue";
  private static final String REDUCED_VALUE_ARG_NAME = "Value";

  private static final String VALUE_FMT = "%0%sd";

  private static final Map<String,TemporalField> FMT_VALUE_NAME_TO_FIELD = Map.ofEntries(
    entry("Era", ERA),
    entry("Year", YEAR),
    entry("YearOfEra", YEAR_OF_ERA),
    entry("DayOfYear", DAY_OF_YEAR),
    entry("MonthOfYear", MONTH_OF_YEAR),
    entry("DayOfMonth", DAY_OF_MONTH),
    entry("ModifiedJulianDay", MODIFIED_JULIAN_DAY),
    entry("QuarterOfYear", QUARTER_OF_YEAR),
    entry("WeekOfMonth", ALIGNED_WEEK_OF_MONTH),
    entry("AlignedWeekOfMonth", ALIGNED_WEEK_OF_MONTH),
    entry("DayOfWeek", DAY_OF_WEEK)
  );

  // Error Methods
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

  // Constructors
  private ModernDigitalDate(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    validateFields(era, yearOfEra, monthOfYear, dayOfMonth);
    ERA_ENUM = yearOfEra == 0 ? ModernDigitalEra.SINCE_EPOCH : era;
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
        (long) CHRONO.ordinalWeekOfYear(monthOfYear, dayOfMonth)
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
        (long) dayOfMonth % CHRONO.DAYS_PER_WEEK
      ),
      entry(
        EPOCH_DAY,
        CHRONO.epochDay(ERA_ENUM, yearOfEra, monthOfYear, dayOfMonth)
      )
    );
  }

  private ModernDigitalDate(long epochDay)
  {
    HashMap<TemporalField,Long> workingFields = epochDayToFields(epochDay);
    int prolepticYear = Math.toIntExact(workingFields.get(YEAR));
    int monthOfYear = Math.toIntExact(workingFields.get(MONTH_OF_YEAR));
    int dayOfMonth = Math.toIntExact(workingFields.get(DAY_OF_MONTH));
    ERA_ENUM = CHRONO.eraOf(prolepticYear);
    workingFields.put(
      ERA,
      ERA_ENUM.getLong(ERA)
    );
    workingFields.put(
      YEAR_OF_ERA,
      (long) CHRONO.prolepticToEraYear(prolepticYear)
    );
    workingFields.put(
      ALIGNED_WEEK_OF_YEAR,
      (long) CHRONO.ordinalWeekOfYear(monthOfYear, dayOfMonth)
    );
    workingFields.put(
      DAY_OF_WEEK,
      (long) dayOfMonth % CHRONO.DAYS_PER_WEEK
    );
    FIELDS = Map.copyOf(workingFields);
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
    return FIELDS.containsKey(
      CHRONO.toInternalField(field)
    );
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
   * @param field A {@link java.time.temporal.TemporalField}, must be {@link net.xmercerweiss.mdc.ModernDigitalChronology#isSupported supported}
   *              and not null
   * @return A {@link java.time.temporal.ValueRange}
   * @throws UnsupportedTemporalTypeException If given an unsupported field
   */
  @Override
  public ValueRange range(TemporalField field)
  {
    return CHRONO.range(field);
  }

  // TODO implement, test, and document
  @Override
  public ChronoLocalDate with(TemporalField field, long newValue)
  {
    return ChronoLocalDate.super.with(field, newValue);
  }

  // TODO implement, test, and document
  @Override
  public ChronoLocalDate plus(TemporalAmount amount)
  {
    return ChronoLocalDate.super.plus(amount);
  }

  // TODO implement, test, and document
  @Override
  public ChronoLocalDate plus(long amountToAdd, TemporalUnit unit)
  {
    return ChronoLocalDate.super.plus(amountToAdd, unit);
  }

  // TODO implement, test, and document
  @Override
  public ChronoLocalDate minus(TemporalAmount amount)
  {
    return ChronoLocalDate.super.minus(amount);
  }

  // TODO implement, test, and document
  @Override
  public ChronoLocalDate minus(long amountToSubtract, TemporalUnit unit)
  {
    return ChronoLocalDate.super.minus(amountToSubtract, unit);
  }

  // TODO implement, test, and document
  @Override
  public long until(Temporal endExclusive, TemporalUnit unit)
  {
    return 0;
  }

  // TODO implement, test, and document
  @Override
  public ChronoPeriod until(ChronoLocalDate endDateExclusive)
  {
    return null;
  }

  // TODO implement, test, and document
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
      throw CHRONO.invalidDateError();
    else if (this.toEpochDay() == that.toEpochDay())
      return CHRONO == that.getChronology() ? 0 : -1;
    else
      return (int) (this.toEpochDay() - that.toEpochDay());
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
   * The {@code Chronology} of this {@code Date} does not support time-based methods; see {@link net.xmercerweiss.mdc.ModernDigitalChronology}
   * @throws UnsupportedOperationException Not implemented
   */
  @Override
  public ChronoLocalDateTime<?> atTime(LocalTime localTime)
  {
    throw CHRONO.noTimeOperationsError();
  }

  // Public Methods
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
   * Following the conventions of ISO 8601, Monday has a value of 1 and Sunday has a value of 7.
   * Leap days belong to no week and therefore have a value of 0
   * @return A signed 32-bit integer within [0, 7]
   */
  public int getDayOfWeek()
  {
    return get(DAY_OF_WEEK);
  }

  // TODO implement, test, and document
  public String format(String pattern)
  {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return format(formatter);
  }

  // Private Methods
  private void validateFields(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    if (
      !(era instanceof ModernDigitalEra) ||
      !range(YEAR_OF_ERA).isValidValue(yearOfEra) ||
      !range(MONTH_OF_YEAR).isValidValue(monthOfYear) ||
      !range(DAY_OF_MONTH).isValidValue(dayOfMonth) ||
      (monthOfYear == 0 && dayOfMonth > range(DAY_OF_MONTH).getSmallestMaximum()) ||
      (monthOfYear == 0 && dayOfMonth > 1 && !CHRONO.isLeapYear(
        CHRONO.prolepticYear(era, yearOfEra)
      ))
    )
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

  private String formatterStringToOutput(String fmt)
  {
    StringBuilder out = new StringBuilder();
    StringBuilder arg = new StringBuilder();
    boolean ignore = false;
    for (char c : fmt.toCharArray())
    {
      if (c == IGNORE_CHAR)
        ignore = !ignore;
      else if (ignore)
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
    }
    return out.toString();
  }

  private String renderFormatArg(String arg)
  {
    if (arg.startsWith(TEXT_ARG_NAME))
    {
      String[] settings = arg.substring(
        TEXT_ARG_NAME.length() + 1,
        arg.length() - 1
      ).split(",");
      return renderTextArg(settings);
    }
    else if (arg.startsWith(VALUE_ARG_NAME))
    {
      String[] settings = arg.substring(
        VALUE_ARG_NAME.length() + 1,
        arg.length() - 1
      ).split(",");
      return renderValueArg(settings);
    }
    else
      return arg;
  }

  private String renderTextArg(String... settings)
  {
    return String.join(":", settings);
  }

  private String renderValueArg(String... settings)
  {
    return String.join(";", settings);
  }
}
