package dev.latvian.apps.ichor.parser.expression.unary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;
import dev.latvian.apps.ichor.parser.expression.AstGetBase;

public abstract class AstAdditive1 extends AstUnary {
	public abstract boolean isAdd();

	public abstract boolean isLeft();

	@Override
	public void append(AstStringBuilder builder) {
		if (isLeft()) {
			builder.append(isAdd() ? "++" : "--");
			builder.append(node);
		} else {
			builder.append(node);
			builder.append(isAdd() ? "++" : "--");
		}
	}

	@Override
	public Object eval(Scope scope) {
		return evalDouble(scope);
	}

	@Override
	public double evalDouble(Scope scope) {
		double o = ((AstGetBase) node).evalDouble(scope);
		double n = isAdd() ? o + 1D : o - 1D;
		((AstGetBase) node).set(scope, n);
		return isLeft() ? n : o;
	}

	@Override
	public int evalInt(Scope scope) {
		int o = ((AstGetBase) node).evalInt(scope);
		int n = isAdd() ? o + 1 : o - 1;
		((AstGetBase) node).set(scope, n);
		return isLeft() ? n : o;
	}
}
