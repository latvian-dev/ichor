package dev.latvian.apps.ichor.parser.stmt;

import dev.latvian.apps.ichor.parser.expr.Expr;

public record ExpressionStmt(Expr expression) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitExpressionStmt(this);
	}
}