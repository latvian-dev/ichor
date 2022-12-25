package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ExitType;

// TODO: Create optimized version for constant string, number and enum cases
public class AstSwitch extends AstStatement implements LabeledStatement {
	public record AstCase(Evaluable value, Interpretable body) {
		public static final AstCase[] EMPTY = new AstCase[0];
	}

	public Evaluable expression;
	public AstCase[] cases;
	public AstCase defaultCase;

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
	public boolean handle(ExitType type) {
		return type == ExitType.BREAK;
	}

	@Override
	public void interpret(Scope scope) {
		for (AstCase c : cases) {
			if (c.value.equals(scope, expression, true)) {
				try {
					c.body.interpretSafe(scope);
				} catch (BreakExit exit) {
					if (exit.stop == this) {
						return;
					} else {
						throw exit;
					}
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
