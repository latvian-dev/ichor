package dev.latvian.apps.ichor.parser.stmt;

import dev.latvian.apps.ichor.parser.expr.Expr;
import dev.latvian.apps.ichor.token.PositionedToken;

public record VarStmt(PositionedToken name, Expr initializer) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitVarStmt(this);
	}
}