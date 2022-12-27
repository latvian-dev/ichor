package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.token.DeclaringToken;
import dev.latvian.apps.ichor.util.AssignType;

public class AstMultiDeclareStatement extends AstDeclareStatement {
	public final AstParam[] variables;

	public AstMultiDeclareStatement(DeclaringToken assignToken, AstParam[] variables) {
		super(assignToken);
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

			builder.append(variables[i]);
		}

		builder.append(';');
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		for (AstParam v : variables) {
			v.declare(scope, cx.eval(scope, v.defaultValue), assignToken.isConst() ? AssignType.IMMUTABLE : AssignType.MUTABLE);
		}
	}

	@Override
	public void optimize(Parser parser) {
		for (AstParam v : variables) {
			v.defaultValue = parser.optimize(v.defaultValue);
		}
	}
}
