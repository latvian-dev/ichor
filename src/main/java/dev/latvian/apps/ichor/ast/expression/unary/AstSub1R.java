package dev.latvian.apps.ichor.ast.expression.unary;

public class AstSub1R extends AstAdditive1 {
	@Override
	public boolean isAdd() {
		return false;
	}

	@Override
	public boolean isLeft() {
		return false;
	}
}
