package net.xmercerweiss.mdc.tests;

import java.util.Map;
import java.util.Map.Entry;
import java.time.*;
import java.time.chrono.ChronoPeriod;
import static java.util.Map.entry;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import net.xmercerweiss.mdc.*;
import net.xmercerweiss.mdc.tests.util.*;


/**
 * This class provides testing of {@code ModernDigitalDate}'s methods that utilize
 * {@link java.time.chrono.ChronoPeriod ChronoPeriods}. Important notes regarding
 * these methods can be found in {@code docs/MISC.MD#ChronoPeriods}
 */
public class ModernDigitalDateChronoPeriodTest
{
  private static final Map<Pair<ModernDigitalDate,ModernDigitalDate>,Period> VALID_DATES_TO_PERIOD = Map.ofEntries(
    entry(
      Pair.of(
        ModernDigitalDate.of(0, 1, 1),
        ModernDigitalDate.of(30, 1, 1)
      ),
      Period.ofYears(30)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(0, 6, 15),
        ModernDigitalDate.of(5, 9, 17)
      ),
      Period.of(5, 3, 2)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(-100, 6, 1),
        ModernDigitalDate.of(0, 1, 1)
      ),
      Period.of(99, 8, 0)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(20, 9, 25),
        ModernDigitalDate.of(21, 9, 10)
      ),
      Period.of(0, 12, 13)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(36, 10, 12),
        ModernDigitalDate.of(40, 2, 7)
      ),
      Period.of(3, 4, 23)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(0, 0, 1),
        ModernDigitalDate.of(5, 0, 1)
      ),
      Period.ofYears(5)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(5, 13, 20),
        ModernDigitalDate.of(6, 1, 1)
      ),
      Period.ofDays(10)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(15, 13, 20),
        ModernDigitalDate.of(16, 1, 19)
      ),
      Period.ofDays(28)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(-100, 13, 20),
        ModernDigitalDate.of(-99, 1, 20)
      ),
      Period.ofMonths(1)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(1, 1, 20),
        ModernDigitalDate.of(1, 2, 20)
      ),
      Period.ofMonths(1)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(1, 1, 20),
        ModernDigitalDate.of(1, 2, 19)
      ),
      Period.ofDays(27)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(6, 13, 28),
        ModernDigitalDate.of(7, 1, 1)
      ),
      Period.ofDays(3)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(6, 0, 2),
        ModernDigitalDate.of(7, 1, 1)
      ),
      Period.ofDays(1)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(6, 0, 1),
        ModernDigitalDate.of(7, 1, 1)
      ),
      Period.ofDays(2)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(5, 13, 28),
        ModernDigitalDate.of(6, 1, 1)
      ),
      Period.ofDays(2)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(5, 0, 1),
        ModernDigitalDate.of(6, 1, 1)
      ),
      Period.ofDays(1)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(30, 1, 1),
        ModernDigitalDate.of(0, 1, 1)
      ),
      Period.ofYears(-30)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(5, 9, 17),
        ModernDigitalDate.of(0, 6, 15)
      ),
      Period.of(-5, -3, -2)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(40, 10, 20),
        ModernDigitalDate.of(36, 10, 1)
      ),
      Period.of(-4, 0, -19)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(40, 2, 7),
        ModernDigitalDate.of(36, 10, 12)
      ),
      Period.of(-3, -4, -23)
    ),
    entry(
      Pair.of(
        ModernDigitalDate.of(8, 0, 1),
        ModernDigitalDate.of(7, 0, 1)
      ),
      Period.ofYears(-1)
    )
  );

  @Test
  void Until_WithValidDate_ProducesExpected()
  {
    for (Entry<Pair<ModernDigitalDate,ModernDigitalDate>,Period> e
      : VALID_DATES_TO_PERIOD.entrySet()
    ) {
      ModernDigitalDate startDate = e.getKey().first();
      ModernDigitalDate endDate = e.getKey().second();
      ChronoPeriod expected = e.getValue();
      ChronoPeriod actual = startDate.until(endDate);
      assertEquals(expected, actual);
    }
  }

  @Test
  void Plus_WithValidDate_ProducesExpected()
  {
    for (Entry<Pair<ModernDigitalDate,ModernDigitalDate>,Period> e
      : VALID_DATES_TO_PERIOD.entrySet()
    ) {
      ModernDigitalDate startDate = e.getKey().first();
      ModernDigitalDate expected = e.getKey().second();
      ChronoPeriod period = e.getValue();
      ModernDigitalDate actual = (ModernDigitalDate) startDate.plus(period);
      assertEquals(expected, actual);
    }
  }

  @Test
  void Minus_WithValidDate_ProducesExpected()
  {
    for (Entry<Pair<ModernDigitalDate,ModernDigitalDate>,Period> e
      : VALID_DATES_TO_PERIOD.entrySet()
    ) {
      ModernDigitalDate expected = e.getKey().first();
      ModernDigitalDate endDate = e.getKey().second();
      ChronoPeriod period = e.getValue();
      ModernDigitalDate actual = (ModernDigitalDate) endDate.minus(period);
      assertEquals(expected, actual);
    }
  }
}
