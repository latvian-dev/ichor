package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import org.jetbrains.annotations.Nullable;

public interface Evaluable extends PrototypeSupplier {
	@Override
	default Prototype getPrototype(Context cx, Scope scope) {
		return cx.getPrototype(scope, eval(cx, scope));
	}

	Object eval(Context cx, Scope scope);

	@Nullable
	default Object evalSelf(Context cx, Scope scope) {
		return null;
	}

	default void evalString(Context cx, Scope scope, StringBuilder builder) {
		var e = this.eval(cx, scope);

		if (e == this) {
			builder.append(this);
		} else {
			cx.asString(scope, e, builder, false);
		}
	}

	default double evalDouble(Context cx, Scope scope) {
		var e = this.eval(cx, scope);

		if (e == this) {
			return Double.NaN;
		} else {
			return cx.asDouble(scope, e);
		}
	}

	default int evalInt(Context cx, Scope scope) {
		var d = evalDouble(cx, scope);
		return Double.isNaN(d) ? 0 : (int) d;
	}

	default boolean evalBoolean(Context cx, Scope scope) {
		var d = evalDouble(cx, scope);
		return !Double.isNaN(d) && d != 0D;
	}

	default Object optimize(Parser parser) {
		return this;
	}
}
