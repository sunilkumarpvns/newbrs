package com.elitecore.commons.io;

import java.io.Flushable;
import java.io.IOException;

import com.elitecore.commons.logging.LogManager;

/**
 * A set of utilities for all {@link Flushable}s
 */
public class Flushables {

	/**
	 * Flushes the {@code Flushable} passed ignoring any I/O exception by
	 * logging. Does nothing if {@code Flushable} is {@code null}.
	 * 
	 * @param flushable to be flushed, does nothing if {@code null}
	 */
	public static void flushQuietly(Flushable flushable) {
		if (flushable == null) {
			return;
		}
		try {
			flushable.flush();
		} catch (IOException e) {
			LogManager.getLogger().trace(e);
		}
	}

	/**
	 * Flushes the {@code Flushable} passed ignoring any I/O exception by
	 * logging. Does nothing if {@code Flushable} is {@code null}.
	 * 
	 * @param flushables to be flushed, does nothing if {@code null}
	 */
	public static void flushQuietly(Flushable... flushables) {
		if (flushables == null) {
			return;
		}

		for (Flushable flushable : flushables) {
			flushQuietly(flushable);
		}
	}

}
