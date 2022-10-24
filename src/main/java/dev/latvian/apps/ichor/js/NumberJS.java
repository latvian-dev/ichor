package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class NumberJS {
	public static final Number ZERO = 0D;
	public static final Number ONE = 1D;
	public static final Number NaN = Double.NaN;
	public static final double MAX_SAFE_INTEGER = 9007199254740991.0;
	public static final double MIN_SAFE_INTEGER = -MAX_SAFE_INTEGER;

	public static final Prototype PROTOTYPE = PrototypeBuilder.create("Number")
			.constructor((cx, args, hasNew) -> args.length == 0 ? NaN : cx.asNumber(args[0]))
			.asString((cx, self) -> self.toString())
			.asNumber((cx, self) -> (Number) self)
			.asBoolean((cx, self) -> ((Number) self).doubleValue() != 0D)
			.constant("NaN", NaN)
			.constant("POSITIVE_INFINITY", Double.POSITIVE_INFINITY)
			.constant("NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY)
			.constant("MAX_VALUE", Double.MAX_VALUE)
			.constant("MIN_VALUE", Double.MIN_VALUE)
			.constant("MAX_SAFE_INTEGER", MAX_SAFE_INTEGER)
			.constant("MIN_SAFE_INTEGER", MIN_SAFE_INTEGER)
			.constant("EPSILON", Math.pow(2D, -52D))
			.function("isFinite", NumberJS::isFinite)
			.function("isNaN", NumberJS::isNaN)
			.function("isInteger", NumberJS::isInteger)
			.function("isSafeInteger", NumberJS::isSafeInteger)
			.function("parseFloat", NumberJS::parseFloat)
			.function("parseInt", NumberJS::parseInt)
			.function("toFixed", NumberJS::toFixed)
			.function("toExponential", NumberJS::toExponential)
			.function("toPrecision", NumberJS::toPrecision);

	private static Object isFinite(Context cx, Object self, Object[] args) {
		if (args.length == 0) {
			return Boolean.FALSE;
		} else if (args[0] instanceof Double n) {
			return !n.isInfinite() && !n.isNaN();
		} else if (args[0] instanceof Float n) {
			return !n.isInfinite() && !n.isNaN();
		}

		double d = cx.asDouble(args[0]);
		return !Double.isInfinite(d) && !Double.isNaN(d);
	}

	private static Object isNaN(Context cx, Object self, Object[] args) {
		if (args.length == 0) {
			return Boolean.TRUE;
		} else if (args[0] instanceof Double n) {
			return n.isNaN();
		} else if (args[0] instanceof Float n) {
			return n.isNaN();
		} else {
			double d = cx.asDouble(args[0]);
			return Double.isNaN(d);
		}
	}

	private static Object isInteger(Context cx, Object self, Object[] args) {
		if (args.length == 0) {
			return false;
		}

		double d = cx.asDouble(args[0]);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (Math.floor(d) == d);
	}

	private static Object isSafeInteger(Context cx, Object self, Object[] args) {
		if (args.length == 0) {
			return false;
		}

		double d = cx.asDouble(args[0]);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (d <= MAX_SAFE_INTEGER) && (d >= MIN_SAFE_INTEGER) && (Math.floor(d) == d);
	}

	private static Object parseFloat(Context cx, Object self, Object[] args) {
		return cx.asDouble(args[0]);
	}

	private static Object parseInt(Context cx, Object self, Object[] args) {
		return cx.asInt(args[0]);
	}

	private static Object toFixed(Context cx, Object self, Object[] args) {
		return self.toString(); // FIXME
	}

	private static Object toExponential(Context cx, Object self, Object[] args) {
		return self.toString(); // FIXME
	}

	private static Object toPrecision(Context cx, Object self, Object[] args) {
		return self.toString(); // FIXME
	}
}