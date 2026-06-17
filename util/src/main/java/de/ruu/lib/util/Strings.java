package de.ruu.lib.util;

import static de.ruu.lib.util.BooleanFunctions.not;
import static de.ruu.lib.util.StringBuffers.sb;
import static java.util.Objects.isNull;

public interface Strings
{
	static String firstLetterToLowerCase(String string)
	{
		if (string == null) throw new IllegalArgumentException("param must not be null");
		if (string.equals("")) return string;
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}

	static String firstLetterToUpperCase(String string)
	{
		if (string == null) throw new IllegalArgumentException("param must not be null");
		if (string.equals("")) return string;
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	static String indent(String string, String indent, int indentLevel)
	{
		if (isNullOrEmptyOrBlank(string)) return string;
		if (indentLevel < 0) return string;

		String indentation = indent.repeat(indentLevel);

		return
				indentation +      // indent first line
						string.replaceAll( // indent following lines
								"\\n",
								"\n" + indentation);
	}

	/**
	 * Removes the characters in <code>charsToTrim</code> from both sides of <code>input</code>.
	 * <p>
	 * Example: abcxxxcba, bca -> xxx
	 *
	 * @param input string to be trimmed
	 * @param charsToTrim characters to be removed
	 *
	 * @return trimmed input string
	 */
	static String allTrimChars(String input, String charsToTrim)
	{
		return lrTrimChars(input, charsToTrim);
	}

	/**
	 * Removes the characters in <code>charsToTrim</code> from both sides of <code>input</code>.
	 *
	 * @param input string to be trimmed
	 * @param charsToTrim characters to be removed
	 *
	 * @return trimmed input string
	 *
	 * @see Strings#allTrimChars(String, String)
	 */
	static String lrTrimChars(String input, String charsToTrim)
	{
		return rTrimChars(lTrimChars(input, charsToTrim), charsToTrim);
	}

	/**
	 * Removes the characters in <code>charsToTrim</code> from left side of <code>input</code>.
	 *
	 * @param input string to be trimmed
	 * @param charsToTrim characters to be removed
	 *
	 * @return trimmed <code>input</code> string, <code>input</code> if <code>input</code> was <code>null</code> or empty
	 *         or blank
	 */
	static String lTrimChars(String input, String charsToTrim)
	{
		if (isNullOrEmptyOrBlank(input))
		{
			return input;
		}
		else
		{
			return trimChars(sb(input), charsToTrim, true).toString();
		}
	}

	/**
	 * Removes the characters in <code>charsToTrim</code> from right side of <code>input</code>.
	 *
	 * @param input string to be trimmed
	 * @param charsToTrim characters to be removed
	 *
	 * @return trimmed <code>input</code> string, <code>input</code> if <code>input</code> was <code>null</code> or empty
	 *         or blank
	 */
	static String rTrimChars(String input, String charsToTrim)
	{
		if (isNullOrEmptyOrBlank(input))
		{
			return input;
		}
		else
		{
			return trimChars(sb(input), charsToTrim, false).toString();
		}
	}

	/**
	 * @param input
	 * @param fillChar
	 * @param targetLength
	 * @return filled string
	 */
	static String lFillCharsTargetLength(String input, char fillChar, int targetLength)
	{
		if (input.length() >= targetLength) { return input; }

		return ("" + fillChar).repeat(targetLength - input.length()) + input;
	}

	/**
	 * @param input
	 * @param fillChar
	 * @param targetLength
	 * @return filled string
	 */
	static String rFillCharsTargetLength(String input, char fillChar, int targetLength)
	{
		if (input.length() >= targetLength) { return input; }

		return input + ("" + fillChar).repeat(targetLength - input.length());
	}

	static boolean isNullOrEmpty(String string)           { return isNull(string) || string.isEmpty(); }

	static boolean isNullOrBlank(String string)           { return isNull(string) || string.isBlank(); }

	static boolean isNullOrEmptyOrBlank(String string)    { return isNullOrEmpty(string) || string.isBlank(); }

	static boolean isEmptyOrBlank(String string)          { return string.isEmpty() || string.isBlank(); }

	static boolean isNotNullOrEmpty(String string)        { return not(isNullOrEmpty(string)); }

	static boolean isNotNullOrBlank(String string)        { return not(isNullOrBlank(string)); }

	static boolean isNotNullOrEmptyOrBlank(String string) { return not(isNullOrEmptyOrBlank(string)); }

	static boolean isNotEmptyOrBlank(String string)       { return not(isEmptyOrBlank(string)); }

	static boolean isNullSafeEquals(String nullSafeString, String other)
	{
		if (nullSafeString == null) return other == null;
		return nullSafeString.equals(other);
	}

	/**
	 * Recursive implementation!
	 * <p>
	 * Removes the characters in <code>charsToTrim</code> from front or back of <code>input</code>.
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
					// found character to be trimmed from front, delete that character and start recursive call
					input.deleteCharAt(0);
					return trimChars(input, charsToTrim, fromFront);
				}
			}
			else
			{
				int lastCharPos = input.length() - 1;
				if (input.charAt(lastCharPos) == c)
				{
					// found character to be trimmed from back, delete that character and start recursive call
					input.deleteCharAt(lastCharPos);
					return trimChars(input, charsToTrim, fromFront);
				}
			}
		}

		return input;
	}

	/**
	 * @param input
	 * @param charToTrim
	 * @return input with charToTrim deleted from the front of input (if exists)
	 */
	static String lTrimChar(String input, char charToTrim)
	{
		if (input.length() == 0) return input;
		if (input.charAt(0) == charToTrim)
		{
			return sb(input).deleteCharAt(0).toString();
		}
		return input;
	}

	/**
	 * @param input
	 * @param charToTrim
	 * @return input with charToTrim deleted from the end of input (if exists)
	 */
	static String rTrimChar(String input, char charToTrim)
	{
		int lastCharPos = input.length() - 1;
		if (lastCharPos < 0) return input;
		if (input.charAt(lastCharPos) == charToTrim)
		{
			return sb(input).deleteCharAt(lastCharPos).toString();
		}
		return input;
	}

	/**
	 * @param input
	 * @param charToTrim
	 * @return input with charToTrim deleted from both sides of input (if exists)
	 */
	static String allTrimChar(String input, char charToTrim)
	{
		return lTrimChar(rTrimChar(input, charToTrim), charToTrim);
	}

	/**
	 * removee char c in input
	 * @param input
	 * @param c
	 * @return input without c-chars
	 */
	static String removeAllCharsIn(String input, char c) { return input.replace(Character.toString(c), ""); }

	static String replaceLast(String string, String toReplace, String replacement)
	{
		int pos = string.lastIndexOf(toReplace);

		if (pos > -1)
		{
			return string.substring(0, pos) + replacement + string.substring(pos + toReplace.length(), string.length());
		}
		else
		{
			return string;
		}
	}
	
	static String normaliseLineSeparator(String string)
	{
		return string.replaceAll("\\n|\\r\\n", Constants.LS);
	}
}