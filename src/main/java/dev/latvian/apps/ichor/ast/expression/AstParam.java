package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstParam implements AstAppendable {
	private static final String DEFAULT_TYPE = "any";

	public final String name;
	public final String type;
	public Evaluable defaultValue;

	public AstParam(String name) {
		this.name = name;
		this.type = DEFAULT_TYPE;
		this.defaultValue = Special.UNDEFINED;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);

		if (defaultValue != Special.UNDEFINED) {
			builder.append(" = ");
			builder.appendValue(defaultValue);
		}
	}
}
