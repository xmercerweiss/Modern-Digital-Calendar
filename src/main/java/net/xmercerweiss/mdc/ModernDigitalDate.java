package net.xmercerweiss.mdc;

import java.io.Serializable;
import java.util.Map;
import java.time.chrono.*;
import java.time.temporal.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.util.Map.entry;
import static java.time.temporal.ChronoField.*;


public class ModernDigitalDate
  implements ChronoLocalDate, Serializable
{
  // Class Constants
  private static final ModernDigitalChronology CHRONO = ModernDigitalChronology.INSTANCE;

  // Instance Fields
  private final Era ERA_ENUM;
  private final Map<TemporalField,Long> FIELDS;

  // Constructors
  private ModernDigitalDate(Era era, long yearOfEra, long monthOfYear, long dayOfMonth)
  {
    validateFields(era, yearOfEra, monthOfYear, dayOfMonth);
    ERA_ENUM = era;
    long prolepticYear = CHRONO.prolepticYear(
      ERA_ENUM,
      (int) yearOfEra
    );
    long epochDay = CHRONO.epochDay(
      ERA_ENUM,
      (int) yearOfEra,
      monthOfYear,
      dayOfMonth
    );
    long dayOfYear = CHRONO.ordinalDayOfYear(monthOfYear, dayOfMonth);
    long weekOfYear = CHRONO.ordinalWeekOfYear(dayOfYear);
    long dayOfWeek = dayOfMonth % CHRONO.DAYS_PER_WEEK;
    FIELDS = Map.ofEntries(
      entry(ERA, ERA_ENUM.getLong(ERA)),
      entry(YEAR_OF_ERA, yearOfEra),
      entry(YEAR, prolepticYear),
      entry(MONTH_OF_YEAR, monthOfYear),
      entry(ALIGNED_WEEK_OF_YEAR, weekOfYear),
      entry(DAY_OF_YEAR, dayOfYear),
      entry(DAY_OF_MONTH, dayOfMonth),
      entry(DAY_OF_WEEK, dayOfWeek),
      entry(EPOCH_DAY, epochDay)
    );
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
  public boolean isLeapYear()
  {
    return CHRONO.isLeapYear(FIELDS.get(YEAR));
  }

  @Override
  public boolean isAfter(ChronoLocalDate other)
  {
    return ChronoLocalDate.super.isAfter(other);
  }

  @Override
  public boolean isBefore(ChronoLocalDate other)
  {
    return ChronoLocalDate.super.isBefore(other);
  }

  @Override
  public boolean isEqual(ChronoLocalDate other)
  {
    return ChronoLocalDate.super.isEqual(other);
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
    return 0;
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
      return this.getChronology() == that.getChronology() ?
        0 : -1;
    else
      return Long.compare(this.toEpochDay(), that.toEpochDay());
  }

  @Override
  public ChronoLocalDateTime<?> atTime(LocalTime localTime)
  {
    throw CHRONO.noTimeOperationsError();
  }

  // Private Methods
  private void validateFields(Era era, long yearOfEra, long monthOfYear, long dayOfMonth)
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
}
