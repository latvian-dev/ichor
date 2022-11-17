package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.ast.Ast;
import dev.latvian.apps.ichor.token.TokenPosSupplier;

public abstract class AstStatement extends Ast implements Interpretable {
	@Override
	public AstStatement pos(TokenPosSupplier pos) {
		super.pos(pos);
		return this;
	}
}
