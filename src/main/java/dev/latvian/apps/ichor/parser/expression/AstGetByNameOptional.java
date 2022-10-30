package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.NamedMemberHolder;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGetByNameOptional extends AstGetByName {
	public AstGetByNameOptional(Evaluable from, String name) {
		super(from, name);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(from);
		builder.append("?.");
		builder.append(name);
	}

	@Override
	public Object eval(Scope scope) {
		var o = from.eval(scope);

		if (o instanceof NamedMemberHolder holder) {
			return holder.hasMember(scope, name) ? holder.getMember(scope, name) : Special.UNDEFINED;
		} else {
			throw new ScriptError(from + " is not a named member holder");
		}
	}
}
