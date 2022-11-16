package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.util.AssignType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class AstClass extends AstStatement implements Prototype, Callable {
	public final String name;
	public Evaluable parent;
	public AstClassFunction constructor;
	public final Map<String, AstClassFunction> methods;
	public final Map<String, AstClassFunction> getters;
	public final Map<String, AstClassFunction> setters;

	public AstClass(String name) {
		this.name = name;
		this.parent = null;
		this.constructor = null;
		this.methods = new HashMap<>();
		this.getters = new HashMap<>();
		this.setters = new HashMap<>();
	}

	@Override
	public String getPrototypeName() {
		return name;
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
		scope.add(getPrototypeName(), this);
	}

	public Instance createClass(Scope scope) {
		var classScope = scope.push();
		Instance inst = new Instance(this, classScope);
		classScope.owner = inst;

		for (var e : methods.entrySet()) {
			classScope.declareMember(e.getKey(), e.getValue(), AssignType.IMMUTABLE);
		}

		var p0 = parent;

		while (p0 != null) {
			var p1 = p0.eval(scope);

			if (!(p1 instanceof PrototypeSupplier)) {
				throw new ScriptError("Cannot extend " + p0);
			}

			if (p1 instanceof AstClass c) {
				for (var e : c.methods.entrySet()) {
					if (classScope.hasMember(e.getKey()) == AssignType.NONE) {
						classScope.declareMember(e.getKey(), e.getValue(), AssignType.IMMUTABLE);
					}
				}

				p0 = c.parent;
			} else {
				throw new ScriptError("Cannot extend " + p0 + " - currently not supported");
			}
		}

		return inst;
	}

	@Override
	public Object call(Scope scope, Object self, Evaluable[] args) {
		return construct(scope, args);
	}

	@Override
	public Object construct(Scope scope, Evaluable[] args) {
		var p = parent.eval(scope);

		if (parent != null && !(p instanceof PrototypeSupplier)) {
			throw new ScriptError("Cannot extend " + p);
		}

		/*
		var c = createClass(scope);

		if (constructor != null) {
			constructor.call(c.classScope, c, args);
		}
		 */

		var instance = new HashMap<>();

		instance.put("printTest", List.of(10, "Hi"));

		return instance;
	}

	public record Instance(AstClass ast, Scope classScope) implements Prototype {
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
