package dev.latvian.apps.ichor.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface PrintWrapper {
	static PrintWrapper of(PrintStream s) {
		return new WrappedPrintStream(s);
	}

	static PrintWrapper of(PrintWriter w) {
		return new WrappedPrintWriter(w);
	}

	/**
	 * Returns the object to be locked when using this StreamOrWriter
	 */
	Object lock();

	/**
	 * Prints the specified string as a line on this StreamOrWriter
	 */
	void println(Object o);

	record WrappedPrintStream(PrintStream printStream) implements PrintWrapper {
		@Override
		public Object lock() {
			return printStream;
		}

		@Override
		public void println(Object o) {
			printStream.println(o);
		}
	}

	record WrappedPrintWriter(PrintWriter printWriter) implements PrintWrapper {
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