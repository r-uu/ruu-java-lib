package de.ruu.lib.util;

import static de.ruu.lib.util.BooleanFunctions.not;

public interface StringBuffers
{
	public static StringBuffer sb() { return new StringBuffer(); }

	public static StringBuffer sb(String string) { return new StringBuffer(string); }

	public static boolean isNullOrEmpty   (StringBuffer sb) { return sb == null || sb.toString().isEmpty(); }
	public static boolean isNotNullOrEmpty(StringBuffer sb) { return not(isNullOrEmpty(sb)); }

	public static boolean isNullOrEmptyOrBlank(StringBuffer sb) { return isNullOrEmpty(sb) || sb.toString().isBlank(); }

	public static boolean startsWith(StringBuffer stringBuffer, String prefix)
	{
		if (isNullOrEmptyOrBlank(stringBuffer))
		{
			return false;
		}

		return stringBuffer.toString().startsWith(prefix);
	}

	public static boolean startsWithUpper(StringBuffer stringBuffer)
	{
		if (isNullOrEmptyOrBlank(stringBuffer))
		{
			return false;
		}

		return Character.isUpperCase(stringBuffer.charAt(0));
	}

	public static boolean startsWithLower(StringBuffer stringBuffer)
	{
		if (isNullOrEmptyOrBlank(stringBuffer))
		{
			return false;
		}

		return Character.isLowerCase(stringBuffer.charAt(0));
	}

	public static boolean endsWith(StringBuffer stringBuffer, String suffix)
	{
		if (isNullOrEmptyOrBlank(stringBuffer))
		{
			return false;
		}

		return stringBuffer.toString().endsWith(suffix);
	}

	/**
	 * @param input string buffer to be trimmed
	 * @param charsToTrim characters to be removed from the left end of {@code input}
	 * @return trimmed {@code input} string buffer
	 */
	public static StringBuffer lTrimChars(StringBuffer input, String charsToTrim)
	{
		if (isNullOrEmpty(input) && !Strings.isNullOrEmpty(charsToTrim))
		{
			return trimChars(input, charsToTrim, true);
		}

		return input;
	}

	/**
	 * @param input string buffer to be trimmed
	 * @param charsToTrim characters to be removed from the right end of {@code input}
	 * @return trimmed {@code input} string buffer
	 */
	public static StringBuffer rTrimChars(StringBuffer input, String charsToTrim)
	{
		if (!isNullOrEmpty(input) && !Strings.isNullOrEmpty(charsToTrim))
		{
			return trimChars(input, charsToTrim, false);
		}

		return input;
	}

	/**
	 * @param input string buffer to be trimmed
	 * @param charsToTrim characters to be removed from the right and left end of {@code input}
	 * @return trimmed {@code input} string buffer
	 */
	public static StringBuffer lrTrimChars(StringBuffer input, String charsToTrim)
	{
		if (!isNullOrEmpty(input) && !Strings.isNullOrEmpty(charsToTrim))
		{
			return trimChars(trimChars(input, charsToTrim, false), charsToTrim, true);
		}

		return input;
	}

	public static StringBuffer replace(StringBuffer stringBuffer, String oldString, String newString)
	{
		return new StringBuffer(stringBuffer.toString().replace(oldString, newString));
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
	 * @return trimmed input string buffer
	 */
	private static StringBuffer trimChars(StringBuffer input, String charsToTrim, boolean fromFront)
	{
		// recursive calls may lead to empty input
		if (input.length() == 0)
		{
			return new StringBuffer();
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