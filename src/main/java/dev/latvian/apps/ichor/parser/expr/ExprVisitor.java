package dev.latvian.apps.ichor.parser.expr;

public interface ExprVisitor<R> {
	R visitAssignExpr(AssignExpr expr);

	R visitBinaryExpr(BinaryExpr expr);

	R visitCallExpr(CallExpr expr);

	R visitGetExpr(GetExpr expr);

	R visitGroupingExpr(GroupingExpr expr);

	R visitLiteralExpr(LiteralExpr expr);

	R visitLogicalExpr(LogicalExpr expr);

	R visitSetExpr(SetExpr expr);

	R visitSuperExpr(SuperExpr expr);

	R visitThisExpr(ThisExpr expr);

	R visitUnaryExpr(UnaryExpr expr);

	R visitVariableExpr(VariableExpr expr);
}