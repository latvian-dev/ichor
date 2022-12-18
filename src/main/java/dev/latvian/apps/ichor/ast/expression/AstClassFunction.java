package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Interpretable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.util.ClassFunctionInstance;

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
	public Object eval(Scope scope) {
		return new ClassFunctionInstance(this, scope);
	}
}
