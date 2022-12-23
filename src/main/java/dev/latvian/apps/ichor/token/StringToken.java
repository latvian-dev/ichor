package dev.latvian.apps.ichor.token;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.EvaluableStringBase;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class StringToken implements Token, EvaluableStringBase, AstAppendable {
	public static final StringToken EMPTY = new StringToken("");

	public static StringToken of(String string) {
		return string.isEmpty() ? EMPTY : new StringToken(string);
	}

	public final String value;

	private StringToken(String v) {
		value = v;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && value.equals(obj.toString());
	}

	@Override
	public Evaluable toEvaluable(Parser parser, TokenPos pos) {
		return this;
	}

	@Override
	public void evalString(Scope scope, StringBuilder builder) {
		builder.append(value);
	}

	@Override
	public String eval(Scope scope) {
		return value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		AstStringBuilder.wrapString(value, builder.builder);
	}
}
