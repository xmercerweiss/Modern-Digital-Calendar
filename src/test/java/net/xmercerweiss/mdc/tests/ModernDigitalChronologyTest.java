/*
 *
 * BSD Zero-Clause License
 *
 * Copyright (C) 2025 Xavier Mercerweiss <mercerweissx@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose
 * with or without fee is hereby granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT,
 * OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package net.xmercerweiss.mdc.tests;

import java.util.*;
import java.util.Map.Entry;
import static java.util.Map.entry;
import java.time.DateTimeException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import net.xmercerweiss.mdc.*;
import net.xmercerweiss.mdc.utils.*;


/**
 * This class tests the most important of {@link ModernDigitalChronology}'s public utility methods
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-08
 */
public class ModernDigitalChronologyTest
{
  private static final ModernDigitalChronology CHRONO = ModernDigitalChronology.INSTANCE;

  private static final Map<Integer,Boolean> PROLEPTIC_TO_IS_LEAP_YEAR = Map.ofEntries(
    entry(-70, false),
    entry(-10, true),
    entry(-6, true),
    entry(-5, false),
    entry(-4, false),
    entry(-3, false),
    entry(-2, true),
    entry(-1, false),
    entry(0, false),
    entry(1, false),
    entry(2, true),
    entry(3, false),
    entry(4, false),
    entry(5, false),
    entry(6, true),
    entry(10, true),
    entry(30, true)
  );

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

  private static final Map<Pair<Integer,Integer>,Integer> MONTH_DAY_TO_DAY_OF_YEAR = Map.ofEntries(
    entry(Pair.of(1, 1), 1),
    entry(Pair.of(2, 1), 29),
    entry(Pair.of(2, 14), 42),
    entry(Pair.of(2, 17), 45),
    entry(Pair.of(3, 1), 57),
    entry(Pair.of(4, 1), 85),
    entry(Pair.of(5, 1), 113),
    entry(Pair.of(6, 1), 141),
    entry(Pair.of(7, 1), 169),
    entry(Pair.of(8, 1), 197),
    entry(Pair.of(9, 1), 225),
    entry(Pair.of(9, 10), 234),
    entry(Pair.of(10, 1), 253),
    entry(Pair.of(11, 1), 281),
    entry(Pair.of(12, 1), 309),
    entry(Pair.of(12, 17), 325),
    entry(Pair.of(13, 1), 337),
    entry(Pair.of(13, 23), 359),
    entry(Pair.of(0, 1), 365),
    entry(Pair.of(0, 2), 366)
  );

  private static final List<Pair<Integer,Integer>> INVALID_MONTH_DAYS = List.of(
    Pair.of(0, 0),
    Pair.of(0, 3),
    Pair.of(0, 10),
    Pair.of(0, 28),
    Pair.of(1, 0),
    Pair.of(1, -1),
    Pair.of(1, 29),
    Pair.of(2, -5),
    Pair.of(13, -28),
    Pair.of(-1, -1),
    Pair.of(-1, 0),
    Pair.of(-1, 7),
    Pair.of(-2, 1),
    Pair.of(-6, 14),
    Pair.of(14, 1),
    Pair.of(15, 8),
    Pair.of(28, 1)
  );

  private static final Map<Integer,Integer> DAY_OF_YEAR_TO_WEEK_OF_YEAR = Map.ofEntries(
    entry(1, 1),
    entry(2, 1),
    entry(7, 1),
    entry(8, 2),
    entry(14, 2),
    entry(15, 3),
    entry(21, 3),
    entry(22, 4),
    entry(28, 4),
    entry(29, 5),
    entry(36, 6),
    entry(43, 7),
    entry(50, 8),
    entry(57, 9),
    entry(64, 10),
    entry(71, 11),
    entry(78, 12),
    entry(85, 13),
    entry(92, 14),
    entry(99, 15),
    entry(106, 16),
    entry(113, 17),
    entry(120, 18),
    entry(127, 19),
    entry(134, 20),
    entry(141, 21),
    entry(148, 22),
    entry(155, 23),
    entry(162, 24),
    entry(169, 25),
    entry(176, 26),
    entry(364, 52),
    entry(365, 0),
    entry(366, 0)
  );

  private static final List<Integer> INVALID_DAYS_OF_YEAR = List.of(
    -1000,
    -365,
    -100,
    -50,
    -25,
    -10,
    -5,
    -4,
    -3,
    -2,
    -1,
    0,
    367,
    368,
    369,
    370,
    400,
    425,
    500,
    1000
  );

  @Test
  void IsLeapYear_FromProleptic_ProducesExpected()
  {
    for (Entry<Integer,Boolean> e : PROLEPTIC_TO_IS_LEAP_YEAR.entrySet())
    {
      int proleptic = e.getKey();
      boolean expected = e.getValue();
      boolean actual = CHRONO.isLeapYear(proleptic);
      assertEquals(expected, actual);
    }
  }

  @Test
  void FirstEpochDayOfYear_FromProleptic_ProducesExpected()
  {
    for (Entry<Integer,Long> e : PROLEPTIC_TO_FIRST_EPOCH_DAY.entrySet())
    {
      int proleptic = e.getKey();
      long expected = e.getValue();
      long actual = CHRONO.epochDay(
        proleptic,  // Year
        1,          // Month
        1           // Day
      );
      assertEquals(expected, actual);
    }
  }

  @Test
  void DayOfYear_FromMonthDay_ProducesExpected()
  {
    for (Entry<Pair<Integer,Integer>,Integer> e : MONTH_DAY_TO_DAY_OF_YEAR.entrySet())
    {
      Pair<Integer,Integer> monthDay = e.getKey();
      int expected = e.getValue();
      int actual = CHRONO.ordinalDayOfYear(
        monthDay.first(),  // Month of year
        monthDay.second()  // Day of month
      );
      assertEquals(expected, actual);
    }
  }

  @Test
  void DayOfYear_FromInvalid_ProducesError()
  {
    for (Pair<Integer,Integer> monthDay : INVALID_MONTH_DAYS)
    {
      try
      {
        CHRONO.ordinalDayOfYear(
          monthDay.first(),  // Month of year
          monthDay.second()  // Day of month
        );
        // If the call above succeeds, the expected error wasn't thrown
        fail("Invalid month-day did not produce DateTimeException");
      }
      catch (DateTimeException e)
      {
        // DateTimeException is the value expected to be caught
      }
      catch (Exception e)
      {
        // If an exception other than DateTimeException is thrown, something
        // has actually gone wrong
        fail(e);
      }
    }
  }

  @Test
  void WeekOfYear_FromDayOfYear_ProducesExpected()
  {
    for (Entry<Integer,Integer> e : DAY_OF_YEAR_TO_WEEK_OF_YEAR.entrySet())
    {
      int dayOfYear = e.getKey();
      int expected = e.getValue();
      int actual = CHRONO.ordinalWeekOfYear(dayOfYear);
      assertEquals(expected, actual);
    }
  }

  @Test
  void WeekOfYear_FromInvalid_ProducesError()
  {
    for (Integer dayOfYear : INVALID_DAYS_OF_YEAR)
    {
      try
      {
        CHRONO.ordinalWeekOfYear(dayOfYear);
        // If the call above succeeds, the expected error wasn't thrown
        fail("Invalid day-of-year call did not produce DateTimeException");
      }
      catch (DateTimeException e)
      {
        // DateTimeException is the value expected to be caught
      }
      catch (Exception e)
      {
        // If an exception other than DateTimeException is thrown, something
        // has actually gone wrong
        fail(e);
      }
    }
  }

}
