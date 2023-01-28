package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

public class MathJS {
	public static final Double PI = Math.PI;
	public static final Double E = Math.E;
	public static final Double LN10 = 2.302585092994046D;
	public static final Double LN2 = 0.6931471805599453D;
	public static final Double LOG2E = 1.4426950408889634D;
	public static final Double LOG10E = 0.4342944819032518D;
	public static final Double SQRT1_2 = Math.sqrt(0.5D);
	public static final Double SQRT2 = Math.sqrt(2D);

	public static Prototype createDefaultPrototype() {
		return new Prototype("Math") {
			@SuppressWarnings("DuplicateBranchesInSwitch")
			@Override
			@Nullable
			public Object get(Context cx, Scope scope, String name) {
				return switch (name) {
					case "PI" -> PI;
					case "E" -> E;
					case "LN10" -> LN10;
					case "LN2" -> LN2;
					case "LOG2E" -> LOG2E;
					case "LOG10E" -> LOG10E;
					case "SQRT1_2" -> SQRT1_2;
					case "SQRT2" -> SQRT2;
					case "abs" -> Functions.WIP;
					case "acos" -> Functions.WIP;
					case "asin" -> Functions.WIP;
					case "atan" -> Functions.WIP;
					case "atan2" -> Functions.WIP;
					case "ceil" -> Functions.WIP;
					case "cos" -> Functions.WIP;
					case "exp" -> Functions.WIP;
					case "floor" -> Functions.WIP;
					case "log" -> Functions.WIP;
					case "max" -> Functions.WIP;
					case "min" -> Functions.WIP;
					case "pow" -> Functions.WIP;
					case "random" -> Functions.WIP;
					case "round" -> Functions.WIP;
					case "sin" -> Functions.WIP;
					case "sqrt" -> Functions.WIP;
					case "tan" -> Functions.WIP;
					case "cbrt" -> Functions.WIP;
					case "cosh" -> Functions.WIP;
					case "expm1" -> Functions.WIP;
					case "hypot" -> Functions.WIP;
					case "log1p" -> Functions.WIP;
					case "log10" -> Functions.WIP;
					case "sinh" -> Functions.WIP;
					case "tanh" -> Functions.WIP;
					case "imul" -> Functions.WIP;
					case "trunc" -> Functions.WIP;
					case "acosh" -> Functions.WIP;
					case "asinh" -> Functions.WIP;
					case "atanh" -> Functions.WIP;
					case "sign" -> Functions.WIP;
					case "log2" -> Functions.WIP;
					case "fround" -> Functions.WIP;
					case "clz32" -> Functions.WIP;
					default -> super.get(cx, scope, name);
				};
			}
		};
	}
}
