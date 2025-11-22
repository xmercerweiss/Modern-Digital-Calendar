package net.xmercerweiss.mdc;

import java.util.List;
import java.time.chrono.Chronology;
import java.time.chrono.AbstractChronology;
import java.time.chrono.Era;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.ValueRange;


public class ModernDigitalChronology extends AbstractChronology
{
  // Class Constants
  public static final ModernDigitalChronology INSTANCE =
    new ModernDigitalChronology();

  // Constructors
  private ModernDigitalChronology()
  {

  }

  // Public Override Methods
  @Override
  public String getId()
  {
    return "";
  }

  @Override
  public String getCalendarType()
  {
    return "";
  }

  @Override
  public ChronoLocalDate date(int prolepticYear, int month, int dayOfMonth)
  {
    return null;
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
  public ChronoLocalDate date(TemporalAccessor temporal)
  {
    return null;
  }

  @Override
  public boolean isLeapYear(long prolepticYear)
  {
    return false;
  }

  @Override
  public int prolepticYear(Era era, int yearOfEra)
  {
    return 0;
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
    return ModernDigitalEra.ERAS;
  }

  @Override
  public ValueRange range(ChronoField field)
  {
    return null;
  }
}
