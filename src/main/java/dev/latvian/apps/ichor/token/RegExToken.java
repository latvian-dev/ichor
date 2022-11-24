package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.ast.expression.AstRegEx;

import java.util.regex.Pattern;

public record RegExToken(Pattern pattern) implements Token {
	@Override
	public String toString() {
		var sb = new StringBuilder();
		AstRegEx.appendRegEx(sb, pattern);
		return sb.toString();
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return new AstRegEx(pattern).pos(pos);
	}
}
