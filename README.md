# The Modern Digital Calendar
## Introduction
This document describes the calendar system this library implements. For installation and usage instructions, see [`TECHNICAL.md`](TECHNICAL.md).

The Modern Digital Calendar is a variation on the [International Fixed Calendar](https://en.wikipedia.org/wiki/International_Fixed_Calendar) paired with new month names and a set year 0 in order to produce a more structured and less ambiguous means of datekeeping. It is, in essence, this nerd's wishlist for calendar reform worked into a single, unified system. 

This project was not created under any delusion of future mass adoption, it's purely a passion project for personal use. However, if you have any thoughts on or uses for this project, I'd love to hear about them! Shoot me an email at `mercerweissx@gmail.com`, and I'll respond ASAIFLI (as soon as I feel like it.) Any part of this system or project my be used however you like, with or without my permission. 

## Internal Representation
The MDC represents all dates using a signed 64-bit integer representing the number of days before or since January 1st, 1970 on the Gregorian calendar: the UNIX epoch. The epoch is of special significance to the MDC. This library does not implement timekeeping, only datekeeping, as the times of each day are kept exactly the same as they are normally. When keeping track of the time of day while using this library, you must use a separate `java.time.LocalTime` object, as no `ModernDigitalDatetime` object is provided.

## Datekeeping
### Year 0
In the MDC, the UNIX epoch is the basis for year 0. 1970 CE Gregorian becomes 0 SE, 0 years _Since Epoch_. Years before 1970 are unceremoniously referred to as a number of years BE, _Before Epoch_. Is it heretical to elevate the UNIX epoch to the birth of Christ? This is left as a question for the reader.

### Leap Days
Keeping with the [International Fixed Calendar](https://en.wikipedia.org/wiki/International_Fixed_Calendar), every year includes one leap day not belonging to any week, weekday, or month. Additional leap days, also not belonging to any week or month, are added with the same frequency as in the Gregorian Calendar. While it complicates the math somewhat, leap years coincide between the Gregorian and Modern Digital Calendars. As a result, years 2, 6, 10, etc. SE **are** leap years, though the same rules for frequency apply to the MDC. In order to determine if a year is a leap year in the MDC, 1970 to the given year must be added before performing the standard calculations. Yes, this is somewhat inelegant, but aligning leap years with the Gregorian calendar helps make the MDC more practical. 

All leap days occur consecutively at the end of every year, and may be denoted as Leap 1st, Leap 2nd, etc. of a given year. Notes
on representation of leap days follows later in this document.
### Months
Years each have 13 months of 28 days, in accordance with the [IFC](https://en.wikipedia.org/wiki/International_Fixed_Calendar). However, all weeks (and therefore months) begin on Monday rather than Sunday, matching the weekday numbering system prescribed by [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601#Week_dates). Every month starts on Monday the 1st and ends on Sunday the 28th. The following table lists the enumerated value of each day of the week.
| Value | Day of Week |
| -----:| ----------- |
| 0 | None (Leap Days) |
| 1 | Monday |
| 2 | Tuesday |
| 3 | Wednesday |
| 4 | Thursday |
| 5 | Friday |
| 6 | Saturday |
| 7 | Sunday |

Months are counted starting from 1, and are renamed to reflect their order in the year. The following names are based on the Greek and Latin names of the position of each month in the year.
| Value | Month of Year | Common Year Gregorian Equivalent |
| --------:| ----------- | ---------------------------------- |
| 1       | Unitary     | Jan. 1st to Jan. 28th              |
| 2       | Duotary     | Jan. 29th to Feb. 25th             |
| 3       | Tertiary    | Feb. 26th to March 25th            |
| 4       | Quartuary   | March 26th to April 22nd           |
| 5       | Pentuary    | April 23rd to May 20th             |
| 6       | Hextuary    | May 21st to June 17th              |
| 7       | September   | June 18th to July 15th             |
| 8       | October     | July 16th to Aug. 12th             |
| 9       | November    | Aug. 13th to Sep. 9th              |
| 10       | December    | Sep. 10th to Oct. 7th              |
| 11       | Hendecember | Oct. 8th to Nov. 4th               |
| 12       | Dodecember  | Nov. 5th to Dec. 2nd               |
| 13       | Tredecember | Dec. 3rd to Dec. 30th              |
| 0       | Leap Day | Dec. 31st                          |

| Value | Month of Year | Leap Year Gregorian Equivalent |
| --------:| ----------- | ---------------------------------- |
| 1       | Unitary     | Jan. 1st to Jan. 28th              |
| 2       | Duotary     | Jan. 29th to Feb. 25th             |
| 3       | Tertiary    | Feb. 26th to March 24th            |
| 4       | Quartuary   | March 25th to April 21st           |
| 5       | Pentuary    | April 22nd to May 19th             |
| 6       | Hextuary    | May 20th to June 16th              |
| 7       | September   | June 17th to July 14th             |
| 8       | October     | July 15th to Aug. 11th             |
| 9       | November    | Aug. 12th to Sep. 8th              |
| 10       | December    | Sep. 9th to Oct. 6th              |
| 11       | Hendecember | Oct. 7th to Nov. 3rd               |
| 12       | Dodecember  | Nov. 4th to Dec. 1st               |
| 13       | Tredecember | Dec. 2nd to Dec. 29th              |
| 0       | Leap Days | Dec 30th, Dec. 31st                   |

September through December are kept, but shifted to the correct positions based on their etymology. Notice that the first half-ish of the year uses months ending with -ary, and the second with -ember. This was simply a stylistic choice made to maintain the "feeling" of the months of the year. All months are abbreviated to their first 3 letters, with the exception of Tredecember, abbrevdiated as "Tred." to prevent confusion with Tertiary.

Notice also that leap days are regarded as belonging to the "0th" month. This is done such that they may be easily referenced in standard `YYYY-MM-DD` notation. While it is somewhat frustrating to have the "0th" month come at the end of the year, 0 is the most reasonable value for days belonging to no month and datekeeping would be further complicated by placing leap days at the beginning of the year. While most years will only have one 1 leap day, and all others only 2, this notation also allows the keeping of an arbitrarily large number of leap days if ever necessary. 
