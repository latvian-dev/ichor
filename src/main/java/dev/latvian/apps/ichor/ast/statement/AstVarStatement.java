package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.AssignType;

public class AstVarStatement extends AstStatement {
	public final String name;
	public final Object initializer;

	public AstVarStatement(String name, Object initializer) {
		this.name = name;
		this.initializer = initializer;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("var ");
		builder.append(name);

		if (initializer != null) {
			builder.append('=');
			builder.append(initializer);
		}
	}

	@Override
	public void interpret(Scope scope) {
		scope.declareMember(name, initializer == null ? Special.UNDEFINED : scope.eval(initializer), AssignType.MUTABLE);
	}
}
