package dev.latvian.apps.ichor.parser.expr;

public record GroupingExpr(Expr expression) implements Expr {
	@Override
	public <R> R accept(ExprVisitor<R> visitor) {
		return visitor.visitGroupingExpr(this);
	}
}