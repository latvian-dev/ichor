package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstReturn extends AstStatement {
	public final Object value;

	public AstReturn(Object value) {
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("return ");
		builder.append(value);
	}

	@Override
	public void interpret(Scope scope) {
		throw new ReturnException(value instanceof Evaluable eval ? eval.eval(scope) : value);
	}

	public static class ReturnException extends RuntimeException {
		public final Object value;

		private ReturnException(Object v) {
			super("return is not supported here!");
			value = v;
		}
	}
}
