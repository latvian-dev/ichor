package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

import java.util.concurrent.Future;

public class AstAwait extends AstExpression {
	public final Evaluable future;

	public AstAwait(Evaluable future) {
		this.future = future;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("await ");
		builder.append(future);
	}

	@Override
	public Object eval(Scope scope) {
		var e = future.eval(scope);

		if (Special.isInvalid(e)) {
			return e;
		} else if (e instanceof Future<?> f) {
			try {
				return f.get();
			} catch (Exception ex) {
				throw new ScriptError(ex).pos(this);
			}
		}

		throw new ScriptError(e + " is not a Promise/Future").pos(this);
	}
}
