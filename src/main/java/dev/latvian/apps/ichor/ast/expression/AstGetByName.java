package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.NamedMemberNotFoundError;

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
	public Object eval(Context cx, Scope scope) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);
		var r = self == p ? p.getStatic(cx, scope, name) : p.getLocal(cx, scope, p.cast(self), name);

		if (r == Special.NOT_FOUND) {
			throw new NamedMemberNotFoundError(name, self).pos(this);
		}

		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		if (!(self == p ? p.setStatic(cx, scope, name, value) : p.setLocal(cx, scope, p.cast(self), name, value))) {
			throw new NamedMemberNotFoundError(name, self).pos(this);
		}
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		var self = evalSelf(cx, scope);
		var p = cx.getPrototype(scope, self);

		if (self == p) {
			throw new NamedMemberNotFoundError(name, self).pos(this);
		}

		return p.deleteLocal(cx, scope, p.cast(self), name);
	}
}
