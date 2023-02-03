package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.IndexedMemberNotFoundError;

public class AstGetByIndex extends AstGetFrom {
	public final int index;

	public AstGetByIndex(Object from, int index) {
		super(from);
		this.index = index;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append('[');
		builder.appendValue(index);
		builder.append(']');
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		if (self == p) {
			throw new IndexedMemberNotFoundError(index, self).pos(this);
		}

		var r = p.getLocal(cx, scope, p.cast(self), index);

		if (r == Special.NOT_FOUND) {
			throw new IndexedMemberNotFoundError(index, self).pos(this);
		}

		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		if (self == p) {
			throw new IndexedMemberNotFoundError(index, self).pos(this);
		}

		if (!p.setLocal(cx, scope, p.cast(self), index, value)) {
			throw new IndexedMemberNotFoundError(index, self).pos(this);
		}
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		if (self == p) {
			throw new IndexedMemberNotFoundError(index, self).pos(this);
		}

		return p.deleteLocal(cx, scope, p.cast(self), index);
	}
}
