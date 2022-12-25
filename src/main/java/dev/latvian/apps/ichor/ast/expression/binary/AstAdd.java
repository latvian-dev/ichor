package dev.latvian.apps.ichor.ast.expression.binary;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.token.DoubleToken;
import dev.latvian.apps.ichor.token.StringToken;

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
			return scope.getContext().asString(scope, l) + scope.getContext().asString(scope, r);
		} else if (l instanceof Number && r instanceof Number) {
			return ((Number) l).doubleValue() + ((Number) r).doubleValue();
		} else {
			throw new ScriptError("Can't add " + left + " + " + right).pos(pos);
		}
	}

	@Override
	public void evalString(Scope scope, StringBuilder builder) {
		var l = left.eval(scope);
		var r = right.eval(scope);

		if (l instanceof CharSequence || l instanceof Character || r instanceof CharSequence || r instanceof Character) {
			scope.getContext().asString(scope, l, builder);
			scope.getContext().asString(scope, r, builder);
		} else if (l instanceof Number && r instanceof Number) {
			AstStringBuilder.wrapNumber(((Number) l).doubleValue() + ((Number) r).doubleValue(), builder);
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
	public Evaluable optimize(Parser parser) {
		var s = super.optimize(parser);

		if (s == this && left instanceof DoubleToken l && right instanceof DoubleToken r) {
			return DoubleToken.of(l.value + r.value);
		} else if (s == this && left instanceof StringToken l && right instanceof StringToken r) {
			return StringToken.of(l.value + r.value);
		}

		return s;
	}
}
