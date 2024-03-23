package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.slot.Slot;

import java.util.Arrays;

public final class ClassPrototype implements Callable {
	public final AstClass astClass;
	public final Scope classEvalScope;

	public ClassPrototype(AstClass astClass, Scope classEvalScope) {
		this.astClass = astClass;
		this.classEvalScope = classEvalScope;
	}

	@Override
	public Object call(Scope callScope, Object[] args, boolean hasNew) {
		var instance = new Instance(this);

		if (astClass.constructor != null) {
			var ctor = astClass.constructor.eval(callScope);
			ctor.call(callScope, args, true);
		}

		return instance;
	}

	public static final class Instance extends Scope {
		public final ClassPrototype prototype;

		public Instance(ClassPrototype prototype) {
			super(prototype.classEvalScope.push());
			setScopeThis(this);
			this.prototype = prototype;

			for (var func : prototype.astClass.methods.values()) {
				add(func.functionName, new ClassFunctionInstance(func, this), Slot.DEFAULT);
			}
		}

		public void interpretConstructorSuper(Object[] args) {
			System.out.println(Arrays.toString(args));
		}
	}
}
