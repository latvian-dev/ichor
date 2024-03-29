package dev.latvian.apps.ichor.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import dev.latvian.apps.ichor.util.IchorUtils;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public class NumberJS extends Prototype<Number> {
	private static final Callable IS_FINITE = Functions.of1((scope, arg) -> {
		double d = scope.asDouble(arg);
		return !Double.isInfinite(d) && !Double.isNaN(d);
	});

	private static final Callable IS_NAN = Functions.of1((scope, arg) -> Double.isNaN(scope.asDouble(arg)));

	private static final Callable IS_INTEGER = Functions.of1((scope, arg) -> {
		double d = scope.asDouble(arg);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (Math.floor(d) == d);
	});

	private static final Callable IS_SAFE_INTEGER = Functions.of1((scope, arg) -> {
		double d = scope.asDouble(arg);
		return !Double.isInfinite(d) && !Double.isNaN(d) && (d <= IchorUtils.MAX_SAFE_INTEGER) && (d >= IchorUtils.MIN_SAFE_INTEGER) && (Math.floor(d) == d);
	});

	private static final Callable PARSE_FLOAT = Functions.of1(Scope::asDouble);
	private static final Callable PARSE_INT = Functions.of1(Scope::asInt);

	private static final Functions.Bound<Number> TO_FIXED = (scope, num, args) -> AstStringBuilder.wrapNumber(num); // FIXME
	private static final Functions.Bound<Number> TO_EXPONENTIAL = (scope, num, args) -> AstStringBuilder.wrapNumber(num); // FIXME
	private static final Functions.Bound<Number> TO_PRECISION = (scope, num, args) -> AstStringBuilder.wrapNumber(num); // FIXME
	private static final Functions.Bound<Number> TO_BYTE = (scope, num, args) -> num.byteValue();
	private static final Functions.Bound<Number> TO_SHORT = (scope, num, args) -> num.shortValue();
	private static final Functions.Bound<Number> TO_INT = (scope, num, args) -> num.intValue();
	private static final Functions.Bound<Number> TO_LONG = (scope, num, args) -> num.longValue();
	private static final Functions.Bound<Number> TO_FLOAT = (scope, num, args) -> num.floatValue();
	private static final Functions.Bound<Number> TO_DOUBLE = (scope, num, args) -> num.doubleValue();
	private static final Functions.Bound<Number> TO_CHAR = (scope, num, args) -> (char) num.intValue();

	public NumberJS(Scope cx) {
		super(cx, "Number", Number.class);
	}

	@Override
	public Object call(Scope scope, Object[] args, boolean hasNew) {
		return args.length == 0 ? IchorUtils.NaN : scope.asNumber(args[0]);
	}

	@Override
	@Nullable
	public Object getStatic(Scope scope, String name) {
		return switch (name) {
			case "NaN" -> IchorUtils.NaN;
			case "POSITIVE_INFINITY" -> IchorUtils.POSITIVE_INFINITY;
			case "NEGATIVE_INFINITY" -> IchorUtils.NEGATIVE_INFINITY;
			case "MAX_VALUE" -> IchorUtils.MAX_DOUBLE_VALUE;
			case "MIN_VALUE" -> IchorUtils.MIN_DOUBLE_VALUE;
			case "MAX_SAFE_INTEGER" -> IchorUtils.MAX_SAFE_INTEGER;
			case "MIN_SAFE_INTEGER" -> IchorUtils.MIN_SAFE_INTEGER;
			case "EPSILON" -> IchorUtils.EPSILON;
			case "isFinite" -> IS_FINITE;
			case "isNaN" -> IS_NAN;
			case "isInteger" -> IS_INTEGER;
			case "isSafeInteger" -> IS_SAFE_INTEGER;
			case "parseFloat" -> PARSE_FLOAT;
			case "parseInt" -> PARSE_INT;
			default -> super.getStatic(scope, name);
		};
	}

	@Override
	@Nullable
	public Object getLocal(Scope scope, Number self, String name) {
		return switch (name) {
			case "millis", "ms" -> Duration.ofMillis(self.longValue());
			case "seconds", "s" -> Duration.ofSeconds(self.longValue());
			case "minutes", "m" -> Duration.ofMinutes(self.longValue());
			case "hours", "h" -> Duration.ofHours(self.longValue());
			case "days", "d" -> Duration.ofDays(self.longValue());
			case "toFixed" -> TO_FIXED.with(self);
			case "toExponential" -> TO_EXPONENTIAL.with(self);
			case "toPrecision" -> TO_PRECISION.with(self);
			case "toByte" -> TO_BYTE.with(self);
			case "toShort" -> TO_SHORT.with(self);
			case "toInt" -> TO_INT.with(self);
			case "toLong" -> TO_LONG.with(self);
			case "toFloat" -> TO_FLOAT.with(self);
			case "toDouble" -> TO_DOUBLE.with(self);
			case "toChar" -> TO_CHAR.with(self);
			default -> super.getLocal(scope, self, name);
		};
	}

	@Override
	public Collection<?> keys(Scope scope, Number self) {
		return List.of();
	}

	@Override
	public Collection<?> values(Scope scope, Number self) {
		return List.of();
	}

	@Override
	public Collection<?> entries(Scope scope, Number self) {
		return List.of();
	}

	@Override
	public boolean asString(Scope scope, Number self, StringBuilder builder, boolean escape) {
		AstStringBuilder.wrapNumber(self, builder);
		return true;
	}

	@Override
	public Number asNumber(Scope scope, Number self) {
		return self;
	}

	@Override
	public Boolean asBoolean(Scope scope, Number self) {
		return self.doubleValue() != 0D;
	}
}
