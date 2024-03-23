package dev.latvian.apps.ichor.ast.expression;

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
		builder.appendValue(value);
	}

	@Override
	public Object eval(Scope scope) {
		var v = scope.eval(value);
		get.set(scope, v);
		return v;
	}

	@Override
	public Object optimize(Parser parser) {
		value = parser.optimize(value);
		return this;
	}
}
