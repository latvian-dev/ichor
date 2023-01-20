package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.MemberNotFoundError;
import dev.latvian.apps.ichor.util.ClassPrototype;
import dev.latvian.apps.ichor.util.FunctionInstance;

public class AstThisExpression extends AstExpression {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("this");
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		Scope s = scope;

		do {
			if (s.owner instanceof ClassPrototype.Instance || s.owner instanceof FunctionInstance func && !func.function.hasMod(AstFunction.Mod.ARROW)) {
				return s.owner;
			}

			s = s.parent;
		}
		while (s != null);

		throw new MemberNotFoundError("this").pos(this);
	}
}
