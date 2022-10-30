package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.NamedMemberHolder;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGetByName extends AstGetFrom {
	public final String name;

	public AstGetByName(Evaluable from, String name) {
		super(from);
		this.name = name;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(from);
		builder.append('.');
		builder.append(name);
	}

	@Override
	public Object eval(Scope scope) {
		var o = from.eval(scope);

		if (o instanceof NamedMemberHolder holder) {
			return holder.getMember(scope, name);
		} else {
			throw new ScriptError(from + " is not a named member holder");
		}
	}

	@Override
	public void set(Scope scope, Object value) {
		var o = from.eval(scope);

		if (o instanceof NamedMemberHolder holder) {
			holder.setMember(scope, name, value);
		} else {
			throw new ScriptError(from + " is not a named member holder");
		}
	}
}
