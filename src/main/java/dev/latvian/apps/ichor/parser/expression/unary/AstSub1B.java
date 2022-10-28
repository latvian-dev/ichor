package dev.latvian.apps.ichor.parser.expression.unary;

import dev.latvian.apps.ichor.parser.Ast;

public class AstSub1B extends AstUnary {
	public AstSub1B(Ast node) {
		super(node);
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append("--");
		node.append(builder);
	}
}
