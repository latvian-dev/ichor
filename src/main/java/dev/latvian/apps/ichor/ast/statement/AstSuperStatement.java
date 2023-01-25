package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.util.ClassPrototype;

public class AstSuperStatement extends AstThisStatement {
	public static class InvalidCallError extends ScriptError {
		public InvalidCallError() {
			super("You can only call super() from a constructor");
		}
	}

	public AstSuperStatement(Object[] a) {
		super(a);
	}

	@Override
	public String getStatementName() {
		return "super";
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		if (scope.scopeThis instanceof ClassPrototype.Instance c) {
			c.interpretConstructorSuper(arguments);
		} else {
			throw new InvalidCallError();
		}
	}
}
