package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstSet extends AstExpression {
	public final AstGetBase get;
	public Object value;

	public AstSet(AstGetBase get, Object value) {
		this.get = get;
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		get.append(builder);
		builder.append('=');
		builder.append(value);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var v = cx.eval(scope, value);
		get.set(cx, scope, v);
		return v;
	}

	@Override
	public Object optimize(Parser parser) {
		value = parser.optimize(value);
		return this;
	}
}
