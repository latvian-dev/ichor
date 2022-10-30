package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;

public class AstSeq extends AstBinaryBoolean {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append("===");
	}

	public static boolean shallowEquals(Object l, Object r) {
		if (l == r) {
			return true;
		} else if (Special.isInvalid(l)) {
			return Special.isInvalid(r);
		} else if (l instanceof Number && r instanceof Number || l instanceof Boolean && r instanceof Boolean) {
			return l.equals(r);
		} else if (l instanceof CharSequence && r instanceof CharSequence) {
			return l.toString().equals(r.toString());
		}

		return l == r;
	}

	@Override
	public boolean evalBoolean(Scope scope) {
		return shallowEquals(evalL(scope), evalR(scope));
	}
}
