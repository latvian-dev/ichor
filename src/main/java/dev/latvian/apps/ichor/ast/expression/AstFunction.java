package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.FunctionInstance;

public class AstFunction extends AstExpression implements Comparable<AstFunction> {
	public interface Mod {
		int ARROW = 1 << 0;
		int CLASS = 1 << 1;
		int STATIC = 1 << 2;
		int SET = 1 << 3;
		int GET = 1 << 4;
		int CONSTRUCTOR = 1 << 5;
		int ASYNC = 1 << 6;
		int VARARGS = 1 << 7;
		int STATEMENT = 1 << 8;
		int GENERATOR = 1 << 9;
	}

	public final AstParam[] params;
	public final Interpretable body;
	public final int modifiers;
	public final int requiredParams;
	public String functionName;

	public AstFunction(AstParam[] params, Interpretable body, int modifiers) {
		this.params = params;
		this.body = body;
		this.modifiers = modifiers;

		int requiredParams0 = params.length;

		for (int i = params.length - 1; i >= 0; i--) {
			if (params[i].defaultValue != Special.UNDEFINED) {
				requiredParams0--;
			} else {
				break;
			}
		}

		this.requiredParams = requiredParams0;
	}

	public boolean hasMod(int mod) {
		return (modifiers & mod) != 0;
	}

	@Override
	public void append(AstStringBuilder builder) {
		if (hasMod(Mod.ASYNC)) {
			builder.append("async ");
		}

		if (!hasMod(Mod.ARROW)) {
			builder.append("function");

			if (functionName != null) {
				builder.append(' ');
				builder.append(functionName);
			}
		}

		builder.append('(');

		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			if (i == params.length - 1 && hasMod(Mod.VARARGS)) {
				builder.append("...");
			}

			params[i].append(builder);
		}

		builder.append(")");

		if (hasMod(Mod.ARROW)) {
			builder.append("=>");
		}

		builder.append(body);
	}

	@Override
	public FunctionInstance eval(Scope scope) {
		return new FunctionInstance(this, scope);
	}

	@Override
	public int compareTo(AstFunction o) {
		return Integer.compare(o.params.length, params.length);
	}

	@Override
	public AstFunction optimize(Parser parser) {
		body.optimize(parser);
		return this;
	}
}
