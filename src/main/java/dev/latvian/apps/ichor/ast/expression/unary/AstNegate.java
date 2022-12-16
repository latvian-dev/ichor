package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstDouble;
import dev.latvian.apps.ichor.error.ScriptError;

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
	public String evalString(Scope scope) {
		throw new ScriptError("Can't negate string!");
	}

	@Override
	public Evaluable optimize() {
		node = node.optimize();

		if (node instanceof AstDouble n) {
			return new AstDouble(-n.value).pos(pos);
		}

		return this;
	}
}
