package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;
import org.jetbrains.annotations.Nullable;

public class AstFor extends AstStatement {
	public final Interpretable initializer;
	public final Evaluable condition;
	public final Interpretable increment;
	public final Interpretable body;

	public AstFor(@Nullable Interpretable initializer, @Nullable Evaluable condition, @Nullable Interpretable increment, @Nullable Interpretable body) {
		this.initializer = initializer;
		this.condition = condition;
		this.increment = increment;
		this.body = body;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("for(");

		if (initializer != null) {
			builder.append(initializer);
		}

		builder.append(';');

		if (condition != null) {
			builder.append(condition);
		}

		builder.append(';');

		if (increment != null) {
			builder.append(increment);
		}

		builder.append(')');

		if (body != null) {
			builder.append(body);
		} else {
			builder.append(';');
		}
	}

	@Override
	public void interpret(Scope scope) {
		var s = scope.push();

		if (initializer != null) {
			initializer.interpret(s);
		}

		while (condition == null || condition.evalBoolean(s)) {
			try {
				if (body != null) {
					body.interpret(s);
				}

				if (increment != null) {
					increment.interpret(s);
				}
			} catch (BreakExit exit) {
				break;
			} catch (ContinueExit ignored) {
			}
		}
	}
}
