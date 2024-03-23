package dev.latvian.apps.ichor.ast.expression;

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
	public Object eval(Scope scope) {
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		if (self == p) {
			throw new IndexedMemberNotFoundError(index, p, self).pos(this);
		}

		var r = p.getLocal(scope, p.cast(self), index);

		if (r == Special.NOT_FOUND) {
			throw new IndexedMemberNotFoundError(index, p, self).pos(this);
		}

		return r;
	}

	@Override
	public void set(Scope scope, Object value) {
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		if (self == p) {
			throw new IndexedMemberNotFoundError(index, p, self).pos(this);
		}

		if (!p.setLocal(scope, p.cast(self), index, value)) {
			throw new IndexedMemberNotFoundError(index, p, self).pos(this);
		}
	}

	@Override
	public boolean delete(Scope scope) {
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		if (self == p) {
			throw new IndexedMemberNotFoundError(index, p, self).pos(this);
		}

		return p.deleteLocal(scope, p.cast(self), index);
	}
}
