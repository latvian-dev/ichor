package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstString;

public record StringToken(String value) implements Token {
	public static final StringToken EMPTY = new StringToken("");

	public static StringToken of(String string) {
		return string.isEmpty() ? EMPTY : new StringToken(string);
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		AstStringBuilder.wrapString(value, sb);
		return sb.toString();
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return new AstString(value).pos(pos);
	}
}
