package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AppendableAst;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.AssignType;

public abstract class AstParam implements AppendableAst {
	private static final String DEFAULT_TYPE = "any";

	public static class Simple extends AstParam {
		public final String name;
		// public final String type;

		public Simple(String name) {
			this.name = name;
			// this.type = DEFAULT_TYPE;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);

			if (defaultValue != Special.UNDEFINED) {
				builder.append('=');
				builder.appendValue(defaultValue);
			}
		}

		@Override
		public void declare(Scope scope, Object value, AssignType type) {
			scope.declareMember(name, value, type);
		}
	}

	public static class DestructuredObject extends AstParam {
		@Override
		public void append(AstStringBuilder builder) {
		}

		@Override
		public void declare(Scope scope, Object value, AssignType type) {
		}
	}

	public static class DestructuredArray extends AstParam {
		@Override
		public void append(AstStringBuilder builder) {
		}

		@Override
		public void declare(Scope scope, Object value, AssignType type) {
		}
	}

	public Object defaultValue;

	public AstParam() {
		this.defaultValue = Special.UNDEFINED;
	}

	public abstract void declare(Scope scope, Object value, AssignType type);
}
