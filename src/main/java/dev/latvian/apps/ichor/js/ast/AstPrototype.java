package dev.latvian.apps.ichor.js.ast;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstExpression;

public class AstPrototype extends AstExpression {
	public final Object from;

	public AstPrototype(Object from) {
		this.from = from;
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return cx.getPrototype(scope, cx.eval(scope, from));
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append(".__prototype__");
	}
}