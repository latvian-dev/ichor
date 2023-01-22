package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.exit.BreakExit;
import dev.latvian.apps.ichor.exit.ExitType;

// TODO: Create optimized version for constant string, number and enum cases
public class AstSwitch extends AstStatement implements LabeledStatement {
	public static final class AstCase {
		public static final AstCase[] EMPTY = new AstCase[0];

		public Object value;
		public Interpretable body;

		public AstCase(Object value, Interpretable body) {
			this.value = value;
			this.body = body;
		}
	}

	public Object expression;
	public AstCase[] cases;
	public AstCase defaultCase;

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("switch (");
		builder.appendValue(expression);
		builder.append(") {");

		for (AstCase c : cases) {
			builder.append("case ");
			builder.appendValue(c.value);
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
	public void interpret(Context cx, Scope scope) {
		for (AstCase c : cases) {
			if (cx.equals(scope, expression, c.value, true)) {
				try {
					c.body.interpretSafe(cx, scope);
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
				defaultCase.body.interpretSafe(cx, scope);
			} catch (BreakExit ignored) {
			}
		}
	}

	@Override
	public void optimize(Parser parser) {
		expression = parser.optimize(expression);

		for (var c : cases) {
			c.value = parser.optimize(c.value);
			c.body.optimize(parser);
		}

		if (defaultCase != null) {
			defaultCase.value = parser.optimize(defaultCase.value);
			defaultCase.body.optimize(parser);
		}
	}
}
