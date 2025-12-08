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
