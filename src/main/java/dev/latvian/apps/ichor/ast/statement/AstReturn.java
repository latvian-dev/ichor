package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ReturnExit;

public class AstReturn extends AstStatement {
	public Object value;

	public AstReturn(Object value) {
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("return ");
		builder.appendValue(value);
	}

	@Override
	public void interpret(Scope scope) {
		var result = scope.eval(value);
		throw result == Special.UNDEFINED ? ReturnExit.DEFAULT_RETURN : new ReturnExit(result);
	}

	@Override
	public void optimize(Parser parser) {
		value = parser.optimize(value);
	}
}
