package dev.latvian.apps.ichor.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface PrintStreamOrWriter {
	/**
	 * Returns the object to be locked when using this StreamOrWriter
	 */
	Object lock();

	/**
	 * Prints the specified string as a line on this StreamOrWriter
	 */
	void println(Object o);

	record WrappedPrintStream(PrintStream printStream) implements PrintStreamOrWriter {
		@Override
		public Object lock() {
			return printStream;
		}

		@Override
		public void println(Object o) {
			printStream.println(o);
		}
	}

	record WrappedPrintWriter(PrintWriter printWriter) implements PrintStreamOrWriter {
		@Override
		public Object lock() {
			return printWriter;
		}

		@Override
		public void println(Object o) {
			printWriter.println(o);
		}
	}
}