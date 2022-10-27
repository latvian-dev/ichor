package dev.latvian.apps.ichor.parser.stmt;

import dev.latvian.apps.ichor.token.PositionedToken;

import java.util.List;

public record FunctionStmt(PositionedToken name, List<PositionedToken> params, List<Stmt> body) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitFunctionStmt(this);
	}
}