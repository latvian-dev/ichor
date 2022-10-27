package dev.latvian.apps.ichor.parser.stmt;

public interface StmtVisitor<R> {
	R visitBlockStmt(BlockStmt stmt);

	R visitClassStmt(ClassStmt stmt);

	R visitExpressionStmt(ExpressionStmt stmt);

	R visitFunctionStmt(FunctionStmt stmt);

	R visitIfStmt(IfStmt stmt);

	R visitPrintStmt(PrintStmt stmt);

	R visitReturnStmt(ReturnStmt stmt);

	R visitVarStmt(VarStmt stmt);

	R visitWhileStmt(WhileStmt stmt);
}