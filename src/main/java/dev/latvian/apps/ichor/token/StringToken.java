package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public record StringToken(String value) implements Token, Evaluable, AstAppendable {
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
	public Object eval(Scope scope) {
		return value;
	}

	@Override
	public double evalDouble(Scope scope) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			return Double.NaN;
		}
	}

	@Override
	public int evalInt(Scope scope) {
		try {
			return (int) Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	@Override
	public Evaluable toEvaluable(Parser parser) {
		return this;
	}

	@Override
	public void append(AstStringBuilder builder) {
		AstStringBuilder.wrapString(value, builder.builder);
	}
}
