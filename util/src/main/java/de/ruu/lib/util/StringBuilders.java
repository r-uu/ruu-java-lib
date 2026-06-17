package de.ruu.lib.util;

import lombok.NonNull;

public interface StringBuilders
{
	public static StringBuilder sb() { return new StringBuilder(); }

	public static StringBuilder sb(@NonNull String string) { return new StringBuilder(string); }

	public static boolean isNullOrEmpty(StringBuilder sb) { return sb == null || sb.toString().isEmpty(); }

	public static boolean isNullOrEmptyOrBlank(StringBuilder sb) { return isNullOrEmpty(sb) || sb.toString().isBlank(); }

	public static boolean startsWith(StringBuilder StringBuilder, String prefix)
	{
		if (isNullOrEmptyOrBlank(StringBuilder))
		{
			return false;
		}

		return StringBuilder.toString().startsWith(prefix);
	}

	public static boolean startsWithUpper(StringBuilder StringBuilder)
	{
		if (isNullOrEmptyOrBlank(StringBuilder))
		{
			return false;
		}

		return Character.isUpperCase(StringBuilder.charAt(0));
	}

	public static boolean startsWithLower(StringBuilder StringBuilder)
	{
		if (isNullOrEmptyOrBlank(StringBuilder))
		{
			return false;
		}

		return Character.isLowerCase(StringBuilder.charAt(0));
	}

	public static boolean endsWith(StringBuilder StringBuilder, String suffix)
	{
		if (isNullOrEmptyOrBlank(StringBuilder))
		{
			return false;
		}

		return StringBuilder.toString().endsWith(suffix);
	}

	/**
	 * @param input string builder to be trimmed
	 * @param charsToTrim characters to be removed
	 * @return trimmed input string builder
	 */
	public static StringBuilder rTrimChars(StringBuilder input, String charsToTrim)
	{
		if (!isNullOrEmpty(input) && !Strings.isNullOrEmpty(charsToTrim))
		{
			return trimChars(input, charsToTrim, false);
		}

		return input;
	}

	public static StringBuilder replace(StringBuilder StringBuilder, String oldString, String newString)
	{
		return new StringBuilder(StringBuilder.toString().replace(oldString, newString));
	}

	/**
	 * Recursive implementation!
	 * <p>
	 * Removes the characters in <code>charsToTrim</code> from front or back of
	 * <code>input</code>.
	 *
	 * @param input
	 * @param charsToTrim
	 * @param fromFront
	 * @return trimmed input string builder
	 */
	private static StringBuilder trimChars(StringBuilder input, String charsToTrim, boolean fromFront)
	{
		// recursive calls may lead to empty input
		if (input.length() == 0)
		{
			return new StringBuilder();
		}

		char[] charsToTrimAsArray = charsToTrim.toCharArray();

		// test each character if it has to be trimmed from front / back of input
		for (char c : charsToTrimAsArray)
		{
			if (fromFront)
			{
				if (input.charAt(0) == c)
				{
					// found character to be trimmed from front, delete that character
					// and start recursive call
					input.deleteCharAt(0);
					return trimChars(input, charsToTrim, fromFront);
				}
			}
			else
			{
				int lastCharPos = input.length() - 1;
				if (input.charAt(lastCharPos) == c)
				{
					// found character to be trimmed from back, delete that character
					// and start recursive call
					input.deleteCharAt(lastCharPos);
					return trimChars(input, charsToTrim, fromFront);
				}
			}
		}

		return input;
	}
}