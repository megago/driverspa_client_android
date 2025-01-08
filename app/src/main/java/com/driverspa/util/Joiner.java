package com.driverspa.util;

import java.util.Collection;
import java.util.Iterator;

public class Joiner {

	/**
	 * Join a collection of strings by a seperator
	 * @param strings collection of string objects
	 * @param sep string to place between strings
	 * @return joined string
	 */
	public static String join(Collection<?> strings, String sep) {
		return join(strings.iterator(), sep);
	}

	/**
	 * Join a collection of strings by a seperator
	 * @param strings iterator of string objects
	 * @param sep string to place between strings
	 * @return joined string
	 */
	public static String join(Iterator<?> strings, String sep) {
		if (!strings.hasNext())
			return "";

		String start = strings.next().toString();
		if (!strings.hasNext()) // only one, avoid builder
			return start;

		StringBuilder sb = new StringBuilder(64).append(start);
		while (strings.hasNext()) {
			sb.append(sep);
			sb.append(strings.next());
		}
		return sb.toString();
	}

}
