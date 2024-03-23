package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public interface IchorUtils {
	Double ZERO = 0.0;
	Double NZERO = -0.0;
	Double ONE = 1.0;
	Double D32 = 32.0;
	Double NaN = Double.NaN;
	Double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
	Double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
	Double MAX_DOUBLE_VALUE = Double.MAX_VALUE;
	Double MIN_DOUBLE_VALUE = Double.MIN_VALUE;
	Double MAX_SAFE_INTEGER = 9007199254740991.0;
	Double MIN_SAFE_INTEGER = -9007199254740991.0;
	Double EPSILON = Math.pow(2, -52);
	Double PI = Math.PI;
	Double E = Math.E;
	Double LN10 = 2.302585092994046;
	Double LN2 = 0.6931471805599453;
	Double LOG2E = 1.4426950408889634;
	Double LOG10E = 0.4342944819032518;
	Double SQRT1_2 = Math.sqrt(0.5);
	Double SQRT2 = Math.sqrt(2);

	Integer[] DIGITS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

	Callable ABS = Functions.of1((scope, arg) -> Math.abs(scope.asDouble(arg)));
	Callable ACOS = Functions.of1((scope, arg) -> Math.acos(scope.asDouble(arg)));
	Callable ASIN = Functions.of1((scope, arg) -> Math.asin(scope.asDouble(arg)));
	Callable ATAN = Functions.of1((scope, arg) -> Math.atan(scope.asDouble(arg)));
	Callable ATAN2 = Functions.of2((scope, arg1, arg2) -> Math.atan2(scope.asDouble(arg1), scope.asDouble(arg2)));
	Callable CEIL = Functions.of1((scope, arg) -> Math.ceil(scope.asDouble(arg)));
	Callable COS = Functions.of1((scope, arg) -> Math.cos(scope.asDouble(arg)));
	Callable EXP = Functions.of1((scope, arg) -> Math.exp(scope.asDouble(arg)));
	Callable FLOOR = Functions.of1((scope, arg) -> Math.floor(scope.asDouble(arg)));
	Callable LOG = Functions.of1((scope, arg) -> Math.log(scope.asDouble(arg)));
	Callable MAX = Functions.of2((scope, arg1, arg2) -> Math.max(scope.asDouble(arg1), scope.asDouble(arg2)));
	Callable MIN = Functions.of2((scope, arg1, arg2) -> Math.min(scope.asDouble(arg1), scope.asDouble(arg2)));
	Callable POW = Functions.of2((scope, arg1, arg2) -> Math.pow(scope.asDouble(arg1), scope.asDouble(arg2)));
	Callable RANDOM = Functions.ofN((scope, args) -> Math.random());
	Callable ROUND = Functions.of1((scope, arg) -> Math.round(scope.asDouble(arg)));
	Callable SIN = Functions.of1((scope, arg) -> Math.sin(scope.asDouble(arg)));
	Callable SQRT = Functions.of1((scope, arg) -> Math.sqrt(scope.asDouble(arg)));
	Callable TAN = Functions.of1((scope, arg) -> Math.tan(scope.asDouble(arg)));
	Callable CBRT = Functions.of1((scope, arg) -> Math.cbrt(scope.asDouble(arg)));
	Callable COSH = Functions.of1((scope, arg) -> Math.cosh(scope.asDouble(arg)));
	Callable EXPM1 = Functions.of1((scope, arg) -> Math.expm1(scope.asDouble(arg)));
	Callable HYPOT = Functions.of2((scope, arg1, arg2) -> Math.hypot(scope.asDouble(arg1), scope.asDouble(arg2)));
	Callable LOG1P = Functions.of1((scope, arg) -> Math.log1p(scope.asDouble(arg)));
	Callable LOG10 = Functions.of1((scope, arg) -> Math.log10(scope.asDouble(arg)));
	Callable SINH = Functions.of1((scope, arg) -> Math.sinh(scope.asDouble(arg)));
	Callable TANH = Functions.of1((scope, arg) -> Math.tanh(scope.asDouble(arg)));
	Callable IMUL = Functions.of2((scope, arg1, arg2) -> Math.multiplyExact(scope.asInt(arg1), scope.asInt(arg2)));

	Callable TRUNC = Functions.of1((scope, arg) -> {
		var x = scope.asDouble(arg);
		return x < 0.0 ? Math.ceil(x) : Math.floor(x);
	});

	Callable ACOSH = Functions.of1((scope, arg) -> {
		var x = scope.asDouble(arg);

		if (!Double.isNaN(x)) {
			return Math.log(x + Math.sqrt(x * x - 1.0));
		}

		return NaN;
	});

	Callable ASINH = Functions.of1((scope, arg) -> {
		var x = scope.asDouble(arg);

		if (Double.isInfinite(x)) {
			return x;
		}

		if (!Double.isNaN(x)) {
			return x == 0.0 ? 1.0 / x > 0.0 ? ZERO : NZERO : Math.log(x + Math.sqrt(x * x + 1.0));
		}

		return NaN;
	});

	Callable ATANH = Functions.of1((scope, arg) -> {
		var x = scope.asDouble(arg);

		if (!Double.isNaN(x) && -1.0 <= x && x <= 1.0) {
			return x == 0.0 ? 1.0 / x > 0.0 ? ZERO : NZERO : 0.5 * Math.log((x + 1.0) / (x - 1.0));
		}

		return NaN;
	});

	Callable SIGN = Functions.of1((scope, arg) -> Math.signum(scope.asDouble(arg)));
	Callable LOG2 = Functions.of1((scope, arg) -> Math.log(scope.asDouble(arg)) * LOG2E);
	Callable FROUND = Functions.of1((scope, arg) -> (float) scope.asDouble(arg));

	Callable CLZ32 = Functions.of1((scope, arg) -> {
		var x = scope.asDouble(arg);

		if (x == 0 || Double.isNaN(x) || Double.isInfinite(x)) {
			return D32;
		}

		long n = (long) x;

		if (n == 0L) {
			return D32;
		}

		return 31 - Math.floor(Math.log(n) * LOG2E);
	});

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
		if (numStr.isEmpty()) {
			throw new NumberFormatException("Empty string");
		}

		var c0 = numStr.charAt(0);
		var c1 = numStr.length() >= 2 ? numStr.charAt(1) : 0;

		if (c0 == '3' && c1 == '.' && numStr.startsWith("3.14159")) {
			return PI;
		} else if (c0 == '2' && c1 == '.' && numStr.startsWith("2.71828")) {
			return E;
		}

		if (c1 == 0) {
			if (c0 >= '0' && c0 <= '9') {
				return DIGITS[c0 - '0'];
			} else {
				throw new NumberFormatException("Invalid number: " + numStr);
			}
		} else if (c0 == '0' && (c1 == 'b' || c1 == 'B') && numStr.length() >= 3) {
			return Long.parseLong(numStr.substring(2), 2);
		} else if (c0 == '0' && c1 == 'o' && numStr.length() >= 3) {
			return Long.parseLong(numStr.substring(2), 8);
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
