package dev.latvian.apps.ichor.parser.expr;

public record LiteralExpr(Object value) implements Expr {
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitLiteralExpr(this);
	}
}