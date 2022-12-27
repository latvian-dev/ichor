package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstSuperStatement extends AstThisStatement {
	public AstSuperStatement(Object[] a) {
		super(a);
	}

	@Override
	public String getStatementName() {
		return "super";
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		if (scope.owner instanceof AstClassFunction func && func.type == AstClassFunction.Type.CONSTRUCTOR) {
			var c = scope.parent.findOwnerClass();

			if (c != null) {
				c.interpretConstructorSuper(scope, arguments);
				return;
			}
		}

		throw new ScriptError("You can only call super() from a constructor");
	}
}
