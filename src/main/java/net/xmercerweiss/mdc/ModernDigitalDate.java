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
  // Class Constance
  private static final ModernDigitalChronology CHRONO = ModernDigitalChronology.INSTANCE;

  // Instance Fields
  private final Era ERA_ENUM;
  private final Map<TemporalField,Long> FIELDS;

  // Constructors
  private ModernDigitalDate(Era era, long yearOfEra, long monthOfYear, long dayOfMonth)
  {
    ERA_ENUM = era;
    long prolepticYear = CHRONO.prolepticYear(
      ERA_ENUM,
      (int) yearOfEra
    );
    long epochDay = CHRONO.epochDay(
      ERA_ENUM.getValue(),
      yearOfEra,
      monthOfYear,
      dayOfMonth
    );
    FIELDS = Map.ofEntries(
      entry(ERA, ERA_ENUM.getLong(ERA)),
      entry(YEAR_OF_ERA, yearOfEra),
      entry(MONTH_OF_YEAR, monthOfYear),
      entry(DAY_OF_MONTH, dayOfMonth),
      entry(YEAR, prolepticYear),
      entry(EPOCH_DAY, epochDay)
    );
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
  public boolean isLeapYear()
  {
    return CHRONO.isLeapYear(FIELDS.get(YEAR));
  }

  @Override
  public int lengthOfMonth()
  {
    return 0;
  }

  @Override
  public int lengthOfYear()
  {
    return ChronoLocalDate.super.lengthOfYear();
  }

  @Override
  public boolean isSupported(TemporalField field)
  {
    return ChronoLocalDate.super.isSupported(field);
  }

  @Override
  public ValueRange range(TemporalField field)
  {
    return CHRONO.range((ChronoField) field);
  }

  @Override
  public int get(TemporalField field)
  {
    return ChronoLocalDate.super.get(field);
  }

  @Override
  public boolean isSupported(TemporalUnit unit)
  {
    return ChronoLocalDate.super.isSupported(unit);
  }

  @Override
  public ChronoLocalDate with(TemporalAdjuster adjuster)
  {
    return ChronoLocalDate.super.with(adjuster);
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
  public <R> R query(TemporalQuery<R> query)
  {
    return query.queryFrom(this);
  }

  @Override
  public Temporal adjustInto(Temporal temporal)
  {
    return ChronoLocalDate.super.adjustInto(temporal);
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
  public ChronoLocalDateTime<?> atTime(LocalTime localTime)
  {
    return ChronoLocalDate.super.atTime(localTime);
  }

  @Override
  public long toEpochDay()
  {
    return ChronoLocalDate.super.toEpochDay();
  }

  @Override
  public int compareTo(ChronoLocalDate other)
  {
    return ChronoLocalDate.super.compareTo(other);
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
  public long getLong(TemporalField field)
  {
    return 0;
  }
}
