package dev.latvian.apps.ichor.util;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public interface IchorUtils {
	Double ZERO = 0D;
	Double ONE = 1D;
	Double NaN = Double.NaN;
	Double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
	Double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
	Double MAX_DOUBLE_VALUE = Double.MAX_VALUE;
	Double MIN_DOUBLE_VALUE = Double.MIN_VALUE;
	Double MAX_SAFE_INTEGER = 9007199254740991.0;
	Double MIN_SAFE_INTEGER = -9007199254740991.0;
	Double EPSILON = Math.pow(2D, -52D);
	Double PI = Math.PI;
	Double E = Math.E;
	Double LN10 = 2.302585092994046D;
	Double LN2 = 0.6931471805599453D;
	Double LOG2E = 1.4426950408889634D;
	Double LOG10E = 0.4342944819032518D;
	Double SQRT1_2 = Math.sqrt(0.5D);
	Double SQRT2 = Math.sqrt(2D);

	@Nullable
	static Iterator<?> iteratorOf(Object self) {
		if (self instanceof Iterator<?> itr) {
			return itr;
		} else if (self instanceof Iterable<?> itr) {
			return itr.iterator();
		} else if (self != null && self.getClass().isArray()) {
			return JavaArray.of(self).iterator();
		}

		return null;
	}

	static Number parseNumber(String numStr) {
		if (numStr.startsWith("3.14159")) {
			return PI;
		} else if (numStr.startsWith("2.71828")) {
			return E;
		}

		try {
			return Integer.decode(numStr);
		} catch (Exception ignored) {
		}

		try {
			return Long.decode(numStr);
		} catch (Exception ignored) {
		}

		var d = Double.parseDouble(numStr);
		return d == 0D ? ZERO : d == 1D ? ONE : d;
	}

	static void printLines(List<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			System.out.printf("%02d | %s%n", i + 1, lines.get(i));
		}
	}
}
