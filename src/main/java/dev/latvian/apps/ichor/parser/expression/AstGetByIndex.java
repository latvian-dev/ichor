package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.IndexedMemberHolder;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGetByIndex extends AstGetFrom {
	public final int index;

	public AstGetByIndex(AstExpression from, int index) {
		super(from);
		this.index = index;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(from);
		builder.append('[');
		builder.append(index);
		builder.append(']');
	}

	@Override
	public Object eval(Scope scope) {
		var o = from.eval(scope);

		if (o instanceof IndexedMemberHolder holder) {
			return holder.getMember(scope, index);
		} else {
			throw new ScriptError(from + " is not an indexed member holder");
		}
	}

	@Override
	public void set(Scope scope, Object value) {
		var o = from.eval(scope);

		if (o instanceof IndexedMemberHolder holder) {
			holder.setMember(scope, index, value);
		} else {
			throw new ScriptError(from + " is not an indexed member holder");
		}
	}
}
