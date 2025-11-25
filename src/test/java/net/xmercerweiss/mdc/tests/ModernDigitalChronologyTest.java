package net.xmercerweiss.mdc.tests;

import java.util.Map;
import java.util.Map.Entry;
import static java.util.Map.entry;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import net.xmercerweiss.mdc.*;


public class ModernDigitalChronologyTest
{
  private static final Map<Integer,Long> PROLEPTIC_TO_FIRST_EPOCH_DAY = Map.ofEntries(
    entry(-100, -36524L),
    entry(-99, -36159L),
    entry(-98, -35794L),
    entry(-70, -25567L),
    entry(-22, -8036L),
    entry(-18, -6575L),
    entry(-14, -5114L),
    entry(-11, -4018L),
    entry(-10, -3653L),
    entry(-9, -3287L),
    entry(-8, -2922L),
    entry(-7, -2557L),
    entry(-6, -2192L),
    entry(-5, -1826L),
    entry(-4, -1461L),
    entry(-3, -1096L),
    entry(-2, -731L),
    entry(-1, -365L),
    entry(0, 0L),
    entry(1, 365L),
    entry(2, 730L),
    entry(3, 1096L),
    entry(4, 1461L),
    entry(5, 1826L),
    entry(6, 2191L),
    entry(7, 2557L),
    entry(8, 2922L),
    entry(9, 3287L),
    entry(10, 3652L),
    entry(11, 4018L),
    entry(14, 5113L),
    entry(15, 5479L),
    entry(18, 6574L),
    entry(19, 6940L),
    entry(22, 8035L),
    entry(23, 8401L),
    entry(30, 10957L),
    entry(31, 11323L),
    entry(98, 35794L),
    entry(99, 36160L),
    entry(100, 36525L)
  );

  @Test
  void FirstEpochDayOfYear_WithProleptic_ProducesExpected()
  {
    ModernDigitalChronology chrono = ModernDigitalChronology.INSTANCE;
    for (Entry<Integer,Long> e : PROLEPTIC_TO_FIRST_EPOCH_DAY.entrySet())
    {
      int proleptic = e.getKey();
      long expected = e.getValue();
      long actual = chrono.epochDay(
        proleptic,  // Year
        1,          // Month
        1           // Day
      );
      assertEquals(expected, actual);
    }
  }
}
