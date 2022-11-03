package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.ast.expression.AstGetScopeMember;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.prototype.PrototypeConstructor;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.util.AssignType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AstClass extends AstStatement {
	public final Prototype prototype;
	public final AstGetScopeMember parent;

	public AstClass(String name, AstGetScopeMember parent, @Nullable AstFunction constructor, List<Map.Entry<String, AstFunction>> methods) {
		this.prototype = PrototypeBuilder.create(name);
		this.parent = parent;

		if (constructor != null) {
			((PrototypeBuilder) prototype).constructor(new Constructor(this, constructor));
		}

		for (var m : methods) {
			((PrototypeBuilder) prototype).function(m.getKey(), m.getValue());
		}
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("class ");
		builder.append(prototype.getPrototypeName());

		if (parent != null) {
			builder.append(" extends ");
			parent.append(builder);
		}

		builder.append(" { ");

		if (prototype instanceof PrototypeBuilder b) {
			boolean first = true;

			for (var k : b.memberKeys()) {
				if (first) {
					first = false;
				} else {
					builder.append(", ");
				}

				builder.append(k);
			}

			builder.append(" }");
		} else {
			builder.append(" ... }");
		}
	}

	@Override
	public void interpret(Scope scope) {
		scope.declareMember(prototype.getPrototypeName(), prototype, AssignType.IMMUTABLE);
	}

	public static class ClassScope extends Scope {
		public Instance classInstance;

		public ClassScope(Scope parent) {
			super(parent);
		}
	}

	public record Constructor(AstClass ast, AstFunction constructor) implements PrototypeConstructor {
		@Override
		public Object construct(Scope scope, Object[] args, boolean hasNew) {
			var p = ast.parent == null ? null : ast.parent.eval(scope);

			if (p != null && !(p instanceof PrototypeSupplier)) {
				throw new RuntimeException("Parent must be a class!");
			}

			var s = new ClassScope(scope);
			s.classInstance = new Instance(ast, null, s);

			/*
			@Override
	public Object construct(Scope scope, Object[] args) {
		var p = parent == null ? null : parent.eval(scope);

		if (p != null && !(p instanceof PrototypeSupplier)) {
			throw new RuntimeException("Parent must be a class!");
		}

		var proto = p == null ? null : ((PrototypeSupplier) p).getPrototype();

		var s = new ClassScope(scope);
		s.root = scope.root;
		s.classInstance = new Instance(this, proto, s);

		for (var c : constructors) {
			if (c.params.length >= args.length) {
				var s1 = new ClassScope(s);

				s1.root.current = s1;

				try {
					c.interpret(s1);
				} finally {
					s.pop();
				}

				break;
			}
		}

		for (var m : methods) {
			s.declareMember(m.name, m, AssignType.IMMUTABLE);
		}

		return s.classInstance;
	}
			 */


			return s;
		}
	}

	public record Instance(AstClass ast, @Nullable Prototype parent, ClassScope classScope) implements Prototype {
		@Override
		public String getPrototypeName() {
			return ast.prototype.getPrototypeName();
		}

		@Override
		public String toString() {
			return ast.prototype.getPrototypeName();
		}

		@Override
		@Nullable
		public Object get(Scope scope, Object self, String name) {
			var c = classScope.getMember(name);

			if (c != Special.NOT_FOUND) {
				return c;
			} else if (parent != null) {
				return parent.get(scope, self, name);
			}

			return Special.NOT_FOUND;
		}
	}
}
