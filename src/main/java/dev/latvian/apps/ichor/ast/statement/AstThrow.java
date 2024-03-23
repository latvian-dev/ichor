package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptThrowError;

public class AstThrow extends AstStatement {
	public Object exception;

	public AstThrow(Object exception) {
		this.exception = exception;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("throw(");
		builder.appendValue(exception);
		builder.append(')');
	}

	@Override
	public void interpret(Scope scope) {
		var e = scope.eval(exception);

		if (e instanceof Throwable t) {
			throw new ScriptThrowError(t).pos(this);
		} else {
			throw new ScriptThrowError(String.valueOf(e)).pos(this);
		}
	}

	@Override
	public void optimize(Parser parser) {
		exception = parser.optimize(exception);
	}
}
