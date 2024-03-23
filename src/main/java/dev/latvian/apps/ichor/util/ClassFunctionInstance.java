package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.expression.AstClassFunction;

public class ClassFunctionInstance extends FunctionInstance {

	public ClassFunctionInstance(AstClassFunction function, Scope evalScope) {
		super(function, evalScope);
	}

	/*
	@Override
	public String getPrototypeName() {
		if (functionName == null) {
			functionName = "<class " + type.name().toLowerCase() + " function>";
		}

		return functionName;
	}
	 */

	@Override
	public Object call(Scope callScope, Object[] args, boolean hasNew) {
		if (function == ((AstClassFunction) function).owner.constructor) {
			return super.call(callScope, args, false);
		} else {
			return super.call(callScope, args, hasNew);
		}
	}
}
