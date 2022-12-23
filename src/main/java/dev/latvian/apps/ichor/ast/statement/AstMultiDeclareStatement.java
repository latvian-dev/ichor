package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.util.AssignType;

public class AstMultiDeclareStatement extends AstStatement {
	public final DeclaringToken assignToken;
	public final AstParam[] variables;

	public AstMultiDeclareStatement(DeclaringToken assignToken, AstParam[] variables) {
		this.assignToken = assignToken;
		this.variables = variables;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(assignToken);
		builder.append(' ');

		for (int i = 0; i < variables.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			builder.append(variables[i].name);

			if (variables[i].defaultValue != Special.UNDEFINED) {
				builder.append('=');
				builder.append(variables[i].defaultValue);
			}
		}

		builder.append(';');
	}

	@Override
	public void interpret(Scope scope) {
		for (AstParam v : variables) {
			scope.declareParam(v, assignToken.isConst() ? AssignType.IMMUTABLE : AssignType.MUTABLE);
		}
	}
}
