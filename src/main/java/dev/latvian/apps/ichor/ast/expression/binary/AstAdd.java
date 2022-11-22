package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.expression.AstString;
import dev.latvian.apps.ichor.error.ScriptError;

public class AstAdd extends AstBinary {
	@Override
	public void appendSymbol(StringBuilder builder) {
		builder.append('+');
	}

	@Override
	public Object eval(Scope scope) {
		var l = left.eval(scope);
		var r = right.eval(scope);

		if (l instanceof CharSequence || l instanceof Character || r instanceof CharSequence || r instanceof Character) {
			return String.valueOf(l) + r;
		} else if (l instanceof Number && r instanceof Number) {
			return ((Number) l).doubleValue() + ((Number) r).doubleValue();
		} else {
			throw new ScriptError("Can't add " + left + " + " + right).pos(pos);
		}
	}

	@Override
	public double evalDouble(Scope scope) {
		return left.evalDouble(scope) + right.evalDouble(scope);
	}

	@Override
	public int evalInt(Scope scope) {
		return left.evalInt(scope) + right.evalInt(scope);
	}

	@Override
	public Evaluable optimize() {
		var s = super.optimize();

		if (s == this && left instanceof AstString l && right instanceof AstString r) {
			return new AstString(l.value + r.value).pos(l.pos);
		}

		return s;
	}
}
