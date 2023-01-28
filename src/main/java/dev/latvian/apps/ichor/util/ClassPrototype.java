package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.ast.statement.AstClass;

import java.util.Arrays;

public record ClassPrototype(AstClass astClass, Scope classEvalScope) implements Callable {
	@Override
	public String toString() {
		return astClass.name;
	}

	@Override
	public Object call(Context cx, Scope callScope, Object[] args, boolean hasNew) {
		var instance = new Instance(cx, this);

		if (astClass.constructor != null) {
			var ctor = astClass.constructor.eval(cx, callScope);
			ctor.call(cx, callScope, args, true);
		}

		return instance;
	}

	public static final class Instance extends Scope {
		public final ClassPrototype prototype;

		public Instance(Context cx, ClassPrototype prototype) {
			super(prototype.classEvalScope.push());
			setScopeThis(this);
			this.prototype = prototype;

			for (var func : prototype.astClass.methods.values()) {
				add(func.functionName, new FunctionInstance(func, cx, this), false);
			}
		}

		public void interpretConstructorSuper(Object[] args) {
			System.out.println(Arrays.toString(args));
		}
	}
}
