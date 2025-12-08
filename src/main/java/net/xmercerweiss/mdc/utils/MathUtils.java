package net.xmercerweiss.mdc.utils;


/**
 * A utility class of simple mathematical functions.
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-08
 */
public class MathUtils
{
  public static int narrowLong(long n)
  {
    if (n < Integer.MIN_VALUE)
      return Integer.MIN_VALUE;
    else if (n > Integer.MAX_VALUE)
      return Integer.MAX_VALUE;
    else
      return (int) n;
  }
}
