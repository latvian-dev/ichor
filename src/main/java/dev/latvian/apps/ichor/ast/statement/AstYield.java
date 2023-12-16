package dev.latvian.apps.ichor.ast.statement;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.WIPFeatureError;

public class AstYield extends AstStatement {
	public final boolean generator;
	public Object value;

	public AstYield(boolean generator, Object value) {
		this.generator = generator;
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("yield ");

		if (generator) {
			builder.append('*');
		}

		builder.appendValue(value);
	}

	@Override
	public void interpret(Context cx, Scope scope) {
		throw new WIPFeatureError();
	}

	@Override
	public void optimize(Parser parser) {
		value = parser.optimize(value);
	}
}
