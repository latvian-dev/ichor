package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstContinue extends AstStatement {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("continue");
	}

	@Override
	public void interpret(Scope scope) {
		throw new ContinueException();
	}

	public static class ContinueException extends RuntimeException {
		private ContinueException() {
			super("continue is not supported here!");
		}
	}
}
