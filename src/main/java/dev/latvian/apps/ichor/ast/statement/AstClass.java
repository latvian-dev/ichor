package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstFunction;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.util.AssignType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class AstClass extends AstStatement implements Callable {
	public final String name;
	public final AstFunction constructor;
	public final Object parent;
	public final Map<String, AstFunction> methods;
	public final Map<String, AstFunction> getters;
	public final Map<String, AstFunction> setters;

	public AstClass(String name, @Nullable Object parent, @Nullable AstFunction constructor, List<Map.Entry<String, AstFunction>> methods) {
		this.name = name;
		this.constructor = constructor;
		this.parent = parent;
		this.methods = new HashMap<>();
		this.getters = new HashMap<>();
		this.setters = new HashMap<>();

		for (var m : methods) {
			if (m.getValue().hasMod(AstFunction.MOD_GET)) {
				this.getters.put(m.getKey(), m.getValue());
			} else if (m.getValue().hasMod(AstFunction.MOD_SET)) {
				this.setters.put(m.getKey(), m.getValue());
			} else {
				this.methods.put(m.getKey(), m.getValue());
			}
		}
	}

	@Override
	public String toString() {
		if (parent == null) {
			return "class " + name;
		} else {
			return "class " + name + " extends " + parent;
		}
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("class ");
		builder.append(name);

		if (parent != null) {
			builder.append(" extends ");
			builder.append(parent);
		}

		builder.append(" {");

		var keys = new LinkedHashSet<String>();
		keys.addAll(methods.keySet());
		keys.addAll(getters.keySet());
		keys.addAll(setters.keySet());

		if (keys.isEmpty()) {
			builder.append("...}");
		} else {
			builder.append(String.join(",", keys));
			builder.append("}");
		}
	}

	@Override
	public void interpret(Scope scope) {
		scope.declareMember(name, this, AssignType.IMMUTABLE);
	}

	@Override
	public Object call(Scope scope, Object self, Object[] args) {
		return construct(scope, args);
	}

	@Override
	public Object construct(Scope scope, Object[] args) {
		var p = scope.eval(parent);

		if (parent != null && !(p instanceof PrototypeSupplier)) {
			throw new ScriptError("Cannot extend " + p);
		}

		var s = scope.push(this);
		s.owner = new Instance(this, s);

		if (constructor != null) {
			constructor.call(s, s.owner, args);
		}

		return s.owner;
	}

	public static class Instance implements Prototype {
		public final AstClass ast;
		public Scope classScope;

		public Instance(AstClass ast, Scope classScope) {
			this.ast = ast;
			this.classScope = classScope;
		}

		@Override
		public String getPrototypeName() {
			return ast.name;
		}

		@Override
		public String toString() {
			return ast.name;
		}

		public void interpretConstructorSuper(Scope scope, Object[] args) {
			/*
			if (parentInstance != null) {
				throw new ScriptError("super() called twice in same constructor");
			} else if (parent == null) {
				throw new ScriptError("super() called in constructor of class without parent");
			}

			var p = parent.construct(scope, args);

			if (p instanceof Instance i) {
				parentInstance = i;
			} else {
				throw new ScriptError("super() returned " + p + " instead of instance of " + parent);
			}
			 */
		}

		@Override
		@Nullable
		public Object get(Scope scope, Object self, String name) {
			return classScope.getMember(name);
		}
	}

	// public record JavaInstance
}
