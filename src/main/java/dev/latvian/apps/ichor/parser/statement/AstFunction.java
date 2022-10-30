package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Interpreter;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;
import dev.latvian.apps.ichor.util.AssignType;

public class AstFunction extends AstStatement {
	public final String name;
	public final String[] params;
	public final Interpretable body;

	public AstFunction(String name, String[] params, Interpretable body) {
		this.name = name;
		this.params = params;
		this.body = body;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append("function ");
		builder.append(name);
		builder.append('(');

		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				builder.append(", ");
			}

			builder.append(params[i]);
		}

		builder.append(") ");
		builder.append(body);
	}

	@Override
	public void interpret(Interpreter interpreter) {
		interpreter.scope.declareMember(name, new FuncCallback(interpreter, this), AssignType.IMMUTABLE);
	}

	private record FuncCallback(Interpreter interpreter, AstFunction function) implements Callable {
		@Override
		public Object call(Scope scope, Object[] args) {
			if (args.length != function.params.length) {
				throw new ScriptError("Invalid number of arguments");
			}

			interpreter.pushScope();

			for (int i = 0; i < function.params.length; i++) {
				scope.declareMember(function.params[i], args[i], AssignType.MUTABLE);
			}

			function.body.interpret(interpreter);
			interpreter.popScope();
			return Special.UNDEFINED;
		}
	}
}
