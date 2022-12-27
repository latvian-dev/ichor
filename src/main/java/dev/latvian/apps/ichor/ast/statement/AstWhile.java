package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ContinueExit;

public class AstWhile extends AstLabeledStatement {
	public Object condition;
	public Interpretable body;

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("while (");
		builder.appendValue(condition);
		builder.append(')');

		if (body != null) {
			builder.append(body);
		} else {
			builder.append(';');
		}
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		while (cx.asBoolean(scope, condition)) {
			if (body != null) {
				try {
					body.interpretSafe(cx, scope);
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
	}

	@Override
	public void optimize(Parser parser) {
		condition = parser.optimize(condition);
		body.optimize(parser);
	}
}
