package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.IndexedMemberHolder;
import dev.latvian.apps.ichor.NamedMemberHolder;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstGetByEvaluable extends AstGetFrom {
	public final Evaluable key;

	public AstGetByEvaluable(AstExpression from, Evaluable key) {
		super(from);
		this.key = key;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(from);
		builder.append('[');
		builder.append(key);
		builder.append(']');
	}

	@Override
	public Object eval(Scope scope) {
		var o = from.eval(scope);

		if (o instanceof IndexedMemberHolder holder) {
			return holder.getMember(scope, key.evalInt(scope));
		} else if (o instanceof NamedMemberHolder holder) {
			return holder.getMember(scope, key.evalString(scope));
		} else {
			throw new ScriptError(from + " is not a named member holder");
		}
	}

	@Override
	public void set(Scope scope, Object value) {
		var o = from.eval(scope);

		if (o instanceof IndexedMemberHolder holder) {
			holder.setMember(scope, key.evalInt(scope), value);
		} else if (o instanceof NamedMemberHolder holder) {
			holder.setMember(scope, key.evalString(scope), value);
		} else {
			throw new ScriptError(from + " is not a named member holder");
		}
	}
}
