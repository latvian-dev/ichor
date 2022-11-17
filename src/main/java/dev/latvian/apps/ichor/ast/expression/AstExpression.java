package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.token.TokenPosSupplier;

public abstract class AstExpression extends Ast implements Evaluable {
	@Override
	public AstExpression pos(TokenPosSupplier pos) {
		super.pos(pos);
		return this;
	}
}
