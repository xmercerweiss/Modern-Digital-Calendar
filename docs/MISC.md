# Miscellany
A collection of unrelated notes on various unusual, unexpected, or otherwise potentially interesting
aspects of this library or the system which it implements.

## Internal Representation
This library represents all dates using a signed 64-bit integer representing the number of days before or since January 
1st, 1970 on the Gregorian calendar: the UNIX epoch. The epoch is of special significance to the MDC. This library does 
not implement timekeeping, only datekeeping, as the times of each day are kept according to the standard defined in ISO 
8601\. When keeping track of the time of day while using this library, you must use a separate `java.time.LocalTime` object, as 
no `ModernDigitalDateTime` or `ModernDigitalTime` objects are provided.
