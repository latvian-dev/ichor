package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AppendableAst;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstParam implements AppendableAst {
	public final String name;
	public AstType type;
	public Object defaultValue;

	public AstParam(String name) {
		this.name = name;
		this.type = null;
		this.defaultValue = Special.UNDEFINED;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(name);

		if (type != null) {
			builder.append(':');
			builder.append(type);
		}

		if (defaultValue != Special.UNDEFINED) {
			builder.append('=');
			builder.appendValue(defaultValue);
		}
	}
}
