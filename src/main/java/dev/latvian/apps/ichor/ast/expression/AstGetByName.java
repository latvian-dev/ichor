package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

import java.util.regex.Pattern;

public class AstGetByName extends AstGetFrom {
	private static final Pattern PLAIN_PATTERN = Pattern.compile("^[a-zA-Z_$][\\w$]*$");

	public final String name;

	public AstGetByName(Evaluable from, String name) {
		super(from);
		this.name = name;
	}

	@Override
	public Object evalKey(Scope scope) {
		return name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.appendValue(from);

		if (PLAIN_PATTERN.matcher(name).find()) {
			builder.append('.');
			builder.append(name);
		} else {
			builder.append("['");
			builder.append(name);
			builder.append("']");
		}
	}

	@Override
	public Object eval(Scope scope) {
		var cx = scope.getContext();
		var self = scope.eval(from);
		var p = cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(scope, self);
		}

		var r = p.get(scope, self, name);

		if (r == Special.NOT_FOUND) {
			throw new ScriptError("Cannot find " + this + " of " + p);
		}

		if (cx.debugger != null) {
			cx.debugger.get(scope, this, r);
		}

		return r;
	}

	@Override
	public void set(Scope scope, Object value) {
		var cx = scope.getContext();
		var self = scope.eval(from);
		var p = cx.getPrototype(self);

		if (cx.debugger != null) {
			cx.debugger.pushSelf(scope, self);
		}

		p.set(scope, self, name, value);

		if (cx.debugger != null) {
			cx.debugger.set(scope, this, value);
		}
	}

	@Override
	public Evaluable createCall(Object[] arguments, boolean isNew) {
		if (arguments.length == 0 && name.equals("toString")) {
			return new ToStringEvaluable(this);
		}

		return super.createCall(arguments, isNew);
	}

	private record ToStringEvaluable(Object obj) implements Evaluable {
		@Override
		public Object eval(Scope scope) {
			return scope.getContext().toString(scope, obj);
		}
	}
}