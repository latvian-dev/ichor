package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstString extends AstStringBase {
	public final String value;

	public AstString(String v) {
		value = v;
	}

	@Override
	public String evalString(Scope scope) {
		return value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		AstStringBuilder.wrapString(value, builder.builder);
	}
}
