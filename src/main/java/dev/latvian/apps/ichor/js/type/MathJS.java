package dev.latvian.apps.ichor.js.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.js.ContextJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

public class MathJS extends Prototype<MathJS> {
	public static final Double PI = Math.PI;
	public static final Double E = Math.E;
	public static final Double LN10 = 2.302585092994046D;
	public static final Double LN2 = 0.6931471805599453D;
	public static final Double LOG2E = 1.4426950408889634D;
	public static final Double LOG10E = 0.4342944819032518D;
	public static final Double SQRT1_2 = Math.sqrt(0.5D);
	public static final Double SQRT2 = Math.sqrt(2D);

	public static final Callable ABS = Functions.of1((cx, scope, arg) -> Math.abs(cx.asDouble(scope, arg)));
	public static final Callable ACOS = Functions.of1((cx, scope, arg) -> Math.acos(cx.asDouble(scope, arg)));
	public static final Callable ASIN = Functions.of1((cx, scope, arg) -> Math.asin(cx.asDouble(scope, arg)));
	public static final Callable ATAN = Functions.of1((cx, scope, arg) -> Math.atan(cx.asDouble(scope, arg)));
	public static final Callable ATAN2 = Functions.of2((cx, scope, arg1, arg2) -> Math.atan2(cx.asDouble(scope, arg1), cx.asDouble(scope, arg2)));
	public static final Callable CEIL = Functions.of1((cx, scope, arg) -> Math.ceil(cx.asDouble(scope, arg)));
	public static final Callable COS = Functions.of1((cx, scope, arg) -> Math.cos(cx.asDouble(scope, arg)));
	public static final Callable EXP = Functions.of1((cx, scope, arg) -> Math.exp(cx.asDouble(scope, arg)));
	public static final Callable FLOOR = Functions.of1((cx, scope, arg) -> Math.floor(cx.asDouble(scope, arg)));
	public static final Callable LOG = Functions.of1((cx, scope, arg) -> Math.log(cx.asDouble(scope, arg)));
	public static final Callable MAX = Functions.of2((cx, scope, arg1, arg2) -> Math.max(cx.asDouble(scope, arg1), cx.asDouble(scope, arg2)));
	public static final Callable MIN = Functions.of2((cx, scope, arg1, arg2) -> Math.min(cx.asDouble(scope, arg1), cx.asDouble(scope, arg2)));
	public static final Callable POW = Functions.of2((cx, scope, arg1, arg2) -> Math.pow(cx.asDouble(scope, arg1), cx.asDouble(scope, arg2)));
	public static final Callable RANDOM = Functions.ofN((cx, scope, args) -> Math.random());
	public static final Callable ROUND = Functions.of1((cx, scope, arg) -> Math.round(cx.asDouble(scope, arg)));
	public static final Callable SIN = Functions.of1((cx, scope, arg) -> Math.sin(cx.asDouble(scope, arg)));
	public static final Callable SQRT = Functions.of1((cx, scope, arg) -> Math.sqrt(cx.asDouble(scope, arg)));
	public static final Callable TAN = Functions.of1((cx, scope, arg) -> Math.tan(cx.asDouble(scope, arg)));
	public static final Callable CBRT = Functions.WIP;
	public static final Callable COSH = Functions.WIP;
	public static final Callable EXPM1 = Functions.WIP;
	public static final Callable HYPOT = Functions.WIP;
	public static final Callable LOG1P = Functions.WIP;
	public static final Callable LOG10 = Functions.WIP;
	public static final Callable SINH = Functions.WIP;
	public static final Callable TANH = Functions.WIP;
	public static final Callable IMUL = Functions.WIP;
	public static final Callable TRUNC = Functions.WIP;
	public static final Callable ACOSH = Functions.WIP;
	public static final Callable ASINH = Functions.WIP;
	public static final Callable ATANH = Functions.WIP;
	public static final Callable SIGN = Functions.of1((cx, scope, arg) -> Math.signum(cx.asDouble(scope, arg)));
	public static final Callable LOG2 = Functions.WIP;
	public static final Callable FROUND = Functions.WIP;
	public static final Callable CLZ32 = Functions.WIP;

	public MathJS(ContextJS cx) {
		super(cx, "Math", MathJS.class);
	}

	@Override
	@Nullable
	public Object getStatic(Context cx, Scope scope, String name) {
		return switch (name) {
			case "PI" -> PI;
			case "E" -> E;
			case "LN10" -> LN10;
			case "LN2" -> LN2;
			case "LOG2E" -> LOG2E;
			case "LOG10E" -> LOG10E;
			case "SQRT1_2" -> SQRT1_2;
			case "SQRT2" -> SQRT2;
			case "abs" -> ABS;
			case "acos" -> ACOS;
			case "asin" -> ASIN;
			case "atan" -> ATAN;
			case "atan2" -> ATAN2;
			case "ceil" -> CEIL;
			case "cos" -> COS;
			case "exp" -> EXP;
			case "floor" -> FLOOR;
			case "log" -> LOG;
			case "max" -> MAX;
			case "min" -> MIN;
			case "pow" -> POW;
			case "random" -> RANDOM;
			case "round" -> ROUND;
			case "sin" -> SIN;
			case "sqrt" -> SQRT;
			case "tan" -> TAN;
			case "cbrt" -> CBRT;
			case "cosh" -> COSH;
			case "expm1" -> EXPM1;
			case "hypot" -> HYPOT;
			case "log1p" -> LOG1P;
			case "log10" -> LOG10;
			case "sinh" -> SINH;
			case "tanh" -> TANH;
			case "imul" -> IMUL;
			case "trunc" -> TRUNC;
			case "acosh" -> ACOSH;
			case "asinh" -> ASINH;
			case "atanh" -> ATANH;
			case "sign" -> SIGN;
			case "log2" -> LOG2;
			case "fround" -> FROUND;
			case "clz32" -> CLZ32;
			default -> super.getStatic(cx, scope, name);
		};
	}
}
