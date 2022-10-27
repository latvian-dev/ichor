package dev.latvian.apps.ichor.parser.stmt;

import java.util.List;

public record BlockStmt(List<Stmt> statements) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitBlockStmt(this);
	}
}