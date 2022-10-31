package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstBreak extends AstStatement {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("break");
	}

	@Override
	public void interpret(Scope scope) {
		throw new BreakException();
	}

	public static class BreakException extends RuntimeException {
		private BreakException() {
			super("break is not supported here!");
		}
	}
}
