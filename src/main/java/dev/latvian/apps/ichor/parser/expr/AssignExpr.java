package dev.latvian.apps.ichor.parser.expr;

import dev.latvian.apps.ichor.token.PositionedToken;

public record AssignExpr(PositionedToken name, Expr value) implements Expr {
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitAssignExpr(this);
	}
}