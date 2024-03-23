package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.IndexedMemberNotFoundError;
import dev.latvian.apps.ichor.error.NamedMemberNotFoundError;

public class AstGetByEvaluable extends AstGetFrom {
	public Object key;

	public AstGetByEvaluable(Object from, Object key) {
		super(from);
		this.key = key;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append('[');
		builder.appendValue(key);
		builder.append(']');
	}

	@Override
	public Object eval(Scope scope) {
		var k = scope.eval(key);
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		Object r;

		if (k instanceof Number n) {
			var ki = n.intValue();

			if (self == p) {
				throw new IndexedMemberNotFoundError(ki, p, self).pos(this);
			}

			r = p.getLocal(scope, p.cast(self), ki);

			if (r == Special.NOT_FOUND) {
				throw new IndexedMemberNotFoundError(ki, p, self).pos(this);
			}
		} else {
			var ks = scope.asString(k, false);
			r = p.getInternal(scope, self, ks);

			if (r == Special.NOT_FOUND) {
				throw new NamedMemberNotFoundError(ks, p, self).pos(this);
			}
		}

		return r;
	}

	@Override
	public void set(Scope scope, Object value) {
		var k = scope.eval(key);
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		if (k instanceof Number n) {
			var ki = n.intValue();

			if (self == p) {
				throw new IndexedMemberNotFoundError(ki, p, self).pos(this);
			}

			if (!p.setLocal(scope, p.cast(self), ki, value)) {
				throw new IndexedMemberNotFoundError(ki, p, self).pos(this);
			}
		} else {
			var ks = scope.asString(k, false);

			if (!(self == p ? p.setStatic(scope, ks, value) : p.setLocal(scope, p.cast(self), ks, value))) {
				throw new NamedMemberNotFoundError(ks, p, self).pos(this);
			}
		}
	}

	@Override
	public boolean delete(Scope scope) {
		var k = scope.eval(key);
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		if (k instanceof Number n) {
			var ki = n.intValue();

			if (self == p) {
				throw new IndexedMemberNotFoundError(ki, p, self).pos(this);
			}

			return p.deleteLocal(scope, p.cast(self), ki);
		} else {
			var ks = scope.asString(k, false);

			if (self == p) {
				throw new NamedMemberNotFoundError(ks, p, self).pos(this);
			}

			return p.deleteLocal(scope, p.cast(self), ks);
		}
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);
		key = parser.optimize(key);
		return this;
	}
}
