package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.parser.Ast;
import dev.latvian.apps.ichor.token.PositionedToken;

public abstract class AstStatement extends Ast {
	public static final AstStatement[] EMPTY_STATEMENT_ARRAY = new AstStatement[0];

	@Override
	public AstStatement pos(PositionedToken token) {
		line = token.line();
		pos = token.pos();
		return this;
	}

	@Override
	public AstStatement pos(Ast other) {
		line = other.line;
		pos = other.pos;
		return this;
	}
}
