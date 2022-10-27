package dev.latvian.apps.ichor.parser.expr;

import dev.latvian.apps.ichor.token.PositionedToken;

public record ThisExpr(PositionedToken keyword) implements Expr {
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitThisExpr(this);
	}
}