package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

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
	public void interpret(Context cx, Scope scope) {
		var e = cx.eval(scope, exception);

		if (e instanceof Throwable t) {
			throw new ScriptError(t).pos(this);
		} else {
			throw new ScriptError(String.valueOf(e)).pos(this);
		}
	}

	@Override
	public void optimize(Parser parser) {
		exception = parser.optimize(exception);
	}
}
