package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;

public class AstTemplateLiteral extends AstStringBase {
	public final Evaluable[] parts;

	public AstTemplateLiteral(Evaluable[] parts) {
		this.parts = parts;
	}

	@Override
	public String evalString(Scope scope) {
		var sb = new StringBuilder();

		for (Evaluable part : parts) {
			sb.append(part.evalString(scope));
		}

		return sb.toString();
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('`');

		for (var part : parts) {
			if (part instanceof AstString token) {
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
