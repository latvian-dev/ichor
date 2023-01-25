package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.NamedMemberNotFoundError;
import dev.latvian.apps.ichor.js.ast.AstPrototype;

import java.util.regex.Pattern;

public class AstGetByName extends AstGetFrom {
	private static final Pattern PLAIN_PATTERN = Pattern.compile("^[a-zA-Z_$][\\w$]*$");

	public final String name;

	public AstGetByName(Object from, String name) {
		super(from);
		this.name = name;
	}

	@Override
	public Object evalKey(Context cx, Scope scope) {
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
	public Object eval(Context cx, Scope scope) {
		var r = cx.wrap(scope, evalSelf(cx, scope)).get(cx, scope, name);

		if (r == Special.NOT_FOUND) {
			throw new NamedMemberNotFoundError(name).pos(this);
		}

		return r;
	}

	@Override
	public void set(Context cx, Scope scope, Object value) {
		if (!cx.wrap(scope, evalSelf(cx, scope)).set(cx, scope, name, value)) {
			throw new NamedMemberNotFoundError(name).pos(this);
		}
	}

	@Override
	public boolean delete(Context cx, Scope scope) {
		return cx.wrap(scope, evalSelf(cx, scope)).delete(cx, scope, name);
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);

		if (name.equals("__prototype__")) {
			return new AstPrototype(from);
		}

		return this;
	}
}
