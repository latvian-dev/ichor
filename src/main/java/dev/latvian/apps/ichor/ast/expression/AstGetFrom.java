package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public abstract class AstGetFrom extends AstGetBase {
	public final Evaluable from;

	public AstGetFrom(Evaluable from) {
		this.from = from;
	}

	public abstract Object evalKey(Scope scope);

	@Override
	public Evaluable createCall(Evaluable[] arguments, boolean isNew) {
		return new AstFromCall(this, arguments, isNew);
	}

	public static class AstFromCall extends AstCallBase {
		public final AstGetFrom callee;

		public AstFromCall(AstGetFrom callee, Evaluable[] arguments, boolean isNew) {
			super(arguments, isNew);
			this.callee = callee;
		}

		@Override
		public void append(AstStringBuilder builder) {
			callee.append(builder);
			super.append(builder);
		}

		@Override
		public String calleeName() {
			return callee.toString();
		}

		@Override
		public Object evalFunc(Scope scope) {
			return callee.eval(scope);
		}

		@Override
		public Object evalSelf(Scope scope) {
			return callee.from.eval(scope);
		}
	}
}
