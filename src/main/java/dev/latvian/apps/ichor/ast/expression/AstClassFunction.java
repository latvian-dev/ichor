package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Evaluable;
import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.exit.ReturnExit;
import dev.latvian.apps.ichor.exit.ScopeExit;
import dev.latvian.apps.ichor.util.AssignType;

public class AstClassFunction extends AstFunction {
	public enum Type {
		CONSTRUCTOR, GETTER, SETTER, METHOD
	}

	public final AstClass owner;
	public final Type type;

	public AstClassFunction(AstClass owner, AstParam[] params, Interpretable body, int modifiers, Type type) {
		super(params, body, modifiers | AstFunction.MOD_CLASS);
		this.owner = owner;
		this.type = type;
	}

	@Override
	public String getPrototypeName() {
		if (functionName == null) {
			functionName = "<class " + type.name().toLowerCase() + " function>";
		}

		return functionName;
	}

	@Override
	public Object call(Scope scope, Object self, Evaluable[] args) {
		if (args.length < requiredParams) {
			throw new ScriptError("Invalid number of arguments: " + args.length + " < " + requiredParams);
		}

		var s = scope.push(this);

		try {
			for (int i = 0; i < params.length; i++) {
				s.declareMember(params[i].name, i >= args.length ? params[i].defaultValue == Special.UNDEFINED ? Special.UNDEFINED : params[i].defaultValue.eval(scope) : args[i].eval(scope), AssignType.MUTABLE);
			}

			body.interpret(s);
		} catch (ReturnExit exit) {
			return exit.value;
		} catch (ScopeExit exit) {
			throw new ScriptError(exit);
		}

		return Special.UNDEFINED;
	}
}
