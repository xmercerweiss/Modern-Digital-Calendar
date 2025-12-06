# Miscellany
A collection of unrelated notes on various unusual, unexpected, or otherwise potentially interesting
aspects of this library or the system which it implements.

## Internal Representation
This library represents all dates using a signed 64-bit integer representing the number of days before or since January 
1st, 1970 on the Gregorian calendar: the UNIX epoch. The epoch is of special significance to the MDC. This library does 
not implement timekeeping, only datekeeping, as the times of each day are kept according to the standard defined in ISO 
8601\. When keeping track of the time of day while using this library, you must use a separate `java.time.LocalTime` object, as 
no `ModernDigitalDateTime` or `ModernDigitalTime` objects are provided.

## ChronoPeriods
Functions such as `plus`, `minus`, and `until` in the `ModernDigitalDate` class, plus their
supporting methods in `ModernDigitalChronology`, work with `ChronoPeriods` from the `java.time`
library. Special care must be taken when using these methods, as their behavior may be unintuitive.

`ChronoPeriods`, specifically `Period` objects, store lengths of time as years, months, and days.
For ease of use, `ModernDigitalDates` working with `Periods` treat years, months, and, by extension, 
weeks as _conceptual_ units rather than literal ones. 

This means that, for _most_ uses, each year of a `Period` __does not represent 365 or 366 days.__ 
Rather, it represents the concept of a year; a `Period` of "3 years" means that two dates have a 
difference of 3 between their years, but not necessarily that there are 1095 (365*3) days between 
them. This is due to the fact that leap days (in both leap and common years) belong to no month
or week. 

`Periods` of months work the same way; a `Period` of "4 months" will not always be 112 (28*4) days, 
it may be anywhere between 112-114 days due to the possible number of leap days within those 4 conceptual
months.

I have attempted to design `plus`, `minus`, and `until` to be internally consistent, meaning given dates 
`A` and `B`, if `p = A.until(B)`, then `A.plus(p) == B`, and `B.minus(p) == A`. This should make most practical
applications of these methods useful.I have included unit tests to ensure this property remains true.

When in doubt, use `ChronoPeriods` when working with years and months, and `epochDays` when working
with days. This _should_ ensure accuracy for non-critical applications. If, for whatever reason, you
_need_ 100% accuracy while also working with the MDC, store dates as `LocalDates` and simply convert to a
`ModernDigitalDate` for display using the `epochDay`. This is the approach recommended by the [official Java
documentation](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/time/chrono/package-summary.html)
on non-standard calendar systems.
