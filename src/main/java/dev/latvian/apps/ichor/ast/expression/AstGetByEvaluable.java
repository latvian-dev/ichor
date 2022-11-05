package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstGetByEvaluable extends AstGetFrom {
	public final Object key;

	public AstGetByEvaluable(Object from, Object key) {
		super(from);
		this.key = key;
	}

	@Override
	public Object evalKey(Scope scope) {
		return scope.eval(key);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append('[');
		builder.append(key);
		builder.append(']');
	}

	@Override
	public Object eval(Scope scope) {
		var cx = scope.getContext();
		var self = scope.eval(from);
		var p = cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(scope, self);
		}

		var k = scope.eval(key);

		Object r;

		if (k instanceof Number) {
			r = p.get(scope, self, ((Number) k).intValue());
		} else {
			r = p.get(scope, self, scope.getContext().asString(scope, k));
		}

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot find " + this + " of " + p);
		}

		if (cx.debugger != null) {
			cx.debugger.get(scope, this, r);
		}

		return r;
	}

	@Override
	public void set(Scope scope, Object value) {
		var cx = scope.getContext();
		var self = scope.eval(from);
		var p = cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(scope, self);
		}

		var k = scope.eval(key);

		if (k instanceof Number) {
			p.set(scope, self, ((Number) k).intValue(), value);
		} else {
			p.set(scope, self, scope.getContext().asString(scope, k), value);
		}

		if (cx.debugger != null) {
			cx.debugger.set(scope, this, value);
		}
	}
}
