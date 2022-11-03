package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.AssignType;

public class AstConstStatement extends AstStatement {
	public final String name;
	public final Object initializer;

	public AstConstStatement(String name, Object initializer) {
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
		scope.declareMember(name, scope.eval(initializer), AssignType.IMMUTABLE);
	}
}
