package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstTemplateLiteral extends AstExpression {
	public final Object[] parts;

	public AstTemplateLiteral(Object[] parts) {
		this.parts = parts;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('`');

		for (var part : parts) {
			if (part instanceof CharSequence token) {
				builder.append(token.toString());
			} else {
				builder.append("${");
				builder.append(part);
				builder.append('}');
			}
		}

		builder.append('`');
	}

	@Override
	public String eval(Context cx, Scope scope) {
		var builder = new StringBuilder();
		evalString(cx, scope, builder);
		return builder.toString();
	}

	@Override
	public void evalString(Context cx, Scope scope, StringBuilder builder) {
		for (var part : parts) {
			if (part instanceof Evaluable eval) {
				eval.evalString(cx, scope, builder);
			} else {
				builder.append(part);
			}
		}
	}

	@Override
	public double evalDouble(Context cx, Scope scope) {
		try {
			return Double.parseDouble(eval(cx, scope));
		} catch (NumberFormatException ex) {
			return Double.NaN;
		}
	}

	@Override
	public int evalInt(Context cx, Scope scope) {
		try {
			return (int) Double.parseDouble(eval(cx, scope));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	@Override
	public boolean evalBoolean(Context cx, Scope scope) {
		return !eval(cx, scope).isEmpty();
	}

	@Override
	public Object optimize(Parser parser) {
		if (parts.length == 0) {
			return "";
		}

		boolean onlyStrings = true;

		for (int i = 0; i < parts.length; i++) {
			parts[i] = parser.optimize(parts[i]);

			if (!(parts[i] instanceof CharSequence)) {
				onlyStrings = false;
			}
		}

		if (onlyStrings) {
			var sb = new StringBuilder();

			for (var part : parts) {
				sb.append(part);
			}

			return sb.toString();
		}

		return this;
	}
}
