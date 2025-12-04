# Technical Documentation: `net.xmercerweiss.mdc`
This document describes this project technically. For conceptual documentation, see [the README](../README.md).

## Introduction
This is an index of the technical documentation for the [Java implementation of the Modern Digital Calendar](https://github.com/xmercerweiss/Modern-Digital-Calendar).
Installation instructions, API references, and examples of valid usage are linked to by this index.

## Index
- [User Manual](#user-manual)
    - [Installation](INSTALL.md)
    - [Quickstart](QUICKSTART.md)
    - [Miscellany](MISC.md)
- [API References](#api-references)
    - [ModernDigitalChronology](api/ModernDigitalChronology.md)
    - [ModernDigitalDate](api/ModernDigitalDate.md)
    - [ModernDigitalEra](api/ModernDigitalEra.md)
    - [CustomField](api/CustomField.md)
    - [CustomUnit](api/CustomUnit.md)

### User Manual
Below is a list of documents instructing the user how to use this library.
- [Installation](INSTALL.md), this library's installation instructions.
- [Quickstart](QUICKSTART.md), a set of basic usage examples for this library.
- [Miscellany](MISC.md), a set of notes on quirks of this library.

### API References
Below is a list of documents each describing, in detail, the interface of a given class of this library.
- [ModernDigitalChronology](api/ModernDigitalChronology.md), the singleton class representing the Modern Digital Calendar system as a whole.
- [ModernDigitalDate](api/ModernDigitalDate.md), the class which stores individual immutable dates. This library's parallel to `LocalDate`.
- [ModernDigitalEra](api/ModernDigitalEra.md), the enum representing each era of the Modern Digital Calendar.
- [CustomField](api/CustomField.md), an enum representing custom `TemporalFields`. Not public; for internal use only.
- [CustomUnit](api/CustomUnit.md), an enum representing custom `TemporalUnits`. Not public; for internal use only.
