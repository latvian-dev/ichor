package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.MemberNotFoundError;

public class AstGetByIndex extends AstGetFrom {
	public final int index;

	public AstGetByIndex(Object from, int index) {
		super(from);
		this.index = index;
	}

	@Override
	public Object evalKey(Context cx, Scope scope) {
		return index;
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
		cx.debugger.pushSelf(cx, scope, self);

		var r = p.get(cx, scope, self, index);

		if (r == Special.NOT_FOUND) {
			throw new MemberNotFoundError(toString(), p);
		}

		cx.debugger.get(cx, scope, this, r);
		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);
		cx.debugger.pushSelf(cx, scope, self);
		p.set(cx, scope, self, index, value);
		cx.debugger.set(cx, scope, this, value);
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);
		cx.debugger.pushSelf(cx, scope, self);
		p.delete(cx, scope, self, index);
		cx.debugger.delete(cx, scope, this);
		return true;
	}
}
