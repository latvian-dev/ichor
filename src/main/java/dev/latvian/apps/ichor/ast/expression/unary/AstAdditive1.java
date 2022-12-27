package dev.latvian.apps.ichor.ast.expression.unary;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstGetBase;

public abstract class AstAdditive1 extends AstUnary {
	public abstract boolean isAdd();

	public abstract boolean isLeft();

	@Override
	public void append(AstStringBuilder builder) {
		if (isLeft()) {
			builder.append(isAdd() ? "++" : "--");
			builder.appendValue(node);
		} else {
			builder.appendValue(node);
			builder.append(isAdd() ? "++" : "--");
		}
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return evalDouble(cx, scope);
	}

	@Override
	public double evalDouble(Context cx, Scope scope) {
		double o = cx.asDouble(scope, node);
		double n = isAdd() ? o + 1D : o - 1D;
		((AstGetBase) node).set(cx, scope, n);
		return isLeft() ? n : o;
	}
}
