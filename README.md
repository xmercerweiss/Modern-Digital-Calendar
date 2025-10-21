## Introduction
The Modern Digital Calendar is a variation on the [International Fixed Calendar](https://en.wikipedia.org/wiki/International_Fixed_Calendar) paired with new month names, a set year 0, military timekeeping, and the removal of DST in order to produce a more structured and less ambiguous means of timekeeping. It is, in essence, a nerd's wish list for calendar reform worked into a single, unified system. 

This project is not created under any delusion of future mass adoption, it's purely a passion project for personal use. However, if you have any thoughts or uses regarding this system, I'd love to hear about them! Shoot me an email at `mercerweissx@gmail.com`, and I'll respond ASAIFLI (as soon as I feel like it.) Any part of this system or project my be used however you like, with or without my permission. 

## Internal Representation
The MDC represents all times using a signed 64-bit integer representing the number of seconds before or since January 1st, 1970 on the Gregorian calendar, the UNIX epoch. This is the the standard means by which all modern computers represent time, though it's of special significance to the MDC.

## Datekeeping
### Year 0
In the MDC, the UNIX epoch is the basis for year 0. 1970 CE Gregorian becomes 0 SE, 0 years _Since Epoch_. Years before 1970 are unceremoniously referred to as a number of years BE, _Before Epoch_. Is it heretical to compare the UNIX epoch to the birth of Christ? This is left as a question for the reader.

### Leap Days
Keeping with the [International Fixed Calendar](https://en.wikipedia.org/wiki/International_Fixed_Calendar), every year includes one leap day not belonging to any week or month. Additional leap days, also not belonging to any week or month, are added with the same frequency as in the Gregorian Calendar. While it complicates the math somewhat, leap years coincide between the Gregorian and Modern Digital Calendars. As a result, years 2, 6, 10, etc. SE **are** leap years, though the same rules for frequency apply to the MDC. In order to determine if a year is a leap year in the MDC, 1970 must be added before performing the standard calculations. Yes, this is inelegant. Yes, it irks me too.

All leap days occur consecutively at the end of every year, and may be denoted as Leap 1st, Leap 2nd, etc. of a given year. Notes
on representation of leap days follows later in this document.
### Months
Years each have 13 months of 28 days, in accordance with the [IFC](https://en.wikipedia.org/wiki/International_Fixed_Calendar). However, all weeks (and therefore months) begin on Monday rather than Sunday, matching the weekday numbering system prescribed by [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601#Week_dates). Every month starts on Monday the 1st and ends on Sunday the 28th.

Months are counted starting from 1, and are renamed to reflect their order in the year. The following names are based on the Greek and Latin names of numbers.
| Position | Name        | Gregorian Equivalent (Common Year) |
| -------- | ----------- | ---------------------------------- |
| 01       | Unitary     | Jan. 1st to Jan. 28th              |
| 02       | Duotary     | Jan. 29th to Feb. 25th             |
| 03       | Tertiary    | Feb. 26th to March 25th            |
| 04       | Quartuary   | March 26th to April 22nd           |
| 05       | Pentuary    | April 23rd to May 20th             |
| 06       | Hextuary    | May 21st to June 17th              |
| 07       | September   | June 18th to July 15th             |
| 08       | October     | July 16th to Aug. 12th             |
| 09       | November    | Aug. 13th to Sep. 9th              |
| 10       | December    | Sep. 10th to Oct. 7th              |
| 11       | Hendecember | Oct. 8th to Nov. 4rd               |
| 12       | Dodecember  | Nov. 5th to Dec. 2st               |
| 13       | Tredecember | Dec. 3nd to Dec. 30th              |
| 14       | Leap Day(s) | Dec. 31st                          |

September through December are kept, but shifted to the correct positions based on their etymology. Notice that the first half-ish of the year uses months ending with -ary, and the second with -ember. This was simply a stylistic choice made to maintain the "feeling" of the months of the year. All months are abbreviated to their first 3 letters, with the exception of Tredecember, abbrevdiated as "Tred." to prevent confusion with Tertiary.

Notice also that leap days are regarded as position 14. This is done such that they may be easily referenced in standard `YYYY-MM-DD` notation. While 0 would have been better fitting a set of days associated with no month, it would be more frustrating to have the last days of the year be referenced with the smallest number. While most years will only have one 1 leap day, and all others only 2, this notation also allows the reference to an arbitrarily large number of leaps, if ever necessary. 
