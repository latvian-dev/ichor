package dev.latvian.apps.ichor.parser.stmt;

import dev.latvian.apps.ichor.parser.expr.Expr;

public record IfStmt(Expr condition, Stmt thenBranch, Stmt elseBranch) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitIfStmt(this);
	}
}