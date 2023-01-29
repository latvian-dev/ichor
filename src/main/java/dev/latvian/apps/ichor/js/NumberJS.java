package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeWrappedObject;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public record NumberJS(Number self) implements PrototypeWrappedObject {
	public static final Double ZERO = 0D;
	public static final Double ONE = 1D;
	public static final Double NaN = Double.NaN;
	public static final Double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
	public static final Double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
	public static final Double MAX_VALUE = Double.MAX_VALUE;
	public static final Double MIN_VALUE = Double.MIN_VALUE;
	public static final Double MAX_SAFE_INTEGER = 9007199254740991.0;
	public static final Double MIN_SAFE_INTEGER = -9007199254740991.0;
	public static final Double EPSILON = Math.pow(2D, -52D);

	private static final Callable IS_FINITE = Functions.of1((cx, scope, arg) -> {
		double d = cx.asDouble(scope, arg);
		return !Double.isInfinite(d) && !Double.isNaN(d);
	});

	private static final Callable IS_NAN = Functions.of1((cx, scope, arg) -> Double.isNaN(cx.asDouble(scope, arg)));

	private static final Callable IS_INTEGER = Functions.of1((cx, scope, arg) -> {
		double d = cx.asDouble(scope, arg);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (Math.floor(d) == d);
	});

	private static final Callable IS_SAFE_INTEGER = Functions.of1((cx, scope, arg) -> {
		double d = cx.asDouble(scope, arg);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (d <= MAX_SAFE_INTEGER) && (d >= MIN_SAFE_INTEGER) && (Math.floor(d) == d);
	});

	private static final Callable PARSE_FLOAT = Functions.of1(Context::asDouble);
	private static final Callable PARSE_INT = Functions.of1(Context::asInt);

	private static final Functions.Bound<Number> TO_FIXED = (cx, scope, num, args) -> AstStringBuilder.wrapNumber(num); // FIXME
	private static final Functions.Bound<Number> TO_EXPONENTIAL = (cx, scope, num, args) -> AstStringBuilder.wrapNumber(num); // FIXME
	private static final Functions.Bound<Number> TO_PRECISION = (cx, scope, num, args) -> AstStringBuilder.wrapNumber(num); // FIXME
	private static final Functions.Bound<Number> TO_BYTE = (cx, scope, num, args) -> num.byteValue();
	private static final Functions.Bound<Number> TO_SHORT = (cx, scope, num, args) -> num.shortValue();
	private static final Functions.Bound<Number> TO_INT = (cx, scope, num, args) -> num.intValue();
	private static final Functions.Bound<Number> TO_LONG = (cx, scope, num, args) -> num.longValue();
	private static final Functions.Bound<Number> TO_FLOAT = (cx, scope, num, args) -> num.floatValue();
	private static final Functions.Bound<Number> TO_DOUBLE = (cx, scope, num, args) -> num.doubleValue();
	private static final Functions.Bound<Number> TO_CHAR = (cx, scope, num, args) -> (char) num.intValue();

	public static Prototype createDefaultPrototype() {
		return new Prototype("Number") {
			@Override
			public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
				return args.length == 0 ? NaN : cx.asNumber(scope, args[0]);
			}

			@Override
			@Nullable
			public Object get(Context cx, Scope scope, String name) {
				return switch (name) {
					case "NaN" -> NaN;
					case "POSITIVE_INFINITY" -> POSITIVE_INFINITY;
					case "NEGATIVE_INFINITY" -> NEGATIVE_INFINITY;
					case "MAX_VALUE" -> MAX_VALUE;
					case "MIN_VALUE" -> MIN_VALUE;
					case "MAX_SAFE_INTEGER" -> MAX_SAFE_INTEGER;
					case "MIN_SAFE_INTEGER" -> MIN_SAFE_INTEGER;
					case "EPSILON" -> EPSILON;
					case "isFinite" -> IS_FINITE;
					case "isNaN" -> IS_NAN;
					case "isInteger" -> IS_INTEGER;
					case "isSafeInteger" -> IS_SAFE_INTEGER;
					case "parseFloat" -> PARSE_FLOAT;
					case "parseInt" -> PARSE_INT;
					default -> super.get(cx, scope, name);
				};
			}
		};
	}

	@Override
	public Object unwrap() {
		return self;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return ((ContextJS) cx).numberPrototype;
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
			case "millis", "ms" -> Duration.ofMillis(self.longValue());
			case "seconds", "s" -> Duration.ofSeconds(self.longValue());
			case "minutes", "m" -> Duration.ofMinutes(self.longValue());
			case "hours", "h" -> Duration.ofHours(self.longValue());
			case "days", "d" -> Duration.ofDays(self.longValue());
			case "toFixed" -> Functions.bound(self, TO_FIXED);
			case "toExponential" -> Functions.bound(self, TO_EXPONENTIAL);
			case "toPrecision" -> Functions.bound(self, TO_PRECISION);
			case "toByte" -> Functions.bound(self, TO_BYTE);
			case "toShort" -> Functions.bound(self, TO_SHORT);
			case "toInt" -> Functions.bound(self, TO_INT);
			case "toLong" -> Functions.bound(self, TO_LONG);
			case "toFloat" -> Functions.bound(self, TO_FLOAT);
			case "toDouble" -> Functions.bound(self, TO_DOUBLE);
			case "toChar" -> Functions.bound(self, TO_CHAR);
			default -> PrototypeWrappedObject.super.get(cx, scope, name);
		};
	}
}
