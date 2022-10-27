package dev.latvian.apps.ichor.parser.expr;

import dev.latvian.apps.ichor.token.PositionedToken;

public record SetExpr(Expr object, PositionedToken name, Expr value) implements Expr {
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitSetExpr(this);
	}
}