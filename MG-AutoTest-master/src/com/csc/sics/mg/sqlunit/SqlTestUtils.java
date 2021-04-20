package com.csc.sics.mg.sqlunit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class SqlTestUtils {

	/**
	 * Compares 2 numbers. Works for most combinations of number types, but floats will be problematic and should be
	 * avoided.
	 * 
	 * @param number1
	 *          A Number, may be null
	 * @param number2
	 *          A Number, may be null
	 * @return a negative integer, zero, or a positive integer as number1 is less than, equal to, or greater than number2.
	 */
	public static int compare(final Number number1, final Number number2) {
		return compare(number1, number2, 0.0);
	}

	/**
	 * Compares 2 numbers. Works for most combinations of number types, but floats will be problematic and should be
	 * avoided.
	 * 
	 * @param number1
	 *          A Number, may be null
	 * @param number2
	 *          A Number, may be null
	 * @param tolerance
	 *          the degree of difference that will be allowed
	 * @return a negative integer, zero, or a positive integer as number1 is less than, equal to, or greater than number2.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int compare(final Number number1, final Number number2, final double tolerance) {
		if (number1 == null && number2 == null) {
			return 0;
		}

		if (number1 == null && number2 != null) {
			return -1;
		}

		if (number1 != null && number2 == null) {
			return 1;
		}

		if (number2.getClass().equals(number1.getClass())) {
			// both numbers are instances of the same type!
			if (number1 instanceof Comparable) {
				// and they implement the Comparable interface
				final int comp = ((Comparable) number1).compareTo(number2);
				if (comp == 0) {
					return 0;
				}
			}
		}

		double diff = number1.doubleValue() - number2.doubleValue();
		diff = Math.abs(diff);

		// for all other Number types, let's check their double values
		if (number1.doubleValue() < number2.doubleValue()) {
			if (diff < tolerance) {
				return 0;
			}
			return -1;
		}
		if (number1.doubleValue() > number2.doubleValue()) {
			if (diff <= tolerance) {
				return 0;
			}
			return 1;
		}

		return 0;
	}

	public static String prettyMethodName(final String methodName) {
		final StringBuilder sb = new StringBuilder();
		char prevChar = Character.toUpperCase(methodName.charAt(0));
		sb.append(prevChar);
		final char[] methodA = methodName.toCharArray();
		for (int i = 1; i < methodA.length; i++) {
			char aChar = methodA[i];
			if (Character.isUpperCase(aChar) && Character.isLowerCase(prevChar)) {
				sb.append(' ');
			}
			if (Character.isDigit(aChar) && !Character.isDigit(prevChar)) {
				sb.append(' ');
			}
			if (Character.isDigit(prevChar) && !Character.isDigit(aChar)) {
				sb.append(' ');
				aChar = Character.toUpperCase(aChar);
			}
			sb.append(aChar);
			prevChar = aChar;
		}
		return sb.toString().trim();
	}

	public static BufferedWriter newBufferedWriter(final File aFile) throws IOException {
		return Files.newBufferedWriter(aFile.toPath(), Charset.forName("UTF-8"));
	}

}
