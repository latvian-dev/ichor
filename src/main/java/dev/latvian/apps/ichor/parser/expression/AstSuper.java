package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstSuper extends AstExpression {
	public final String target;

	public AstSuper(String target) {
		this.target = target;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("super.");
		builder.append(target);
	}

	@Override
	public Object eval(Scope scope) {
		return scope.parent.getMember(target);
	}
}
