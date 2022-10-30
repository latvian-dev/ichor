package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Interpreter;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

import java.util.Collection;

public class AstBlock extends AstInterpretableGroup {

	public AstBlock(Collection<Interpretable> statements) {
		super(statements);
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('{');
		super.append(builder);
		builder.append('}');
	}

	@Override
	public void interpret(Interpreter interpreter) {
		interpreter.pushScope();
		super.interpret(interpreter);
		interpreter.popScope();
	}
}
