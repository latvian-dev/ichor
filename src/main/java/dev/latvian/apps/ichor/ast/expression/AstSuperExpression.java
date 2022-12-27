package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.SuperInstance;

public class AstSuperExpression extends AstExpression {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("super");
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return new SuperInstance(scope);
	}
}
