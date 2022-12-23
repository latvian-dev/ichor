package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.EvaluableStringBase;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.token.StringToken;

public class AstTemplateLiteral extends AstExpression implements EvaluableStringBase {
	public final Evaluable[] parts;

	public AstTemplateLiteral(Evaluable[] parts) {
		this.parts = parts;
	}

	@Override
	public void evalString(Scope scope, StringBuilder builder) {
		for (Evaluable part : parts) {
			part.evalString(scope, builder);
		}
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('`');

		for (var part : parts) {
			if (part instanceof StringToken token) {
				builder.append(token.value);
			} else {
				builder.append("${");
				builder.append(part);
				builder.append('}');
			}
		}

		builder.append('`');
	}
}
