package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.AssignType;

public class AstConstStatement extends AstStatement {
	public final String name;
	public final Evaluable initializer; // TODO: Let initializer be null, in case const is defined later

	public AstConstStatement(String name, Evaluable initializer) {
		this.name = name;
		this.initializer = initializer;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("const ");
		builder.append(name);
		builder.append('=');
		builder.append(initializer);
	}

	@Override
	public void interpret(Scope scope) {
		scope.declareMember(name, initializer.eval(scope), AssignType.IMMUTABLE);
	}
}
