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


public class ModernDigitalDate
  implements ChronoLocalDate, Serializable
{
  // Class Constants
  private static final ModernDigitalChronology CHRONO = ModernDigitalChronology.INSTANCE;

  private static final String DISPLAY_FMT = "%(d-%02d-%02d MDC";

  private static final char IGNORE_CHAR = '\'';
  private static final char TERM_CHAR = ')';
  private static final String TEXT_ARG_NAME = "Text";
  private static final String VALUE_ARG_NAME = "Value";
  private static final String VALUE_FMT = "%0%sd";

  /*
  Text(Era,SHORT)
  Value(Year)
  Value(YearOfEra)
  Value(DayOfYear)
  Value(MonthOfYear)
  Value(DayOfMonth)
  Value(ModifiedJulianDay)
  Value(QuarterOfYear)
  Localized(WeekBasedYear)
  Localized(WeekOfWeekBasedYear,1)
  Localized(WeekOfMonth,1)
  Text(DayOfWeek,SHORT)
  Localized(DayOfWeek,1)
  Value(AlignedWeekOfMonth)
   */

  // Static Methods
  public static ModernDigitalDate of(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    return new ModernDigitalDate(era, yearOfEra, monthOfYear, dayOfMonth);
  }

  public static ModernDigitalDate of(int prolepticYear, int monthOfYear, int dayOfMonth)
  {
    Era era = CHRONO.eraOf(prolepticYear);
    int yearOfEra = CHRONO.prolepticToEraYear(prolepticYear);
    return ModernDigitalDate.of(era, yearOfEra, monthOfYear, dayOfMonth);
  }

  public static ModernDigitalDate ofYearDay(int prolepticYear, int dayOfYear)
  {
    int monthOfYear = 1 + (dayOfYear / CHRONO.DAYS_PER_MONTH);
    int dayOfMonth = dayOfYear % CHRONO.DAYS_PER_MONTH;
    return ModernDigitalDate.of(prolepticYear, monthOfYear, dayOfMonth);
  }

  public static ModernDigitalDate ofEpochDay(long epochDay)
  {
    return new ModernDigitalDate(epochDay);
  }

  public static ModernDigitalDate now()
  {
    LocalDate today = LocalDate.now();
    return ModernDigitalDate.ofEpochDay(today.toEpochDay());
  }

  public static ModernDigitalDate from(TemporalAccessor temporal)
  {
    if (temporal instanceof ChronoLocalDate date)
      return ModernDigitalDate.ofEpochDay(date.toEpochDay());
    else
      throw CHRONO.noTimeOperationsError();
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

  // Public Override Methods
  @Override
  public boolean isSupported(TemporalField field)
  {
    return FIELDS.containsKey(
      CHRONO.toInternalField(field)
    );
  }

  @Override
  public boolean isSupported(TemporalUnit unit)
  {
    return FIELDS.containsKey(
      CHRONO.toInternalField(unit)
    );
  }

  @Override
  public boolean isAfter(ChronoLocalDate that)
  {
    return this.toEpochDay() > that.toEpochDay();
  }

  @Override
  public boolean isEqual(ChronoLocalDate that)
  {
    return this.toEpochDay() == that.toEpochDay();
  }

  @Override
  public boolean isBefore(ChronoLocalDate that)
  {
    return this.toEpochDay() < that.toEpochDay();
  }

  @Override
  public boolean isLeapYear()
  {
    return CHRONO.isLeapYear(FIELDS.get(YEAR));
  }

  @Override
  public Chronology getChronology()
  {
    return CHRONO;
  }

  @Override
  public Era getEra()
  {
    return ERA_ENUM;
  }

  @Override
  public int get(TemporalField field)
  {
    return (int) getLong(field);
  }

  @Override
  public long getLong(TemporalField field)
  {
    if (isSupported(field))
      return FIELDS.get(field);
    else throw CHRONO.invalidFieldError();
  }

  @Override
  public long toEpochDay()
  {
    return FIELDS.get(EPOCH_DAY);
  }

  @Override
  public int lengthOfMonth()
  {
    int month = getMonth();
    if (month == 0)
      return lengthOfYear() - CHRONO.NON_LEAP_DAYS_PER_YEAR;
    else
      return CHRONO.DAYS_PER_MONTH;
  }

  @Override
  public int lengthOfYear()
  {
    return isLeapYear() ?
      CHRONO.DAYS_PER_LEAP_YEAR : CHRONO.DAYS_PER_COMMON_YEAR;
  }

  @Override
  public ValueRange range(TemporalField field)
  {
    return CHRONO.range(field);
  }

  @Override
  public ChronoLocalDate with(TemporalField field, long newValue)
  {
    return ChronoLocalDate.super.with(field, newValue);
  }

  @Override
  public ChronoLocalDate plus(TemporalAmount amount)
  {
    return ChronoLocalDate.super.plus(amount);
  }

  @Override
  public ChronoLocalDate plus(long amountToAdd, TemporalUnit unit)
  {
    return ChronoLocalDate.super.plus(amountToAdd, unit);
  }

  @Override
  public ChronoLocalDate minus(TemporalAmount amount)
  {
    return ChronoLocalDate.super.minus(amount);
  }

  @Override
  public ChronoLocalDate minus(long amountToSubtract, TemporalUnit unit)
  {
    return ChronoLocalDate.super.minus(amountToSubtract, unit);
  }

  @Override
  public long until(Temporal endExclusive, TemporalUnit unit)
  {
    return 0;
  }

  @Override
  public ChronoPeriod until(ChronoLocalDate endDateExclusive)
  {
    return null;
  }

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

  @Override
  public String toString()
  {
    return DISPLAY_FMT.formatted(
      getYear(),
      getMonth(),
      getDayOfMonth()
    );
  }

  @Override
  public ChronoLocalDateTime<?> atTime(LocalTime localTime)
  {
    throw CHRONO.noTimeOperationsError();
  }

  // Public Methods
  public int getYearOfEra()
  {
    return get(YEAR_OF_ERA);
  }

  public int getYear()
  {
    return get(YEAR);
  }

  public int getMonth()
  {
    return get(MONTH_OF_YEAR);
  }

  public int getWeekOfYear()
  {
    return get(ALIGNED_WEEK_OF_YEAR);
  }

  public int getDayOfYear()
  {
    return get(DAY_OF_YEAR);
  }

  public int getDayOfMonth()
  {
    return get(DAY_OF_MONTH);
  }

  public int getDayOfWeek()
  {
    return get(DAY_OF_WEEK);
  }

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
