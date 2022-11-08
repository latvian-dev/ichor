package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.exit.ReturnExit;
import dev.latvian.apps.ichor.exit.ScopeExit;
import dev.latvian.apps.ichor.prototype.PrototypeFunction;
import dev.latvian.apps.ichor.util.AssignType;

public class AstFunction extends AstExpression implements PrototypeFunction, Comparable<AstFunction> {
	public static final int MOD_ARROW = 1;
	public static final int MOD_CLASS = 2;
	public static final int MOD_STATIC = 4;
	public static final int MOD_SET = 8;
	public static final int MOD_GET = 16;
	public static final int MOD_CONSTRUCTOR = 32;

	public final AstParam[] params;
	public final Interpretable body;
	public final int modifiers;
	public final int requiredParams;

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

	@Override
	public String getPrototypeName() {
		return "<function>";
	}

	public boolean hasMod(int mod) {
		return (modifiers & mod) != 0;
	}

	@Override
	public void append(AstStringBuilder builder) {
		builder.append('(');

		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				builder.append(", ");
			}

			params[i].append(builder);
		}

		builder.append(") => ");
		builder.append(body);
	}

	@Override
	public Object call(Scope scope, Object self, Object[] args) {
		if (args.length < requiredParams) {
			throw new ScriptError("Invalid number of arguments: " + args.length + " < " + requiredParams);
		}

		var s = scope.push(this);

		try {
			for (int i = 0; i < params.length; i++) {
				s.declareMember(params[i].name, i >= args.length ? params[i].defaultValue == Special.UNDEFINED ? Special.UNDEFINED : scope.eval(params[i].defaultValue) : scope.eval(args[i]), AssignType.MUTABLE);
			}

			body.interpret(s);
		} catch (ReturnExit exit) {
			return exit.value;
		} catch (ScopeExit exit) {
			throw new ScriptError(exit);
		}

		return Special.UNDEFINED;
	}

	@Override
	public int compareTo(AstFunction o) {
		return Integer.compare(o.params.length, params.length);
	}
}