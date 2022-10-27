package dev.latvian.apps.ichor.parser.expr;

public interface Expr {
	<R> R accept(ExprVisitor<R> visitor);
}
