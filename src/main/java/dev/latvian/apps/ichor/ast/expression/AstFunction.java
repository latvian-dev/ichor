package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Parser;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.util.FunctionInstance;

public class AstFunction extends AstExpression implements Comparable<AstFunction> {
	public static final int MOD_ARROW = 1;
	public static final int MOD_CLASS = 2;
	public static final int MOD_STATIC = 4;
	public static final int MOD_SET = 8;
	public static final int MOD_GET = 16;
	public static final int MOD_CONSTRUCTOR = 32;
	public static final int MOD_ASYNC = 64;
	public static final int MOD_VARARGS = 128;

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
		if (hasMod(MOD_ASYNC)) {
			builder.append("async ");
		}

		if (!hasMod(MOD_ARROW)) {
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

			if (i == params.length - 1 && hasMod(MOD_VARARGS)) {
				builder.append("...");
			}

			params[i].append(builder);
		}

		builder.append(")");

		if (hasMod(MOD_ARROW)) {
			builder.append("=>");
		}

		builder.append(body);
	}

	@Override
	public Object eval(Context cx, Scope scope) {
		return new FunctionInstance(this, cx, scope);
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
