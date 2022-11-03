package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Evaluable;
import dev.latvian.apps.ichor.prototype.Interpretable;

public class AstWhile extends AstStatement {
	public final Evaluable condition;
	public final Interpretable body;

	public AstWhile(Evaluable condition, Interpretable body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("while (");
		builder.append(condition);
		builder.append(") ");
		builder.append(body);
	}

	@Override
	public void interpret(Scope scope) {
		while (condition.evalBoolean(scope)) {
			try {
				body.interpretInNewScope(scope);
			} catch (AstBreak.BreakException ex) {
				break;
			} catch (AstContinue.ContinueException ignored) {
			}
		}
	}
}
