package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstAppendable;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstParam implements AstAppendable {
	public static final AstParam[] EMPTY_PARAM_ARRAY = new AstParam[0];
	private static final String DEFAULT_TYPE = "any";

	public final String name;
	public final String type;
	public Object defaultValue;

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
