package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

import java.util.regex.Pattern;

public class AstGetByName extends AstGetFrom {
	private static final Pattern PLAIN_PATTERN = Pattern.compile("^[a-zA-Z_$][\\w$]*$");

	public final String name;

	public AstGetByName(Evaluable from, String name) {
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
		var o = from.eval(scope);
		var p = scope.getContext().getPrototype(o);
		return p.get(scope, name, o);
	}

	@Override
	public void set(Scope scope, Object value) {
		var o = from.eval(scope);
		var p = scope.getContext().getPrototype(o);
		p.set(scope, name, o, value);
	}
}
