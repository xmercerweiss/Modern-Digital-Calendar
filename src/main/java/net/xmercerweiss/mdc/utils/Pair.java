package net.xmercerweiss.mdc.utils;


/**
 * A simple pair utility class
 * @param first The first value, must be of type T
 * @param second The second value, must be of type U
 * @param <T> The type of the first element
 * @param <U> The type of the second element
 * @author Xavier Mercerweiss
 * @version v1.0 2025-12-08
 */
public record Pair<T,U>(T first, U second)
{
  public static <T,U> Pair<T,U> of(T first, U second)
  {
    return new Pair<>(first, second);
  }
}
