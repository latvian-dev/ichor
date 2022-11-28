package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;
import org.jetbrains.annotations.Nullable;

public class AstWhile extends AstStatement {
	public final Evaluable condition;
	public final Interpretable body;
	public final String label;

	public AstWhile(Evaluable condition, @Nullable Interpretable body, String label) {
		this.condition = condition;
		this.body = body;
		this.label = label;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("while (");
		builder.append(condition);
		builder.append(')');

		if (body != null) {
			builder.append(body);
		} else {
			builder.append(';');
		}
	}

	@Override
	public void interpret(Scope scope) {
		while (condition.evalBoolean(scope)) {
			if (body != null) {
				try {
					body.interpret(scope);
				} catch (BreakExit exit) {
					break;
				} catch (ContinueExit ignored) {
				}
			}
		}
	}
}
