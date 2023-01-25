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
	public Object evalKey(Context cx, Scope scope) {
		return cx.eval(scope, key);
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
		var k = evalKey(cx, scope);

		Object r;

		if (k instanceof Number n) {
			var ki = n.intValue();
			r = cx.wrap(scope, evalSelf(cx, scope)).get(cx, scope, ki);

			if (r == Special.NOT_FOUND) {
				throw new IndexedMemberNotFoundError(ki).pos(this);
			}
		} else {
			var ks = cx.asString(scope, k, false);
			r = cx.wrap(scope, evalSelf(cx, scope)).get(cx, scope, ks);

			if (r == Special.NOT_FOUND) {
				throw new NamedMemberNotFoundError(ks).pos(this);
			}
		}

		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		var k = evalKey(cx, scope);

		if (k instanceof Number n) {
			var ki = n.intValue();

			if (!cx.wrap(scope, evalSelf(cx, scope)).set(cx, scope, ki, value)) {
				throw new IndexedMemberNotFoundError(ki).pos(this);
			}
		} else {
			var ks = cx.asString(scope, k, false);

			if (!cx.wrap(scope, evalSelf(cx, scope)).set(cx, scope, ks, value)) {
				throw new NamedMemberNotFoundError(ks).pos(this);
			}
		}
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		var k = evalKey(cx, scope);

		if (k instanceof Number n) {
			var ki = n.intValue();
			return cx.wrap(scope, evalSelf(cx, scope)).delete(cx, scope, ki);
		} else {
			var ks = cx.asString(scope, k, false);
			return cx.wrap(scope, evalSelf(cx, scope)).delete(cx, scope, ks);
		}
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);
		key = parser.optimize(key);
		return this;
	}
}
