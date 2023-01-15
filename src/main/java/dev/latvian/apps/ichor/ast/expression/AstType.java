package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.ast.AppendableAst;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public abstract class AstType implements AppendableAst {
	public static class Generic extends AstType {
		public static final Generic ANY = new Generic("any");
		public static final Generic VOID = new Generic("void");
		public static final Generic BOOLEAN = new Generic("boolean");
		public static final Generic NUMBER = new Generic("number");
		public static final Generic STRING = new Generic("string");
		public static final Generic FUNCTION = new Generic("function");
		public static final Generic OBJECT = new Generic("object");
		public static final Generic BIGINT = new Generic("bigint");
		public static final Generic ARRAY = new Generic("Array");

		public final String name;

		public Generic(String name) {
			this.name = name;
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);
		}
	}

	public static class Array extends AstType {
		public final AstType type;

		public Array(AstType type) {
			this.type = type;
		}

		@Override
		public void append(AstStringBuilder builder) {
			type.append(builder);
			builder.append('[');
			builder.append(']');
		}
	}

	public static class Or extends AstType {
		public final AstType a;
		public final AstType b;

		public Or(AstType a, AstType b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public void append(AstStringBuilder builder) {
			a.append(builder);
			builder.append('|');
			b.append(builder);
		}
	}

	public static class Typed extends AstType {
		public final AstType type;
		public final AstType[] subTypes;

		public Typed(AstType type, AstType[] subTypes) {
			this.type = type;
			this.subTypes = subTypes;
		}

		@Override
		public void append(AstStringBuilder builder) {
			type.append(builder);
			builder.append('<');

			for (int i = 0; i < subTypes.length; i++) {
				if (i > 0) {
					builder.append(',');
				}

				subTypes[i].append(builder);
			}

			builder.append('>');
		}
	}
}
