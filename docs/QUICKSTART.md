# Quickstart Guide
The primary class of this library is the immutable `ModernDigitalDate` object, which is used much the
same as a standard `LocalDate`. This is intentional, this library is built on top of the API provided 
by [`java.time.chrono`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/time/chrono/package-summary.html).

This project provides [full API documentation](https://xmercerweiss.github.io/mdc/net/xmercerweiss/mdc/package-summary.html)
for ease of use.

## Creating a Date
Instances of `ModernDigitalDate` can be created in a multitude of ways, each by way of a different
static factory method. 
```java
import java.time.LocalDate;
import net.xmercerweiss.ModernDigitalDate;


public class Quickstart
{
  public static void main(String[] args)
  {
    LocalDate bday = LocalDate.of(2006, 9, 10);  // My birthday
    ModernDigitalDate modernBday = ModernDigitalDate.from(bday);

    ModernDigitalDate newYearsDay = ModernDigitalDate.of(56, 1, 1);
    // Uni. 1st, 56 SE, the MDC equivalent of Jan. 1st, 2026 ISO
    
    ModernDigitalDate today = ModernDigitalDate.now();
    
    ModernDigitalDate fromEpoch = ModernDigitalDate.ofEpochDay(1000);
      // 1000 days after Jan. 1st, 1970 ISO
  }
}
```

## Reading a Date
Any valid field of a `ModernDigitalDate` can be read using the 
[`.get(TemporalField)`](https://xmercerweiss.github.io/mdc/net/xmercerweiss/mdc/ModernDigitalDate.html#get(java.time.temporal.TemporalField)) 
method. However, for the sake of readability, each field may also be read explicitly.
```java
import net.xmercerweiss.ModernDigitalDate;


public class Quickstart
{
  public static void main(String[] args)
  {
    ModernDigitalDate date = ModernDigitalDate.of(-30, 13, 25);
      // Tred. 25th, 30 BE
    
    date.getEra();        // BEFORE_EPOCH
    date.getYear();       // -30
    date.getYearOfEra();  // 30
    date.getMonth();      // 13
    date.getDayOfMonth(); // 25
    date.getDayOfYear();  // 361
      // etc.
  }
}
```

## Formatting a Date
Lastly, each `ModernDigitalDate` can be formatted into a string using the [formatting syntax](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/time/format/DateTimeFormatter.html#patterns)
of the Java standard library's `DateTimeFormatter`; or, a `DateTimeFormatter` itself may be given.

Note that the `ModernDigitalDate` _does not_ actually call the formatter if given; it simply extracts 
its formatting pattern and passes it to its own formatting implementation.
```java
import java.time.format.DateTimeFormatter;
import net.xmercerweiss.mdc.ModernDigitalDate;


public class Quickstart
{
  public static void main(String[] args)
  {
    ModernDigitalDate date = ModernDigitalDate.now();
    DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
    String pattern = "EEEE, MMMM d, y G";
    
    System.out.println(date.format(fmt));
      // 0055-13-07
    
    System.out.println(date.format(pattern));
      // Sunday, Tredecember 7, 55 SE
  }
}
```