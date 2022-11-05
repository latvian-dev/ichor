package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.ScopeExit;

public class AstIf extends AstStatement {
	public final Evaluable condition;
	public final Interpretable trueBody;
	public final Interpretable falseBody;

	public AstIf(Evaluable condition, Interpretable ifTrue, Interpretable ifFalse) {
		this.condition = condition;
		this.trueBody = ifTrue;
		this.falseBody = ifFalse;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("if (");
		builder.append(condition);
		builder.append(") ");
		builder.append(trueBody);

		if (falseBody != null) {
			builder.append(" else ");
			builder.append(falseBody);
		}
	}

	@Override
	public void interpret(Scope scope) throws ScopeExit {
		if (condition.evalBoolean(scope)) {
			trueBody.interpret(scope);
		} else if (falseBody != null) {
			falseBody.interpret(scope);
		}
	}
}
