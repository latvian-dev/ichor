package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.token.DoubleToken;

public class AstNegate extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('-');
		builder.appendValue(node);
	}

	@Override
	public Object eval(Scope scope) {
		return evalDouble(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		return -node.evalDouble(scope);
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return !node.evalBoolean(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return -node.evalInt(scope);
	}

	@Override
	public void evalString(Scope scope, StringBuilder builder) {
		throw new ScriptError("Can't negate string!");
	}

	@Override
	public Evaluable optimize(Parser parser) {
		node = node.optimize(parser);

		if (node instanceof DoubleToken n) {
			return DoubleToken.of(-n.value);
		}

		return this;
	}
}
