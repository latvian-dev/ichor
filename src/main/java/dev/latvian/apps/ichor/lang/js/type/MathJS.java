package dev.latvian.apps.ichor.lang.js.type;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.lang.js.ContextJS;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.IchorUtils;
import org.jetbrains.annotations.Nullable;

public class MathJS extends Prototype<MathJS> {
	public MathJS(ContextJS cx) {
		super(cx, "Math", MathJS.class);
	}

	@Override
	@Nullable
	public Object getStatic(Context cx, Scope scope, String name) {
		return switch (name) {
			case "PI" -> IchorUtils.PI;
			case "E" -> IchorUtils.E;
			case "LN10" -> IchorUtils.LN10;
			case "LN2" -> IchorUtils.LN2;
			case "LOG2E" -> IchorUtils.LOG2E;
			case "LOG10E" -> IchorUtils.LOG10E;
			case "SQRT1_2" -> IchorUtils.SQRT1_2;
			case "SQRT2" -> IchorUtils.SQRT2;
			case "abs" -> IchorUtils.ABS;
			case "acos" -> IchorUtils.ACOS;
			case "asin" -> IchorUtils.ASIN;
			case "atan" -> IchorUtils.ATAN;
			case "atan2" -> IchorUtils.ATAN2;
			case "ceil" -> IchorUtils.CEIL;
			case "cos" -> IchorUtils.COS;
			case "exp" -> IchorUtils.EXP;
			case "floor" -> IchorUtils.FLOOR;
			case "log" -> IchorUtils.LOG;
			case "max" -> IchorUtils.MAX;
			case "min" -> IchorUtils.MIN;
			case "pow" -> IchorUtils.POW;
			case "random" -> IchorUtils.RANDOM;
			case "round" -> IchorUtils.ROUND;
			case "sin" -> IchorUtils.SIN;
			case "sqrt" -> IchorUtils.SQRT;
			case "tan" -> IchorUtils.TAN;
			case "cbrt" -> IchorUtils.CBRT;
			case "cosh" -> IchorUtils.COSH;
			case "expm1" -> IchorUtils.EXPM1;
			case "hypot" -> IchorUtils.HYPOT;
			case "log1p" -> IchorUtils.LOG1P;
			case "log10" -> IchorUtils.LOG10;
			case "sinh" -> IchorUtils.SINH;
			case "tanh" -> IchorUtils.TANH;
			case "imul" -> IchorUtils.IMUL;
			case "trunc" -> IchorUtils.TRUNC;
			case "acosh" -> IchorUtils.ACOSH;
			case "asinh" -> IchorUtils.ASINH;
			case "atanh" -> IchorUtils.ATANH;
			case "sign" -> IchorUtils.SIGN;
			case "log2" -> IchorUtils.LOG2;
			case "fround" -> IchorUtils.FROUND;
			case "clz32" -> IchorUtils.CLZ32;
			default -> super.getStatic(cx, scope, name);
		};
	}
}
