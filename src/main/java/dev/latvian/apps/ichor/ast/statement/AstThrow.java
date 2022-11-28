package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstThrow extends AstStatement {
	public final Evaluable exception;

	public AstThrow(Evaluable exception) {
		this.exception = exception;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("throw(");
		builder.append(exception);
		builder.append(')');
	}

	@Override
	public void interpret(Scope scope) {
		var e = exception.eval(scope);

		if (e instanceof Throwable t) {
			throw new ScriptError(t).pos(this);
		} else {
			throw new ScriptError(String.valueOf(e)).pos(this);
		}
	}
}
