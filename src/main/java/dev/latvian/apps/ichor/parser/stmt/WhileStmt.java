package dev.latvian.apps.ichor.parser.stmt;

import dev.latvian.apps.ichor.parser.expr.Expr;

public record WhileStmt(Expr condition, Stmt body) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitWhileStmt(this);
	}
}