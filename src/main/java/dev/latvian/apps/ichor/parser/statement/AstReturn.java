package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpreter;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

public class AstReturn extends AstStatement {
	public final Object value;

	public AstReturn(Object value) {
		this.value = value;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("return ");
		builder.append(value);
	}

	@Override
	public void interpret(Interpreter interpreter) {
		if (value instanceof Evaluable e) {
			interpreter.returnValue = e.eval(interpreter.scope);
		} else {
			interpreter.returnValue = value;
		}
	}
}
