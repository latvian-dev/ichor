package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;

import java.util.concurrent.Future;

public class AstAwait extends AstExpression {
	public Object future;

	public AstAwait(Object future) {
		this.future = future;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("await ");
		builder.append(future);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		var e = cx.eval(scope, future);

		if (Special.isInvalid(e)) {
			return e;
		} else if (e instanceof Future<?> f) {
			try {
				return f.get();
			} catch (Exception ex) {
				throw new FutureResolveError(f, ex).pos(this);
			}
		}

		throw new NotFutureError(e).pos(this);
	}

	@Override
	public Object optimize(Parser parser) {
		future = parser.optimize(future);
		return this;
	}

	public static class NotFutureError extends ScriptError {

		public final Object value;

		public NotFutureError(Object value) {
			super(value + " is not a Promise/Future");
			this.value = value;
		}
	}

	public static class FutureResolveError extends ScriptError {
		public final Future<?> future;

		public FutureResolveError(Future<?> future, Throwable ex) {
			super(ex);
			this.future = future;
		}
	}
}
