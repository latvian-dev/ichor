package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.util.SimpleFunction;
import org.jetbrains.annotations.Nullable;

public class NumberJS implements WrappedObject {
	public static final Double ZERO = 0D;
	public static final Double ONE = 1D;
	public static final Double NaN = Double.NaN;
	public static final double MAX_SAFE_INTEGER = 9007199254740991.0;
	public static final double MIN_SAFE_INTEGER = -MAX_SAFE_INTEGER;

	public static final Prototype PROTOTYPE = new PrototypeBuilder("Number") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? NaN : cx.asNumber(scope, args[0]);
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
			.staticFunction("parseInt", NumberJS::parseInt);


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

	private static final SimpleFunction.Callback<Number> TO_FIXED = (cx, scope, num, args) -> num.toString(); // FIXME
	private static final SimpleFunction.Callback<Number> TO_EXPONENTIAL = (cx, scope, num, args) -> num.toString(); // FIXME
	private static final SimpleFunction.Callback<Number> TO_PRECISION = (cx, scope, num, args) -> num.toString(); // FIXME
	private static final SimpleFunction.Callback<Number> TO_BYTE = (cx, scope, num, args) -> num.byteValue();
	private static final SimpleFunction.Callback<Number> TO_SHORT = (cx, scope, num, args) -> num.shortValue();
	private static final SimpleFunction.Callback<Number> TO_INT = (cx, scope, num, args) -> num.intValue();
	private static final SimpleFunction.Callback<Number> TO_LONG = (cx, scope, num, args) -> num.longValue();
	private static final SimpleFunction.Callback<Number> TO_FLOAT = (cx, scope, num, args) -> num.floatValue();
	private static final SimpleFunction.Callback<Number> TO_DOUBLE = (cx, scope, num, args) -> num.doubleValue();
	private static final SimpleFunction.Callback<Number> TO_CHAR = (cx, scope, num, args) -> (char) num.intValue();

	public final Number self;

	public NumberJS(Number self) {
		this.self = self;
	}

	@Override
	public Object unwrap() {
		return self;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return PROTOTYPE;
	}

	@Override
	public String toString() {
		return "[" + self.getClass().getSimpleName() + " " + self + "]";
	}

	@Override
	public void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		AstStringBuilder.wrapNumber(self, builder);
	}

	@Override
	public Number asNumber(Context cx, Scope scope) {
		return self;
	}

	@Override
	public boolean asBoolean(Context cx, Scope scope) {
		return self.doubleValue() != 0D;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		return switch (name) {
			case "toFixed" -> SimpleFunction.of(self, TO_FIXED);
			case "toExponential" -> SimpleFunction.of(self, TO_EXPONENTIAL);
			case "toPrecision" -> SimpleFunction.of(self, TO_PRECISION);
			case "toByte" -> SimpleFunction.of(self, TO_BYTE);
			case "toShort" -> SimpleFunction.of(self, TO_SHORT);
			case "toInt" -> SimpleFunction.of(self, TO_INT);
			case "toLong" -> SimpleFunction.of(self, TO_LONG);
			case "toFloat" -> SimpleFunction.of(self, TO_FLOAT);
			case "toDouble" -> SimpleFunction.of(self, TO_DOUBLE);
			case "toChar" -> SimpleFunction.of(self, TO_CHAR);
			default -> PROTOTYPE.get(cx, scope, self, name);
		};
	}
}
