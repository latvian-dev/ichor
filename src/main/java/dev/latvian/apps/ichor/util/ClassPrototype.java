package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.prototype.Prototype;

public record ClassPrototype(AstClass astClass) implements Prototype, Callable {
	@Override
	public String getPrototypeName() {
		return astClass.name;
	}

	@Override
	public String toString() {
		return astClass.name;
	}

	public void interpretConstructorSuper(Scope scope, Object[] arguments) {
	}

	/*
	public Instance createClass(Scope scope) {
		var classScope = scope.push();
		Instance inst = new Instance(this, classScope);
		classScope.owner = inst;

		for (var e : methods.entrySet()) {
			classScope.declareMember(e.getKey(), e.getValue(), AssignType.IMMUTABLE);
		}

		var p0 = parent;

		while (p0 != null) {
			var p1 = p0.eval(scope);

			if (!(p1 instanceof PrototypeSupplier)) {
				throw new ScriptError("Cannot extend " + p0);
			}

			if (p1 instanceof AstClass c) {
				for (var e : c.methods.entrySet()) {
					if (classScope.hasMember(e.getKey()) == AssignType.NONE) {
						classScope.declareMember(e.getKey(), e.getValue(), AssignType.IMMUTABLE);
					}
				}

				p0 = c.parent;
			} else {
				throw new ScriptError("Cannot extend " + p0 + " - currently not supported");
			}
		}

		return inst;
	}

	@Override
	public Object call(Scope scope, Object self, Evaluable[] args) {
		return construct(scope, args);
	}
	 */

	// public record JavaInstance
}
