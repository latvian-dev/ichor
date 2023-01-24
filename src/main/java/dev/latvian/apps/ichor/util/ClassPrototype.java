package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.error.MemberNotFoundError;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public record ClassPrototype(AstClass astClass, Scope classEvalScope) implements Prototype, Callable {
	@Override
	public String getPrototypeName() {
		return astClass.name;
	}

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

	public static final class Instance implements Prototype {
		public final ClassPrototype prototype;
		public final Scope members;

		public Instance(Context cx, ClassPrototype prototype) {
			this.prototype = prototype;
			this.members = prototype.classEvalScope.push(this);
			this.members.scopeThis = this;

			for (var func : prototype.astClass.methods.values()) {
				members.add(func.functionName, new FunctionInstance(func, cx, members), false);
			}
		}

		public void interpretConstructorSuper(Object[] args) {
			System.out.println(Arrays.toString(args));
		}

		@Override
		public String getPrototypeName() {
			return prototype.getPrototypeName();
		}

		@Override
		@Nullable
		public Object get(Context cx, Scope callScope, Object self, String name) {
			var r = members.getMember(name);

			if (r == Special.NOT_FOUND) {
				throw new MemberNotFoundError(name);
			}

			return r;
		}

		@Override
		public boolean set(Context cx, Scope callScope, Object self, String name, @Nullable Object value) {
			members.setMember(name, value);
			return true;
		}
	}
}
