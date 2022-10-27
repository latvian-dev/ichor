package dev.latvian.apps.ichor.parser.stmt;

public interface Stmt {
	<R> R accept(StmtVisitor<R> visitor);
}
