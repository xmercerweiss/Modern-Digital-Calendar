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
import java.time.*;
import static java.util.Map.entry;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import net.xmercerweiss.mdc.*;


/**
 * This class tests {@link ModernDigitalDate}'s static factory methods
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-08
 */
public class ModernDigitalDateCreationTest
{
  private static final List<Integer[]> VALID_DATE_FIELDS = List.of(
      new Integer[]{1, 0, 1, 1},
      new Integer[]{1, 0, 1, 1},
      new Integer[]{0, 1, 1, 1},
      new Integer[]{1, 0, 0, 1},
      new Integer[]{0, 2, 0, 2},
      new Integer[]{1, 1, 1, 1},
      new Integer[]{1, 30, 1, 1},
      new Integer[]{1, 50, 12, 28},
      new Integer[]{1, 10, 6, 1},
      new Integer[]{1, 30, 13, 14},
      new Integer[]{1, 1000, 13, 28},
      new Integer[]{0, 5, 10, 28},
      new Integer[]{0, 1, 6, 1},
      new Integer[]{0, 10, 13, 17},
      new Integer[]{0, 100, 7, 20},
      new Integer[]{0, 1970, 1, 1}
  );

  private static final List<Integer[]> INVALID_DATE_FIELDS = List.of(
    new Integer[]{1, 0, 0, 0},  // Invalid leap-day "0"
    new Integer[]{1, 0, 0, 2},  // Invalid 2nd leap-day in common year
    new Integer[]{1, 0, 0, 3},  // Invalid "3rd" leap-day in common year
    new Integer[]{1, 2, 0, 3},  // Invalid "3rd" leap-day in leap year
    new Integer[]{1, 0, 0, 4},  // Invalid "4th" leap-day in common year
    new Integer[]{1, 2, 0, 4},  // Invalid "4th" leap-day in leap year
    new Integer[]{1, 0, 0, 28},  // Invalid "28th" leap-day
    new Integer[]{1, 0, 1, 29},  // Invalid date Unitary 29th
    new Integer[]{1, 0, 1, 31},  // Invalid date Unitary 31st
    new Integer[]{1, 0, 2, 29},  // Invalid date Duotary 29th
    new Integer[]{1, 0, 14, 1},  // Invalid "14th" month
    new Integer[]{0, -1, 1, 1},  // Invalid "-1st" year BE
    new Integer[]{1, -1, 1, 1},  // Invalid "-1st" year SE
    new Integer[]{1, 0, -1, 1},  // Invalid "-1st" month
    new Integer[]{0, 1, 1, -10}  // Invalid date Unitary "-10th"
  );

  private static final Map<Long,Integer[]> EPOCH_DAY_TO_DATE_FIELDS = Map.ofEntries(
    entry(
      -3653L,
      new Integer[]{0, 10, 1, 1}
    ),
    entry(
      -731L,
      new Integer[]{0, 2, 1, 1}
    ),
    entry(
      -366L,
      new Integer[]{0, 2, 0, 2}
    ),
    entry(
      -365L,
      new Integer[]{0, 1, 1, 1}
    ),
    entry(
      -183L,
      new Integer[]{0, 1, 7, 15}
    ),
    entry(
      -1L,
      new Integer[]{0, 1, 0, 1}
    ),
    entry(
      0L,
      new Integer[]{1, 0, 1, 1}
    ),
    entry(
      1L,
      new Integer[]{1, 0, 1, 2}
    ),
    entry(
      364L,
      new Integer[]{1, 0, 0, 1}
    ),
    entry(
      365L,
      new Integer[]{1, 1, 1, 1}
    ),
    entry(
      730L,
      new Integer[]{1, 2, 1, 1}
    ),
    entry(
      731L,
      new Integer[]{1, 2, 1, 2}
    ),
    entry(
      1095L,
      new Integer[]{1, 2, 0, 2}
    ),
    entry(
      1096L,
      new Integer[]{1, 3, 1, 1}
    ),
    entry(
      3652L,
      new Integer[]{1, 10, 1, 1}
    ),
    entry(
      10956L,
      new Integer[]{1, 29, 0, 1}
    ),
    entry(
      10957L,
      new Integer[]{1, 30, 1, 1}
    ),
    entry(
      11322L,
      new Integer[]{1, 30, 0, 2}
    ),
    entry(
      11323L,
      new Integer[]{1, 31, 1, 1}
    )
  );

  private static Integer[] dateToFieldsArray(ModernDigitalDate date)
  {
    return new Integer[]{
      date.getEra().getValue(),
      date.getYearOfEra(),
      date.getMonth(),
      date.getDayOfMonth()
    };
  }

  @Test
  void DateOf_WithValidFields_ProducesExpected()
  {
    for (Integer[] expected : VALID_DATE_FIELDS)
    {
      ModernDigitalDate date = ModernDigitalDate.of(
        ModernDigitalEra.ofValue(expected[0]),
        expected[1],
        expected[2],
        expected[3]
      );
      Integer[] actual = dateToFieldsArray(date);
      assertArrayEquals(expected, actual);
    }
  }

  @Test
  void DateOf_WithInvalidFields_ProducesError()
  {
    for (Integer[] expected : INVALID_DATE_FIELDS)
    {
      try
      {
        ModernDigitalDate.of(
          ModernDigitalEra.ofValue(expected[0]),
          expected[1],
          expected[2],
          expected[3]
        );
        // If the call above succeeds, the expected error wasn't thrown
        fail("Invalid date fields did not produce DateTimeException");
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
  void DateOfEpochDay_WithValidLong_ProducesExpected()
  {
    for (Entry<Long, Integer[]> e : EPOCH_DAY_TO_DATE_FIELDS.entrySet())
    {
      long epochDay = e.getKey();
      ModernDigitalDate date = ModernDigitalDate.ofEpochDay(epochDay);
      Integer[] expected = e.getValue();
      Integer[] actual = dateToFieldsArray(date);
      assertArrayEquals(expected, actual);
    }
  }

  @Test
  void Now_ToEpochDay_ProducesExpected()
  {
    long expected= LocalDate.now().toEpochDay();
    long actual = ModernDigitalDate.now().toEpochDay();
    assertEquals(expected, actual);
  }
}
