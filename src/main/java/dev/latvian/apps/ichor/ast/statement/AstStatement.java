package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.prototype.Interpretable;
import dev.latvian.apps.ichor.token.PositionedToken;

public abstract class AstStatement extends Ast implements Interpretable {
	@Override
	public AstStatement pos(PositionedToken token) {
		pos = token.pos();
		return this;
	}

	@Override
	public AstStatement pos(Ast other) {
		pos = other.pos;
		return this;
	}
}
