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

  // Instance Fields
  private final Era ERA_ENUM;
  private final Map<TemporalField,Long> FIELDS;

  // Constructors
  private ModernDigitalDate(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    validateFields(era, yearOfEra, monthOfYear, dayOfMonth);
    ERA_ENUM = era;
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
    return isLeapYear() ? 366 : 365;
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
    return ChronoLocalDate.super.format(formatter);
  }

  @Override
  public <R> R query(TemporalQuery<R> query)
  {
    return query.queryFrom(this);
  }

  @Override
  public int compareTo(ChronoLocalDate that)
  {
    if (this.toEpochDay() == that.toEpochDay())
      return CHRONO == that.getChronology() ? 0 : -1;
    else
      return (int) (this.toEpochDay() - that.toEpochDay());
  }

  @Override
  public ChronoLocalDateTime<?> atTime(LocalTime localTime)
  {
    throw CHRONO.noTimeOperationsError();
  }

  // Private Methods
  private void validateFields(Era era, int yearOfEra, int monthOfYear, int dayOfMonth)
  {
    if (
      !(era instanceof ModernDigitalEra) ||
      !range(YEAR_OF_ERA).isValidValue(yearOfEra) ||
      !range(MONTH_OF_YEAR).isValidValue(monthOfYear) ||
      !range(DAY_OF_MONTH).isValidValue(dayOfMonth)
    )
    {
      throw CHRONO.invalidDateError();
    }
  }

  private HashMap<TemporalField,Long> epochDayToFields(long epochDay)
  {
    HashMap<TemporalField,Long> fields = new HashMap<>();
    long remainingDays = epochDay;
    long prolepticYear = 400 * (remainingDays / CHRONO.DAYS_PER_400_YEARS);
    remainingDays %= CHRONO.DAYS_PER_400_YEARS;
    prolepticYear += 100 * (remainingDays / CHRONO.DAYS_PER_100_YEARS);
    remainingDays %= CHRONO.DAYS_PER_100_YEARS;
    prolepticYear += 4 * (remainingDays / CHRONO.DAYS_PER_4_YEARS);
    remainingDays %= CHRONO.DAYS_PER_4_YEARS;
    prolepticYear += remainingDays / CHRONO.DAYS_PER_COMMON_YEAR;
    remainingDays = Math.abs(remainingDays % CHRONO.DAYS_PER_COMMON_YEAR);
    fields.put(EPOCH_DAY, epochDay);
    fields.put(YEAR, prolepticYear);
    fields.put(DAY_OF_YEAR, remainingDays);
    fields.put(MONTH_OF_YEAR, 1 + (remainingDays / CHRONO.DAYS_PER_MONTH));
    fields.put(DAY_OF_MONTH, remainingDays % CHRONO.DAYS_PER_MONTH);
    return fields;
  }
}
