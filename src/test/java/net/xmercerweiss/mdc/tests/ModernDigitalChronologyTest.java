package net.xmercerweiss.mdc.tests;

import java.util.Map;
import java.util.Map.Entry;
import static java.util.Map.entry;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import net.xmercerweiss.mdc.*;


public class ModernDigitalChronologyTest
{
  private static final Map<Long,Long> PROLEPTIC_TO_FIRST_EPOCH_DAY = Map.ofEntries(
    entry(-100L, -36524L),
    entry(-99L, -36159L),
    entry(-98L, -35794L),
    entry(-70L, -25567L),
    entry(-22L, -8036L),
    entry(-18L, -6575L),
    entry(-14L, -5114L),
    entry(-11L, -4018L),
    entry(-10L, -3653L),
    entry(-9L, -3287L),
    entry(-8L, -2922L),
    entry(-7L, -2557L),
    entry(-6L, -2192L),
    entry(-5L, -1826L),
    entry(-4L, -1461L),
    entry(-3L, -1096L),
    entry(-2L, -731L),
    entry(-1L, -365L),
    entry(0L, 0L),
    entry(1L, 365L),
    entry(2L, 730L),
    entry(3L, 1096L),
    entry(4L, 1461L),
    entry(5L, 1826L),
    entry(6L, 2191L),
    entry(7L, 2557L),
    entry(8L, 2922L),
    entry(9L, 3287L),
    entry(10L, 3652L),
    entry(11L, 4018L),
    entry(14L, 5113L),
    entry(15L, 5479L),
    entry(18L, 6574L),
    entry(19L, 6940L),
    entry(22L, 8035L),
    entry(23L, 8401L),
    entry(30L, 10957L),
    entry(31L, 11323L),
    entry(98L, 35794L),
    entry(99L, 36160L),
    entry(100L, 36525L)
  );

  @Test
  void FirstEpochDayOfYear_WithProleptic_ProducesExpected()
  {
    ModernDigitalChronology chrono = ModernDigitalChronology.INSTANCE;
    for (Entry<Long,Long> e : PROLEPTIC_TO_FIRST_EPOCH_DAY.entrySet())
    {
      long proleptic = e.getKey();
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
