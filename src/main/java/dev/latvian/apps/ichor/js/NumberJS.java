package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class NumberJS {
	public static final Double ZERO = 0D;
	public static final Double ONE = 1D;
	public static final Double NaN = Double.NaN;
	public static final double MAX_SAFE_INTEGER = 9007199254740991.0;
	public static final double MIN_SAFE_INTEGER = -MAX_SAFE_INTEGER;

	public static final Prototype PROTOTYPE = new PrototypeBuilder("Number") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? NaN : cx.asDouble(scope, args[0]);
		}

		@Override
		public void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape) {
			AstStringBuilder.wrapNumber(self, builder);
		}

		@Override
		public Number asNumber(Context cx, Scope scope, Object self) {
			return (Number) self;
		}

		@Override
		public boolean asBoolean(Context cx, Scope scope, Object self) {
			return ((Number) self).doubleValue() != 0D;
		}
	}
			.constant("NaN", NaN)
			.constant("POSITIVE_INFINITY", Double.POSITIVE_INFINITY)
			.constant("NEGATIVE_INFINITY", Double.NEGATIVE_INFINITY)
			.constant("MAX_VALUE", Double.MAX_VALUE)
			.constant("MIN_VALUE", Double.MIN_VALUE)
			.constant("MAX_SAFE_INTEGER", MAX_SAFE_INTEGER)
			.constant("MIN_SAFE_INTEGER", MIN_SAFE_INTEGER)
			.constant("EPSILON", Math.pow(2D, -52D))
			.staticFunction("isFinite", NumberJS::isFinite)
			.staticFunction("isNaN", NumberJS::isNaN)
			.staticFunction("isInteger", NumberJS::isInteger)
			.staticFunction("isSafeInteger", NumberJS::isSafeInteger)
			.staticFunction("parseFloat", NumberJS::parseFloat)
			.staticFunction("parseInt", NumberJS::parseInt)
			.function("toFixed", NumberJS::toFixed)
			.function("toExponential", NumberJS::toExponential)
			.function("toPrecision", NumberJS::toPrecision);

	private static Object isFinite(Context cx, Scope scope, Object[] args) {
		if (args.length == 0) {
			return Boolean.FALSE;
		}

		double d = cx.asDouble(scope, args[0]);
		return !Double.isInfinite(d) && !Double.isNaN(d);
	}

	private static Object isNaN(Context cx, Scope scope, Object[] args) {
		return args.length == 0 ? Boolean.TRUE : Double.isNaN(cx.asDouble(scope, args[0]));
	}

	private static Object isInteger(Context cx, Scope scope, Object[] args) {
		if (args.length == 0) {
			return false;
		}

		double d = cx.asDouble(scope, args[0]);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (Math.floor(d) == d);
	}

	private static Object isSafeInteger(Context cx, Scope scope, Object[] args) {
		if (args.length == 0) {
			return false;
		}

		double d = cx.asDouble(scope, args[0]);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (d <= MAX_SAFE_INTEGER) && (d >= MIN_SAFE_INTEGER) && (Math.floor(d) == d);
	}

	private static Object parseFloat(Context cx, Scope scope, Object[] args) {
		return cx.asDouble(scope, args[0]);
	}

	private static Object parseInt(Context cx, Scope scope, Object[] args) {
		return cx.asInt(scope, args[0]);
	}

	private static Object toFixed(Context cx, Scope scope, Object self, Object[] args) {
		return self.toString(); // FIXME
	}

	private static Object toExponential(Context cx, Scope scope, Object self, Object[] args) {
		return self.toString(); // FIXME
	}

	private static Object toPrecision(Context cx, Scope scope, Object self, Object[] args) {
		return self.toString(); // FIXME
	}
}
