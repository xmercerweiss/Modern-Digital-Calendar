package net.xmercerweiss.mdc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.time.*;
import java.time.chrono.*;
import java.time.temporal.*;
import java.time.format.TextStyle;
import java.util.Locale;

import static java.util.Map.entry;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.*;


public class ModernDigitalChronology extends AbstractChronology
  implements Serializable
{
  // Class Constants
  public static final ModernDigitalChronology INSTANCE =
    new ModernDigitalChronology();

  private static final long NEG_INFINITY = Long.MIN_VALUE;
  private static final long POS_INFINITY = Long.MAX_VALUE;

  private static final long ISO_YEAR_OFFSET = 1970;

  public static final long DAYS_PER_WEEK = 7;
  public static final long WEEKS_PER_MONTH = 4;
  public static final long DAYS_PER_MONTH = DAYS_PER_WEEK * WEEKS_PER_MONTH;
  public static final long NON_LEAP_MONTHS_PER_YEAR = 13;
  public static final long WEEKS_PER_YEAR = WEEKS_PER_MONTH * NON_LEAP_MONTHS_PER_YEAR;
  public static final long NON_LEAP_DAYS_PER_YEAR = NON_LEAP_MONTHS_PER_YEAR * DAYS_PER_MONTH;
  public static final long DAYS_PER_COMMON_YEAR = NON_LEAP_DAYS_PER_YEAR + 1;
  public static final long DAYS_PER_LEAP_YEAR = NON_LEAP_DAYS_PER_YEAR + 2;

  private static final String ID = "ModernDigital";
  private static final String DISPLAY_NAME = "Modern Digital Calendar";

  private static final Map<TemporalField,ChronoField> FIELD_TO_INTERNAL_FIELD = Map.ofEntries(
    entry(ERA, ERA),
    entry(YEAR_OF_ERA, YEAR_OF_ERA),
    entry(YEAR, YEAR),
    entry(MONTH_OF_YEAR, MONTH_OF_YEAR),
    entry(ALIGNED_WEEK_OF_YEAR, ALIGNED_WEEK_OF_YEAR),
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
      "ModernDigitalChronology attempted to create date with invalid values in one or more fields"
    );
  }

  static UnsupportedOperationException noTimeOperationsError()
  {
    return new UnsupportedOperationException(
      "ModernDigitalChronology only implements datekeeping; use LocalTime for timekeeping"
    );
  }

  // Constructors
  private ModernDigitalChronology()
  {
    // Private no-arg constructor for Singleton instance, intentionally empty
  }

  // Public Override Methods
  @Override
  public String getId()
  {
    return ID;
  }

  @Override
  public String getCalendarType()
  {
    // This is a custom calendar, and therefore is not defined by the Unicode
    // Locale Data Markup Language standard; null is the correct return value
    return null;
  }

  @Override
  public String getDisplayName(TextStyle style, Locale locale)
  {
    // The Chronology implementations provided by java.time.chrono disregard
    // the Locale and TextStyle parameters of this method, always returning a
    // phrase such as "Buddhist Calendar". This method provides the same
    // incomplete implementation for consistency
    return DISPLAY_NAME;
  }

  @Override
  public ChronoLocalDate date(Era era, int yearOfEra, int month, int dayOfMonth)
  {
    return super.date(era, yearOfEra, month, dayOfMonth);
  }

  @Override
  public ChronoLocalDate date(int prolepticYear, int month, int dayOfMonth)
  {
    return null;
  }

  @Override
  public ChronoLocalDate dateYearDay(Era era, int yearOfEra, int dayOfYear)
  {
    return super.dateYearDay(era, yearOfEra, dayOfYear);
  }

  @Override
  public ChronoLocalDate dateYearDay(int prolepticYear, int dayOfYear)
  {
    return null;
  }

  @Override
  public ChronoLocalDate dateEpochDay(long epochDay)
  {
    return null;
  }

  @Override
  public ChronoLocalDate dateNow()
  {
    return super.dateNow();
  }

  @Override
  public ChronoLocalDate dateNow(ZoneId zone)
  {
    return super.dateNow(zone);
  }

  @Override
  public ChronoLocalDate dateNow(Clock clock)
  {
    return super.dateNow(clock);
  }

  @Override
  public ChronoLocalDate date(TemporalAccessor temporal)
  {
    return null;
  }

  @Override
  public boolean isLeapYear(long prolepticYear)
  {
    int isoYear = (int) Math.abs(prolepticYear + ISO_YEAR_OFFSET);
    return (isoYear % 400 == 0) || (isoYear % 100 != 0 && isoYear % 4 == 0);
  }

  @Override
  public int prolepticYear(Era era, int yearOfEra)
  {
    if (
      range(YEAR_OF_ERA).isValidValue(yearOfEra) &&
      era instanceof ModernDigitalEra mde
    )
      return switch (mde)
      {
        case BEFORE_EPOCH -> -1 * yearOfEra;
        case SINCE_EPOCH -> yearOfEra;
      };
    else throw invalidEraError();
  }

  @Override
  public Era eraOf(int eraValue)
  {
    return eraValue <= 0 ?
      ModernDigitalEra.BEFORE_EPOCH : ModernDigitalEra.SINCE_EPOCH;
  }

  @Override
  public List<Era> eras()
  {
    return Arrays.stream(ModernDigitalEra.values())
      .map(e -> (Era) e)
      .toList();
  }

  @Override
  public ValueRange range(ChronoField field)
  {
    return range((TemporalField) field);
  }

  @Override
  public ChronoPeriod period(int years, int months, int days)
  {
    return Period.of(years, months, days);
  }

  @Override
  public long epochSecond(int prolepticYear, int month, int dayOfMonth, int hour, int minute, int second, ZoneOffset zoneOffset)
  {
    throw noTimeOperationsError();
  }

  @Override
  public long epochSecond(Era era, int yearOfEra, int month, int dayOfMonth, int hour, int minute, int second, ZoneOffset zoneOffset)
  {
    throw noTimeOperationsError();
  }

  @Override
  public ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor temporal)
  {
    throw noTimeOperationsError();
  }

  @Override
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor temporal)
  {
    throw noTimeOperationsError();
  }

  @Override
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant instant, ZoneId zone)
  {
    throw noTimeOperationsError();
  }

  // Public Methods
  public boolean isSupported(TemporalField field)
  {
    return FIELD_TO_INTERNAL_FIELD.containsKey(field);
  }

  public boolean isSupported(TemporalUnit unit)
  {
    return UNIT_TO_INTERNAL_FIELD.containsKey(unit);
  }

  public TemporalField toInternalField(TemporalField field)
  {
    if (isSupported(field))
      return FIELD_TO_INTERNAL_FIELD.get(field);
    else throw invalidFieldError();
  }

  public TemporalField toInternalField(TemporalUnit unit)
  {

    if (isSupported(unit))
      return UNIT_TO_INTERNAL_FIELD.get(unit);
    else throw invalidFieldError();
  }

  public ValueRange range(TemporalField field)
  {
    if (FIELD_TO_INTERNAL_FIELD.containsKey(field))
    {
      ChronoField internalField = FIELD_TO_INTERNAL_FIELD.get(field);
      return switch (internalField)
      {
        case ERA -> ValueRange.of(0, 1);
        case YEAR_OF_ERA -> ValueRange.of(0, POS_INFINITY);
        case YEAR -> ValueRange.of(NEG_INFINITY, POS_INFINITY);
        case MONTH_OF_YEAR -> ValueRange.of(0, NON_LEAP_MONTHS_PER_YEAR);
        // Leap days belong to "month 0" of year
        case ALIGNED_WEEK_OF_YEAR -> ValueRange.of(0, WEEKS_PER_YEAR);
        // Leap days belong to "week 0" of year
        case DAY_OF_YEAR -> ValueRange.of(1, DAYS_PER_COMMON_YEAR, DAYS_PER_LEAP_YEAR);
        case DAY_OF_MONTH -> ValueRange.of(1, 2, DAYS_PER_MONTH);
        // The "0th" month (the non-month to which leap days belong) has a max of 2 days
        case DAY_OF_WEEK -> ValueRange.of(0, DAYS_PER_WEEK);
        // Leap days belong to "day 0" of week
        case EPOCH_DAY -> ValueRange.of(NEG_INFINITY, POS_INFINITY);
        // The "epoch day" branch duplicates "year" because while they have
        // the same range of values, they are not enumerated constants representing
        // the same field within the calendar. They exist independently of one another
        default -> throw invalidFieldError();
      };
    }
    else throw invalidFieldError();
  }

  public long epochDay(Era era, int yearOfEra, long monthOfYear, long dayOfMonth)
  {
    return epochDay(prolepticYear(era, yearOfEra), monthOfYear, dayOfMonth);
  }

  public long epochDay(long prolepticYear, long monthOfYear, long dayOfMonth)
  {
    int isoYear = (int) (prolepticYear + ISO_YEAR_OFFSET);
    int dayOfYear = (int) ordinalDayOfYear(monthOfYear, dayOfMonth);
    LocalDate isoDate = LocalDate.ofYearDay(isoYear, dayOfYear);
    return isoDate.toEpochDay();
  }

  public long ordinalDayOfYear(long monthOfYear, long dayOfMonth)
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

  public long ordinalWeekOfYear(long monthOfYear, long dayOfMonth)
  {
    return ordinalWeekOfYear(
      ordinalDayOfYear(monthOfYear, dayOfMonth)
    );
  }

  public long ordinalWeekOfYear(long dayOfYear)
  {
    return 1 + (dayOfYear / DAYS_PER_WEEK);
  }
}
