package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;
import dev.latvian.apps.ichor.util.ClassPrototype;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class AstClass extends AstStatement {
	public final String name;
	public Object parent;
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
			builder.appendValue(parent);
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
	public void interpret(Context cx, Scope scope) {
		scope.addImmutable(name, new ClassPrototype(this, scope));
	}

	@Override
	public void optimize(Parser parser) {
		super.optimize(parser);
		parent = parser.optimize(parent);

		if (constructor != null) {
			constructor.optimize(parser);
		}

		for (var entry : methods.entrySet()) {
			entry.getValue().optimize(parser);
		}

		for (var entry : getters.entrySet()) {
			entry.getValue().optimize(parser);
		}

		for (var entry : setters.entrySet()) {
			entry.getValue().optimize(parser);
		}
	}
}
