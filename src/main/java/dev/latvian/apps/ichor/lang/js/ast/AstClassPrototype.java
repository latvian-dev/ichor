package dev.latvian.apps.ichor.lang.js.ast;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstExpression;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;

public class AstClassPrototype extends AstExpression {
	public final Object from;

	public AstClassPrototype(Object from) {
		this.from = from;
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var self = cx.eval(scope, from);

		if (self == null) {
			return Special.UNDEFINED;
		} else if (self instanceof PrototypeSupplier ps) {
			return ps.getPrototype(cx, scope);
		} else {
			return cx.getClassPrototype(self.getClass());
		}
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append(".prototype");
	}
}