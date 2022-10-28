package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.parser.Ast;
import dev.latvian.apps.ichor.token.PositionedToken;

public abstract class AstExpression extends Ast {
	public static final AstExpression[] EMPTY_EXPRESSION_ARRAY = new AstExpression[0];

	@Override
	public AstExpression pos(PositionedToken token) {
		line = token.line();
		pos = token.pos();
		return this;
	}

	@Override
	public AstExpression pos(Ast other) {
		line = other.line;
		pos = other.pos;
		return this;
	}
}
