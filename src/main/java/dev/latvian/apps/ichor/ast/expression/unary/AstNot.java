package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.token.BooleanToken;

public class AstNot extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('!');
		builder.appendValue(node);
	}

	@Override
	public Object eval(Scope scope) {
		return evalBoolean(scope);
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !node.evalBoolean(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return node.evalBoolean(scope) ? 0 : 1;
	}

	@Override
	public double evalDouble(Scope scope) {
		return evalInt(scope);
	}

	@Override
	public Evaluable optimize(Parser parser) {
		node = node.optimize(parser);

		if (node instanceof BooleanToken n) {
			return BooleanToken.of(!n.value);
		}

		return this;
	}
}
