package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;
import dev.latvian.apps.ichor.parser.expression.AstGetScopeMember;

public class AstClass extends AstStatement {
	public final String name;
	public final AstGetScopeMember parent;
	public final AstFunction[] methods;

	public AstClass(String name, AstGetScopeMember parent, AstFunction[] methods) {
		this.name = name;
		this.parent = parent;
		this.methods = methods;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("class ");
		builder.append(name);

		if (parent != null) {
			builder.append(" extends ");
			parent.append(builder);
		}

		builder.append(' ');

		if (methods.length == 1) {
			methods[0].append(builder);
		} else {
			builder.append("{ ");

			for (var m : methods) {
				m.append(builder);
				builder.append(' ');
			}

			builder.append('}');
		}
	}

	@Override
	public void interpret(Scope scope) {
	}
}
