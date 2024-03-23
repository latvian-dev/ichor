package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;

public class AstClassPrototype extends AstExpression {
	public final Object from;

	public AstClassPrototype(Object from) {
		this.from = from;
	}

	@Override
	public Object eval(Scope scope) {
		var self = scope.eval(from);

		if (self == null) {
			return Special.UNDEFINED;
		} else if (self instanceof PrototypeSupplier ps) {
			return ps.getPrototype(scope);
		} else {
			return scope.getClassPrototype(self.getClass());
		}
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);
		builder.append(".prototype");
	}
}