package net.xmercerweiss.mdc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.time.*;
import java.time.chrono.*;
import java.time.temporal.*;
import java.time.format.TextStyle;
import java.util.Locale;


public class ModernDigitalChronology extends AbstractChronology
  implements Serializable
{
  // Class Constants
  public static final ModernDigitalChronology INSTANCE =
    new ModernDigitalChronology();

  private static final long NEG_INFINITY = Long.MIN_VALUE;
  private static final long POS_INFINITY = Long.MAX_VALUE;

  private static final int ISO_YEAR_OFFSET = 1970;

  private static final String ID = "ModernDigital";

  private static final String INV_ERA_ERR_MSG =
    "ModernDigitalChronology given invalid Era; must be instance of ModernDigitalEra";

  private static final String INV_FIELD_ERR_MSG =
    "ModernDigitalChronology given invalid ChronoField; cannot process fields shorter than a day";

  // Constructors
  private ModernDigitalChronology()
  {
    // Private no-arg constructor for Singleton instance, intentionally blank
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
  public ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor temporal)
  {
    return super.localDateTime(temporal);
  }

  @Override
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor temporal)
  {
    return super.zonedDateTime(temporal);
  }

  @Override
  public ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant instant, ZoneId zone)
  {
    return super.zonedDateTime(instant, zone);
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
    if (era instanceof ModernDigitalEra mde)
      return switch (mde)
      {
        case BEFORE_EPOCH -> -1 * Math.abs(yearOfEra);
        case SINCE_EPOCH -> Math.abs(yearOfEra);
      };
    else throw new ClassCastException(INV_ERA_ERR_MSG);
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
    return switch (field)
    {
      case ERA -> ValueRange.of(0, 1);
      case YEAR_OF_ERA -> ValueRange.of(0, POS_INFINITY);
      case YEAR -> ValueRange.of(NEG_INFINITY, POS_INFINITY);
      case MONTH_OF_YEAR -> ValueRange.of(0, 13);
        // Leap days belong to "month 0" of year
      case ALIGNED_WEEK_OF_YEAR -> ValueRange.of(0, 52);
        // Leap days belong to "week 0" of year
      case DAY_OF_YEAR -> ValueRange.of(1, 365, 366);
      case PROLEPTIC_MONTH -> ValueRange.of(NEG_INFINITY, POS_INFINITY);
        // The "proleptic month" branch duplicates "year" because while they have
        // the same range of values, they are not enumerated constants representing
        // the same field within the calendar. They exist independently of one another
      case ALIGNED_WEEK_OF_MONTH -> ValueRange.of(0, 4);
        // Leap days belong to "week 0" of "month 0"
      case DAY_OF_MONTH -> ValueRange.of(1, 2, 28);
        // The "0th" month (the non-month to which leap days belong) has a max of 2 days
      case DAY_OF_WEEK, ALIGNED_DAY_OF_WEEK_IN_MONTH, ALIGNED_DAY_OF_WEEK_IN_YEAR
        -> ValueRange.of(0, 7);  // Leap days belong to "day 0" of week
      case EPOCH_DAY -> ValueRange.of(NEG_INFINITY, POS_INFINITY);
        // See comment under "proleptic month" for duplicate reasoning
      default -> throw new DateTimeException(INV_FIELD_ERR_MSG);
    };
  }

  @Override
  public String getDisplayName(TextStyle style, Locale locale)
  {
    return super.getDisplayName(style, locale);
  }

  @Override
  public ChronoPeriod period(int years, int months, int days)
  {
    return Period.of(years, months, days);
  }

  @Override
  public long epochSecond(int prolepticYear, int month, int dayOfMonth, int hour, int minute, int second, ZoneOffset zoneOffset)
  {
    return super.epochSecond(prolepticYear, month, dayOfMonth, hour, minute, second, zoneOffset);
  }

  @Override
  public long epochSecond(Era era, int yearOfEra, int month, int dayOfMonth, int hour, int minute, int second, ZoneOffset zoneOffset)
  {
    return super.epochSecond(era, yearOfEra, month, dayOfMonth, hour, minute, second, zoneOffset);
  }

  // Public Methods
  public long epochDay(long era, long yearOfEra, long monthOfYear, long dayOfMonth)
  {
    return 0;
  }
}
