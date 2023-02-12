package dev.latvian.apps.ichor.lang.js.ast;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstExpression;

public class AstArguments extends AstExpression {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("arguments");
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return scope.scopeArguments;
	}
}
