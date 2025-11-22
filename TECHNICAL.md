# Technical Documentation: `net.xmercerweiss.mdc`
## Introduction
This document contains the technical specifications and installation and usage instructions for this library. For a description of the calendar system it implements, see [`README.md`](README.md).

## Installation
[Update when ready]

## Usage
[Update when ready]

## Internal Representation
The MDC represents all dates using a signed 64-bit integer representing the number of days before or since January 1st, 1970 on the Gregorian calendar: the UNIX epoch. The epoch is of special significance to the MDC. This library does not implement timekeeping, only datekeeping, as the times of each day are kept exactly the same as they are normally. When keeping track of the time of day while using this library, you must use a separate `java.time.LocalTime` object, as no `ModernDigitalDatetime` object is provided.