package net.xmercerweiss.mdc.tests;

import java.time.LocalDate;
import java.util.List;
import java.time.temporal.TemporalQueries;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import net.xmercerweiss.mdc.*;
import static net.xmercerweiss.mdc.ModernDigitalEra.*;


public class ModernDigitalDateUsageTest
{
  private static final List<ModernDigitalDate> VALID_DATES = List.of(
    ModernDigitalDate.of(BEFORE_EPOCH, 1000, 13, 28),
    ModernDigitalDate.of(BEFORE_EPOCH, 500, 6, 7),
    ModernDigitalDate.of(BEFORE_EPOCH, 100, 5, 25),
    ModernDigitalDate.of(BEFORE_EPOCH, 10, 4, 20),
    ModernDigitalDate.of(BEFORE_EPOCH, 5, 3, 14),
    ModernDigitalDate.of(BEFORE_EPOCH, 1, 1, 1),
    ModernDigitalDate.of(SINCE_EPOCH, 0, 1, 1),
    ModernDigitalDate.of(SINCE_EPOCH, 1, 1, 13),
    ModernDigitalDate.of(SINCE_EPOCH, 5, 1, 1),
    ModernDigitalDate.of(SINCE_EPOCH, 10, 6, 15),
    ModernDigitalDate.of(SINCE_EPOCH, 11, 11, 11),
    ModernDigitalDate.of(SINCE_EPOCH, 29, 0, 1),
    ModernDigitalDate.of(SINCE_EPOCH, 30, 1, 1),
    ModernDigitalDate.of(SINCE_EPOCH, 36, 10, 1),
    ModernDigitalDate.of(SINCE_EPOCH, 37, 12, 17),
    ModernDigitalDate.of(SINCE_EPOCH, 100, 1, 1),
    ModernDigitalDate.of(SINCE_EPOCH, 500, 6, 15),
    ModernDigitalDate.of(SINCE_EPOCH, 1000, 7, 21)
  );

  @Test
  void Query_ForChronology_WithTemporalQuery()
  {
    ModernDigitalDate date = ModernDigitalDate.now();
    assertEquals(
      date.getChronology(),
      date.query(TemporalQueries.chronology())
    );
  }

  @Test
  void Query_ForLocalDate_WithTemporalQuery()
  {
    ModernDigitalDate date = ModernDigitalDate.now();
    assertEquals(
      LocalDate.from(date),
      date.query(TemporalQueries.localDate())
    );
  }

  @Test
  void DateToString_WithValidDates_ProducesExpected()
  {
    // TODO write real formatting tests
  }
}
