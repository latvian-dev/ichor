package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstTernary extends AstExpression {
	public final Evaluable condition;
	public final Evaluable ifTrue;
	public final Evaluable ifFalse;

	public AstTernary(Evaluable condition, Evaluable ifTrue, Evaluable ifFalse) {
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('(');
		builder.append(condition);
		builder.append('?');
		builder.append(ifTrue);
		builder.append(':');
		builder.append(ifFalse);
		builder.append(')');
	}

	@Override
	public Object eval(Scope scope) {
		return condition.evalBoolean(scope) ? ifTrue.eval(scope) : ifFalse.eval(scope);
	}
}
