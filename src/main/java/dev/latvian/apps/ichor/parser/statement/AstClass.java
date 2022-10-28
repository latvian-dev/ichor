package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.parser.expression.AstVarExpression;

public class AstClass extends AstStatement {
	public final String name;
	public final AstVarExpression parent;
	public final AstFunction[] methods;

	public AstClass(String name, AstVarExpression parent, AstFunction[] methods) {
		this.name = name;
		this.parent = parent;
		this.methods = methods;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append("class ").append(name);

		if (parent != null) {
			builder.append(" extends ");
			parent.append(builder);
		}

		builder.append(' ');

		if (methods.length == 1) {
			methods[0].append(builder);
		} else {
			builder.append("{\n");

			for (var m : methods) {
				builder.append('\t');
				m.append(builder);
				builder.append('\n');
			}

			builder.append('}');
		}
	}
}
