package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;

public class AstSwitch extends AstStatement {
	public record AstCase(Evaluable value, Interpretable body) {
		public static final AstCase[] EMPTY = new AstCase[0];
	}

	public final Evaluable expression;
	public final AstCase[] cases;
	public final AstCase defaultCase;

	public AstSwitch(Evaluable expression, AstCase[] cases, AstCase defaultCase) {
		this.expression = expression;
		this.cases = cases;
		this.defaultCase = defaultCase;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("switch (");
		builder.append(expression);
		builder.append(") {");

		for (AstCase c : cases) {
			builder.append("case ");
			builder.append(c.value);
			builder.append(':');
			builder.append(c.body);
		}

		if (defaultCase != null) {
			builder.append("default:");
			builder.append(defaultCase.body);
		}

		builder.append('}');
	}

	@Override
	public void interpret(Scope scope) {
		var value = expression.eval(scope);

		for (AstCase c : cases) {
			if (c.value.equals(value, scope, true)) {
				try {
					c.body.interpretSafe(scope);
				} catch (BreakExit ignored) {
					return;
				}
			}
		}

		if (defaultCase != null) {
			try {
				defaultCase.body.interpretSafe(scope);
			} catch (BreakExit ignored) {
			}
		}
	}
}
