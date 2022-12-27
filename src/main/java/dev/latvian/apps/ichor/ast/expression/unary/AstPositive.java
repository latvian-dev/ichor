package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstPositive extends AstUnary {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append('+');
		builder.appendValue(node);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		throw new ScriptError("Something didn't call optimize()");
	}

	@Override
	public Object optimize(Parser parser) {
		return parser.optimize(node);
	}
}
