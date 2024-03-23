package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstAdd extends AstBinary {
	public static class InvalidAdditionError extends ScriptError {
		public final Object left, right;

		public InvalidAdditionError(Object left, Object right) {
			super("Can't add " + left + " + " + right);
			this.left = left;
			this.right = right;
		}
	}

	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('+');
	}

	@Override
	public Object eval(Scope scope) {
		var l = scope.eval(left);
		var r = scope.eval(right);

		if (l instanceof CharSequence || l instanceof Character || r instanceof CharSequence || r instanceof Character) {
			var sb = new StringBuilder();
			scope.asString(l, sb, false);
			scope.asString(r, sb, false);
			return sb.toString();
		} else if (l instanceof Number && r instanceof Number) {
			return ((Number) l).doubleValue() + ((Number) r).doubleValue();
		} else {
			throw new InvalidAdditionError(l, r).pos(pos);
		}
	}

	@Override
	public double evalDouble(Scope scope) {
		return scope.asDouble(left) + scope.asDouble(right);
	}

	@Override
	public Object optimize(Parser parser) {
		super.optimize(parser);

		if (left instanceof Number l && right instanceof Number r) {
			return l.doubleValue() + r.doubleValue();
		} else if (left instanceof CharSequence l && right instanceof CharSequence r) {
			return l.toString() + r;
		}

		return this;
	}
}
