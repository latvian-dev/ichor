package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AppendableAst;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstType;

public abstract class AstDeclaration implements AppendableAst {
	public static class Simple extends AstDeclaration {
		public final String name;
		public AstType type;
		public Object defaultValue;

		public Simple(String name) {
			this.name = name;
			this.type = null;
			this.defaultValue = Special.UNDEFINED;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);

			if (type != null) {
				builder.append(':');
				builder.append(type);
			}

			if (defaultValue != Special.UNDEFINED) {
				builder.append('=');
				builder.appendValue(defaultValue);
			}
		}

		@Override
		public void declare(Context cx, Scope scope, boolean isConst) {
			scope.add(name, cx.eval(scope, defaultValue), isConst);
		}

		@Override
		public void optimize(Parser parser) {
			defaultValue = parser.optimize(defaultValue);
		}
	}

	public static class DestructuredObject extends AstDeclaration {
		@Override
		public void append(AstStringBuilder builder) {
		}

		@Override
		public void declare(Context cx, Scope scope, boolean isConst) {
		}
	}

	public static class DestructuredArray extends AstDeclaration {
		@Override
		public void append(AstStringBuilder builder) {
		}

		@Override
		public void declare(Context cx, Scope scope, boolean isConst) {
		}
	}

	public abstract void declare(Context cx, Scope scope, boolean isConst);

	public void optimize(Parser parser) {
	}
}
