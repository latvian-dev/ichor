package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;

public class AstFor extends AstLabeledStatement {
	public Interpretable initializer;
	public Object condition;
	public Interpretable increment;
	public Interpretable body;

	@Override
	public void append(AstStringBuilder builder) {
		if (!label.isEmpty()) {
			builder.append(label);
			builder.append(':');
		}

		builder.append("for(");

		if (!(initializer instanceof AstEmptyBlock)) {
			builder.append(initializer);
		}

		if (!(initializer instanceof AstDeclareStatement)) {
			builder.append(';');
		}

		if (condition != null) {
			builder.appendValue(condition);
		}

		builder.append(';');

		if (!(increment instanceof AstEmptyBlock)) {
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
	public void interpret(Context cx, Scope scope) {
		var s = scope.push();

		for (initializer.interpretSafe(cx, scope); condition == null || cx.asBoolean(scope, condition); increment.interpretSafe(cx, scope)) {
			try {
				if (body != null) {
					body.interpretSafe(cx, s);
				}
			} catch (BreakExit exit) {
				if (exit.stop == this) {
					break;
				} else {
					throw exit;
				}
			} catch (ContinueExit exit) {
				if (exit.stop != this) {
					throw exit;
				}
			}
		}
	}

	@Override
	public void optimize(Parser parser) {
		initializer.optimize(parser);
		condition = parser.optimize(condition);
		increment.optimize(parser);
		body.optimize(parser);
	}
}
