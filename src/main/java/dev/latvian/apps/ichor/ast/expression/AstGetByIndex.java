package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstGetByIndex extends AstGetFrom {
	public final int index;

	public AstGetByIndex(Evaluable from, int index) {
		super(from);
		this.index = index;
	}

	@Override
	public Object evalKey(Scope scope) {
		return index;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append('[');
		builder.append(index);
		builder.append(']');
	}

	@Override
	public Object eval(Scope scope) {
		var cx = scope.getContext();
		var self = from.eval(scope);
		var p = cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(scope, self);
		}

		var r = p.get(scope, self, index);

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
		var self = from.eval(scope);
		var p = cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(scope, self);
		}

		p.set(scope, self, index, value);

		if (cx.debugger != null) {
			cx.debugger.set(scope, this, value);
		}
	}
}
