package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AppendableAst;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstType;
import dev.latvian.apps.ichor.error.IndexedMemberNotFoundError;
import dev.latvian.apps.ichor.error.NamedMemberNotFoundError;
import dev.latvian.apps.ichor.error.WIPFeatureError;
import dev.latvian.apps.ichor.prototype.Prototype;

public interface AstDeclaration extends AppendableAst {
	class AstSimple implements AstDeclaration {
		public final String name;
		public AstType type;
		public Object initialValue;

		public AstSimple(String name) {
			this.name = name;
			this.type = null;
			this.initialValue = Special.UNDEFINED;
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

			if (initialValue != Special.UNDEFINED) {
				builder.append('=');
				builder.appendValue(initialValue);
			}
		}

		@Override
		public void declare(Context cx, Scope scope, byte flags) {
			scope.add(name, cx.eval(scope, initialValue), flags);
		}

		@Override
		public void optimize(Parser parser) {
			initialValue = parser.optimize(initialValue);
		}
	}

	interface Destructured extends AppendableAst {
		Destructured[] EMPTY = new Destructured[0];

		void declare(Context cx, Scope scope, byte flags, Object of, Prototype<?> p);
	}

	class AstDestructured implements AstDeclaration {
		public Destructured part;
		public Object of;

		@Override
		public void append(AstStringBuilder builder) {
			part.append(builder);
			builder.append('=');
			builder.appendValue(of);
		}

		@Override
		public void declare(Context cx, Scope scope, byte flags) {
			var self = cx.eval(scope, of);
			var p = cx.getPrototype(scope, self);
			part.declare(cx, scope, flags, self, p);
		}
	}

	record NestedDestructured(String name, Destructured nested) implements Destructured {
		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);
			builder.append(':');
			nested.append(builder);
		}

		@Override
		public void declare(Context cx, Scope scope, byte flags, Object of, Prototype<?> p) {
			var r = p.getInternal(cx, scope, of, name);
			nested.declare(cx, scope, flags, r, cx.getPrototype(scope, r));
		}
	}

	record DestructuredObjectPart(String name, String rename) implements Destructured {
		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);

			if (!rename.isEmpty()) {
				builder.append(':');
				builder.append(rename);
			}
		}

		@Override
		public void declare(Context cx, Scope scope, byte flags, Object of, Prototype<?> p) {
			var r = p.getInternal(cx, scope, of, name);

			if (r == Special.NOT_FOUND) {
				throw new NamedMemberNotFoundError(name, p, p);
			}

			scope.add(rename.isEmpty() ? name : rename, r, flags);
		}
	}

	record DestructuredObject(Destructured[] parts, String rest) implements Destructured {
		@Override
		public void append(AstStringBuilder builder) {
			builder.append('{');

			for (int i = 0; i < parts.length; i++) {
				if (i > 0) {
					builder.append(',');
				}

				parts[i].append(builder);
			}

			if (!rest.isEmpty()) {
				if (parts.length > 0) {
					builder.append(',');
				}

				builder.append("...");
				builder.append(rest);
			}

			builder.append('}');
		}

		@Override
		public void declare(Context cx, Scope scope, byte flags, Object of, Prototype<?> p) {
			/*
			var r = p.getInternal(cx, scope, of, name);

			if (r == Special.NOT_FOUND) {
				throw new NamedMemberNotFoundError(name, p);
			}

			scope.add(rename.isEmpty() ? name : rename, r, flags);

			if(!rest.isEmpty()) {
				scope.add(rest, r, flags);
			}
			 */

			for (var part : parts) {
				part.declare(cx, scope, flags, of, p);
			}

			if (!rest.isEmpty()) {
				throw new WIPFeatureError();
			}
		}
	}

	record DestructuredArrayPart(int index, String name) implements Destructured {
		@Override
		public void append(AstStringBuilder builder) {
			builder.append(name);
		}

		@Override
		public void declare(Context cx, Scope scope, byte flags, Object of, Prototype<?> p) {
			var r = p.getLocal(cx, scope, p.cast(of), index);

			if (r == Special.NOT_FOUND) {
				throw new IndexedMemberNotFoundError(index, p, p);
			}

			scope.add(name, r, flags);
		}
	}

	void declare(Context cx, Scope scope, byte flags);

	default void optimize(Parser parser) {
	}
}
