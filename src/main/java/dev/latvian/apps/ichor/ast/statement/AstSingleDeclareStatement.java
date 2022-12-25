package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.util.AssignType;

public class AstSingleDeclareStatement extends AstDeclareStatement {
	public final AstParam variable;

	public AstSingleDeclareStatement(DeclaringToken assignToken, AstParam variable) {
		super(assignToken);
		this.variable = variable;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append(assignToken);
		builder.append(' ');

		builder.append(variable.name);

		if (variable.defaultValue != Special.UNDEFINED) {
			builder.append('=');
			builder.append(variable.defaultValue);
		}

		builder.append(';');
	}

	@Override
	public void interpret(Scope scope) {
		scope.declareParam(variable, assignToken.isConst() ? AssignType.IMMUTABLE : AssignType.MUTABLE);
	}
}
