package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.NamedMemberNotFoundError;
import dev.latvian.apps.ichor.prototype.Prototype;

import java.util.regex.Pattern;

public class AstGetByName extends AstGetFrom {
	private static final Pattern PLAIN_PATTERN = Pattern.compile("^[a-zA-Z_$][\\w$]*$");

	public final String name;

	public AstGetByName(Object from, String name) {
		super(from);
		this.name = name;
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
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);
		var r = p.getInternal(scope, self, name);

		if (r == Special.NOT_FOUND) {
			throw new NamedMemberNotFoundError(name, p, self).pos(this);
		}

		return r;
	}

	@Override
	public void set(Scope scope, Object value) {
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		if (!(self == p ? p.setStatic(scope, name, value) : p.setLocal(scope, p.cast(self), name, value))) {
			throw new NamedMemberNotFoundError(name, p, self).pos(this);
		}
	}

	@Override
	public boolean delete(Scope scope) {
		var self = evalSelf(scope);
		var p = scope.getPrototype(self);

		if (self == p) {
			throw new NamedMemberNotFoundError(name, p, self).pos(this);
		}

		return p.deleteLocal(scope, p.cast(self), name);
	}

	@Override
	public Object optimize(Parser parser) {
		from = parser.optimize(from);

		// Is this correct? So far seems to work
		if (from instanceof Prototype<?> p) {
			var m = p.getStatic(parser.getRootScope(), name);

			if (m == Special.NOT_FOUND) {
				throw new NamedMemberNotFoundError(name, p, p).pos(this);
			}

			// Disabled for functions for now, potential issues with static java member scoping
			if (!(m instanceof Callable)) {
				return m;
			}
		}

		return this;
	}
}
