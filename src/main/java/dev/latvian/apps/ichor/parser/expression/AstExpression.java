package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.parser.Ast;
import dev.latvian.apps.ichor.token.PositionedToken;

public abstract class AstExpression extends Ast implements Evaluable {
	@Override
	public AstExpression pos(PositionedToken token) {
		pos = token.pos();
		return this;
	}

	@Override
	public AstExpression pos(Ast other) {
		pos = other.pos;
		return this;
	}
}
