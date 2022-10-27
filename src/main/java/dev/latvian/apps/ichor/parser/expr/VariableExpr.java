package dev.latvian.apps.ichor.parser.expr;

import dev.latvian.apps.ichor.token.PositionedToken;

public record VariableExpr(PositionedToken name) implements Expr {
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitVariableExpr(this);
	}
}