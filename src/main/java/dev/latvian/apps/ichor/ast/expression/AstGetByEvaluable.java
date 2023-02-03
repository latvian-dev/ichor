package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
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
	public Object eval(Context cx, Scope scope) {
		var k = cx.eval(scope, key);
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		Object r;

		if (k instanceof Number n) {
			var ki = n.intValue();

			if (self == p) {
				throw new IndexedMemberNotFoundError(ki, self).pos(this);
			}

			r = p.getLocal(cx, scope, p.cast(self), ki);

			if (r == Special.NOT_FOUND) {
				throw new IndexedMemberNotFoundError(ki, self).pos(this);
			}
		} else {
			var ks = cx.asString(scope, k, false);
			r = self == p ? p.getStatic(cx, scope, ks) : p.getLocal(cx, scope, p.cast(self), ks);

			if (r == Special.NOT_FOUND) {
				throw new NamedMemberNotFoundError(ks, self).pos(this);
			}
		}

		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		var k = cx.eval(scope, key);
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		if (k instanceof Number n) {
			var ki = n.intValue();

			if (self == p) {
				throw new IndexedMemberNotFoundError(ki, self).pos(this);
			}

			if (!p.setLocal(cx, scope, p.cast(self), ki, value)) {
				throw new IndexedMemberNotFoundError(ki, self).pos(this);
			}
		} else {
			var ks = cx.asString(scope, k, false);

			if (!(self == p ? p.setStatic(cx, scope, ks, value) : p.setLocal(cx, scope, p.cast(self), ks, value))) {
				throw new NamedMemberNotFoundError(ks, self).pos(this);
			}
		}
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		var k = cx.eval(scope, key);
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		if (k instanceof Number n) {
			var ki = n.intValue();

			if (self == p) {
				throw new IndexedMemberNotFoundError(ki, self).pos(this);
			}

			return p.deleteLocal(cx, scope, p.cast(self), ki);
		} else {
			var ks = cx.asString(scope, k, false);

			if (self == p) {
				throw new NamedMemberNotFoundError(ks, self).pos(this);
			}

			return p.deleteLocal(cx, scope, p.cast(self), ks);
		}
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);
		key = parser.optimize(key);
		return this;
	}
}
