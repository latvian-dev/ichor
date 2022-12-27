package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.EvaluableStringBase;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstTemplateLiteral extends AstExpression implements EvaluableStringBase {
	public final Object[] parts;

	public AstTemplateLiteral(Object[] parts) {
		this.parts = parts;
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
