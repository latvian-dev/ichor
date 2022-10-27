package dev.latvian.apps.ichor.parser.stmt;

import dev.latvian.apps.ichor.parser.expr.VariableExpr;
import dev.latvian.apps.ichor.token.PositionedToken;

import java.util.List;

public record ClassStmt(PositionedToken name, VariableExpr superclass, List<FunctionStmt> methods) implements Stmt {
	@Override
	public <R> R accept(StmtVisitor<R> visitor) {
		return visitor.visitClassStmt(this);
	}
}