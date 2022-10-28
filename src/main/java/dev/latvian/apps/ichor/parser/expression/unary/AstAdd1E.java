package dev.latvian.apps.ichor.parser.expression.unary;

import dev.latvian.apps.ichor.parser.Ast;

public class AstAdd1E extends AstUnary {
	public AstAdd1E(Ast node) {
		super(node);
	}

	@Override
	public void append(StringBuilder builder) {
		node.append(builder);
		builder.append("++");
	}
}
