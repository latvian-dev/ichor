package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.ThisInstance;

public class AstThisExpression extends AstExpression {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("this");
	}

	@Override
	public Object eval(Scope scope) {
		return new ThisInstance(scope);
	}
}
