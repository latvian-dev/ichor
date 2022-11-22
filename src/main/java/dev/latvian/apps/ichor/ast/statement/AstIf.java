package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import org.jetbrains.annotations.Nullable;

public class AstIf extends AstStatement {
	public final Evaluable condition;
	public final Interpretable trueBody;
	public final Interpretable falseBody;

	public AstIf(Evaluable condition, @Nullable Interpretable ifTrue, @Nullable Interpretable ifFalse) {
		this.condition = condition;
		this.trueBody = ifTrue;
		this.falseBody = ifFalse;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("if (");
		builder.append(condition);
		builder.append(") ");

		if (trueBody != null) {
			builder.append(trueBody);
		} else {
			builder.append(';');
		}

		if (falseBody != null) {
			builder.append(" else ");
			builder.append(falseBody);
		}
	}

	@Override
	public void interpret(Scope scope) {
		if (condition.evalBoolean(scope)) {
			if (trueBody != null) {
				trueBody.interpret(scope);
			}
		} else if (falseBody != null) {
			falseBody.interpret(scope);
		}
	}
}
