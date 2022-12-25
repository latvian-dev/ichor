package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ReturnExit;

public class AstReturn extends AstStatement {
	public final Evaluable value;

	public AstReturn(Evaluable value) {
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("return ");
		builder.append(value);
	}

	@Override
	public void interpret(Scope scope) {
		var result = value.eval(scope);
		throw result == Special.UNDEFINED ? ReturnExit.DEFAULT_RETURN : new ReturnExit(result);
	}
}
