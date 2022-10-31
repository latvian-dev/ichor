package dev.latvian.apps.ichor.parser.statement;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.AssignType;
import org.jetbrains.annotations.Nullable;

public class AstFunction extends AstStatement implements Prototype {
	public final String name;
	public final String[] params;
	public final Interpretable body;

	public AstFunction(String name, String[] params, Interpretable body) {
		this.name = name;
		this.params = params;
		this.body = body;
	}

	@Override
	public String getPrototypeName() {
		return name;
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
	public void interpret(Scope scope) {
		scope.declareMember(name, this, AssignType.IMMUTABLE);
	}

	@Override
	public Object call(Scope scope, Object[] args, @Nullable Object self) {
		if (args.length < params.length) {
			throw new ScriptError("Invalid number of arguments");
		}

		if (scope.root.context.debug) {
			var sb = new StringBuilder("Calling ");
			sb.append(name);
			sb.append('(');

			for (int i = 0; i < args.length; i++) {
				if (i > 0) {
					sb.append(',');
				}

				sb.append(args[i]);
			}

			sb.append(')');
			System.out.println(sb);
		}

		var s = scope.push();

		try {
			for (int i = 0; i < params.length; i++) {
				s.declareMember(params[i], args[i], AssignType.MUTABLE);

				if (scope.root.context.debug) {
					System.out.println("Declared " + params[i] + " = " + args[i]);
				}
			}

			body.interpret(s);
		} catch (AstReturn.ReturnException ex) {
			return ex.value;
		} finally {
			scope.pop();
		}

		return Special.UNDEFINED;
	}

	@Override
	public Object eval(Scope scope) {
		return this;
	}
}
