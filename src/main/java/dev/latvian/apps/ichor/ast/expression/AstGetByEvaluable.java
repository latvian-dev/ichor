package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

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
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);
		cx.debugger.pushSelf(cx, scope, self);

		var k = evalKey(cx, scope);

		Object r;

		if (k instanceof Number n) {
			r = p.get(cx, scope, self, n.intValue());
		} else {
			r = p.get(cx, scope, self, cx.asString(scope, k, false));
		}

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot find " + this + " of " + p);
		}

		cx.debugger.get(cx, scope, this, r);
		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);
		cx.debugger.pushSelf(cx, scope, self);

		var k = evalKey(cx, scope);

		if (k instanceof Number) {
			p.set(cx, scope, self, ((Number) k).intValue(), value);
		} else {
			p.set(cx, scope, self, cx.asString(scope, k, false), value);
		}

		cx.debugger.set(cx, scope, this, value);
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);
		cx.debugger.pushSelf(cx, scope, self);

		var k = evalKey(cx, scope);

		if (k instanceof Number) {
			p.delete(cx, scope, self, ((Number) k).intValue());
		} else {
			p.delete(cx, scope, self, cx.asString(scope, k, false));
		}

		cx.debugger.delete(cx, scope, this);
		return true;
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);
		key = parser.optimize(key);
		return this;
	}
}
