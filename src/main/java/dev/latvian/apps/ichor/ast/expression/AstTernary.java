package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstTernary extends AstExpression {
	public Object condition;
	public Object ifTrue;
	public Object ifFalse;

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('(');
		builder.appendValue(condition);
		builder.append('?');
		builder.appendValue(ifTrue);
		builder.append(':');
		builder.appendValue(ifFalse);
		builder.append(')');
	}

	@Override
	public Object eval(Scope scope) {
		return scope.asBoolean(condition) ? scope.eval(ifTrue) : scope.eval(ifFalse);
	}

	@Override
	public Object optimize(Parser parser) {
		condition = parser.optimize(condition);
		ifTrue = parser.optimize(ifTrue);
		ifFalse = parser.optimize(ifFalse);
		return condition instanceof Boolean b ? b ? ifTrue : ifFalse : this;
	}
}
