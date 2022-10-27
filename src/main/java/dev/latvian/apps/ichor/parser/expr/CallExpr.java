package dev.latvian.apps.ichor.parser.expr;

import dev.latvian.apps.ichor.token.PositionedToken;

import java.util.List;

public record CallExpr(Expr callee, PositionedToken paren, List<Expr> arguments) implements Expr {
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitCallExpr(this);
	}
}