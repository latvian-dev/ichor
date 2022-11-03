package dev.latvian.apps.ichor.ast.expression.unary;

public class AstAdd1L extends AstAdditive1 {
	@Override
	public boolean isAdd() {
		return true;
	}

	@Override
	public boolean isLeft() {
		return true;
	}
}
