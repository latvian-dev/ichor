package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.token.KeywordToken;
import dev.latvian.apps.ichor.token.StaticToken;
import dev.latvian.apps.ichor.util.AssignType;

public class AstSingleDeclareStatement extends AstStatement {
	public final StaticToken assignToken;
	public final AstParam variable;

	public AstSingleDeclareStatement(StaticToken assignToken, AstParam variable) {
		this.assignToken = assignToken;
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
	}

	@Override
	public void interpret(Scope scope) {
		scope.declareParam(variable, assignToken == KeywordToken.CONST ? AssignType.IMMUTABLE : AssignType.MUTABLE);
	}
}
