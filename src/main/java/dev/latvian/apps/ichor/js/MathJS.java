package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
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

	public static final Prototype PROTOTYPE = new PrototypeBuilder("Math") {
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
				default -> super.get(cx, scope, name);
			};
		}
	};
}
