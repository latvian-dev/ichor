package dev.latvian.apps.ichor.parser.stmt;

import dev.latvian.apps.ichor.parser.expr.Expr;
import dev.latvian.apps.ichor.token.PositionedToken;

public record ReturnStmt(PositionedToken keyword, Expr value) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitReturnStmt(this);
	}
}