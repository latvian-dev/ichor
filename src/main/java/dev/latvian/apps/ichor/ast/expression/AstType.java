package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AppendableAst;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public abstract class AstType implements AppendableAst {
	@FunctionalInterface
	public interface CastFunc {
		CastFunc NONE = (scope, from) -> from;

		Object cast(Scope scope, Object from);
	}

	public static class Generic extends AstType {
		public static final Generic ANY = new Generic("any", CastFunc.NONE);
		public static final Generic VOID = new Generic("void", CastFunc.NONE);
		public static final Generic BOOLEAN = new Generic("boolean", Scope::asBoolean);
		public static final Generic NUMBER = new Generic("number", Scope::asNumber);
		public static final Generic STRING = new Generic("string", (scope, from) -> scope.asString(from, false));
		public static final Generic FUNCTION = new Generic("function", CastFunc.NONE);
		public static final Generic OBJECT = new Generic("object", CastFunc.NONE);
		public static final Generic BIGINT = new Generic("bigint", CastFunc.NONE);
		public static final Generic ARRAY = new Generic("Array", CastFunc.NONE);

		public final String name;
		public final CastFunc cast;

		public Generic(String name, CastFunc cast) {
			this.name = name;
			this.cast = cast;
		}

		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);
		}

		@Override
		public Object cast(Scope scope, Object from) {
			return cast.cast(scope, from);
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

	public Object cast(Scope scope, Object from) {
		return from;
	}
}
