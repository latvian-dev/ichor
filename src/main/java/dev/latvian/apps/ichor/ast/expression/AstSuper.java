package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.CallableAst;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

public class AstSuper extends AstExpression implements Prototype, CallableAst {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("super");
	}

	@Override
	public String getPrototypeName() {
		return "super";
	}

	@Override
	@Nullable
	public Object get(Scope scope, Object self, String name) {
		return Special.NOT_FOUND;
	}

	@Override
	public boolean set(Scope scope, Object self, String name, @Nullable Object value) {
		return false;
	}

	@Override
	public Evaluable createCall(Object[] arguments, boolean isNew) {
		return new AstCall(arguments);
	}

	public static class AstCall extends AstExpression {
		public final Object[] arguments;

		public AstCall(Object[] arguments) {
			this.arguments = arguments;
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.append("super(");

			for (int i = 0; i < arguments.length; i++) {
				if (i > 0) {
					builder.append(',');
				}

				builder.appendValue(arguments[i]);
			}

			builder.append(')');
		}

		@Override
		public Object eval(Scope scope) {
			if (scope.owner instanceof AstFunction func && func.hasMod(AstFunction.MOD_CONSTRUCTOR)) {
				var c = scope.parent.findOwnerClass();

				if (c != null) {
					c.interpretConstructorSuper(scope, arguments);
					return c;
				}
			}

			throw new ScriptError("You can only call super() from a constructor");
		}
	}
}
