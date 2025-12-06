package net.xmercerweiss.mdc.tests;

import java.util.Map;
import java.util.Map.Entry;
import java.time.LocalDate;
import java.time.temporal.TemporalQueries;
import java.time.format.DateTimeFormatter;
import static java.util.Map.entry;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import net.xmercerweiss.mdc.*;
import static net.xmercerweiss.mdc.ModernDigitalEra.*;


public class ModernDigitalDateUsageTest
{
  private static final Map<ModernDigitalDate,String> VALID_DATE_TO_ISO_LOCAL_STR = Map.ofEntries(
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 1000, 13, 28), "-1000-13-28"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 500, 6, 7), "-500-06-07"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 100, 5, 25), "-100-05-25"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 10, 4, 20), "-010-04-20"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 5, 3, 14), "-005-03-14"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 1, 1, 1), "-001-01-01"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 0, 1, 1), "0000-01-01"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 1, 1, 13), "0001-01-13"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 5, 1, 1), "0005-01-01"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 10, 6, 15), "0010-06-15"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 11, 11, 11), "0011-11-11"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 29, 0, 1), "0029-00-01"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 30, 1, 1), "0030-01-01"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 36, 10, 1), "0036-10-01"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 37, 12, 17), "0037-12-17"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 100, 1, 1), "0100-01-01"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 500, 6, 15), "0500-06-15"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 1000, 7, 21), "1000-07-21")
  );

  private static final Map<ModernDigitalDate,String> VALID_DATE_TO_ISO_ORDINAL_STR = Map.ofEntries(
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 1000, 13, 28), "-1000-364"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 500, 6, 7), "-500-147"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 100, 5, 25), "-100-137"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 10, 4, 20), "-010-104"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 5, 3, 14), "-005-070"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 1, 1, 1), "-001-001"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 0, 1, 1), "0000-001"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 1, 1, 13), "0001-013"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 5, 1, 1), "0005-001"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 10, 6, 15), "0010-155"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 11, 11, 11), "0011-291"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 29, 0, 1), "0029-365"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 30, 1, 1), "0030-001"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 36, 10, 1), "0036-253"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 37, 12, 17), "0037-325"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 100, 1, 1), "0100-001"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 500, 6, 15), "0500-155"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 1000, 7, 21), "1000-189")
  );

  private static final Map<ModernDigitalDate,String> VALID_DATE_TO_CUSTOM_STR = Map.ofEntries(
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 1000, 13, 28), "Sunday, Tredecember 28, 1000 BE"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 500, 6, 7), "Sunday, Hextuary 7, 500 BE"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 100, 5, 25), "Thursday, Pentuary 25, 100 BE"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 100, 5, 26), "Friday, Pentuary 26, 100 BE"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 10, 4, 20), "Saturday, Quartuary 20, 10 BE"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 5, 3, 14), "Sunday, Tertiary 14, 5 BE"),
    entry(ModernDigitalDate.of(BEFORE_EPOCH, 1, 7, 9), "Tuesday, September 9, 1 BE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 0, 1, 1), "Monday, Unitary 1, 0 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 1, 1, 13), "Saturday, Unitary 13, 1 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 5, 1, 1), "Monday, Unitary 1, 5 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 10, 6, 15), "Monday, Hextuary 15, 10 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 11, 11, 11), "Thursday, Hendecember 11, 11 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 22, 0, 2), "None, Leap 2, 22 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 29, 0, 1), "None, Leap 1, 29 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 30, 1, 1), "Monday, Unitary 1, 30 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 36, 10, 1), "Monday, December 1, 36 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 37, 12, 17), "Wednesday, Dodecember 17, 37 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 100, 9, 24), "Wednesday, November 24, 100 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 500, 8, 18), "Thursday, October 18, 500 SE"),
    entry(ModernDigitalDate.of(SINCE_EPOCH, 1000, 7, 19), "Friday, September 19, 1000 SE")
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
  void DateToIsoLocalString_WithValidDates_ProducesExpected()
  {
    DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
    for (Entry<ModernDigitalDate,String> e : VALID_DATE_TO_ISO_LOCAL_STR.entrySet())
    {
      ModernDigitalDate date = e.getKey();
      String expected = e.getValue();
      String actual = date.format(fmt);
      assertEquals(expected, actual);
    }
  }

  @Test
  void DateToIsoOrdinalString_WithValidDates_ProducesExpected()
  {
    DateTimeFormatter fmt = DateTimeFormatter.ISO_ORDINAL_DATE;
    for (Entry<ModernDigitalDate,String> e : VALID_DATE_TO_ISO_ORDINAL_STR.entrySet())
    {
      ModernDigitalDate date = e.getKey();
      String expected = e.getValue();
      String actual = date.format(fmt);
      assertEquals(expected, actual);
    }
  }
  @Test
  void DateToCustomString_WithValidDates_ProducesExpected()
  {
    String fmt = "EEEE, MMMM d, y G";
    for (Entry<ModernDigitalDate,String> e : VALID_DATE_TO_CUSTOM_STR.entrySet())
    {
      ModernDigitalDate date = e.getKey();
      String expected = e.getValue();
      String actual = date.format(fmt);
      assertEquals(expected, actual);
    }
  }
}
