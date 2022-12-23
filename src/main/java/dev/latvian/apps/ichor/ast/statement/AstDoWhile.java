package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;
import org.jetbrains.annotations.Nullable;

public class AstDoWhile extends AstLabelledStatement {
	public final Evaluable condition;
	public final Interpretable body;

	public AstDoWhile(Evaluable condition, @Nullable Interpretable body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("do ");

		if (body != null) {
			builder.append(body);
		} else {
			builder.append(';');
		}

		builder.append("while (");
		builder.append(condition);
		builder.append(')');
	}

	@Override
	public void interpret(Scope scope) {
		do {
			if (body != null) {
				try {
					body.interpretSafe(scope);
				} catch (BreakExit exit) {
					break;
				} catch (ContinueExit ignored) {
				}
			}
		}
		while (condition.evalBoolean(scope));
	}
}
