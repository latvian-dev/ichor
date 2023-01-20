package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.error.MemberNotFoundError;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

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
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		return new Instance(this, scope);
	}

	public void interpretConstructorSuper(Scope scope, Object[] args) {
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

	public static final class Instance implements Prototype {
		public final ClassPrototype prototype;
		public final Scope evalScope;
		public final Scope members;

		public Instance(ClassPrototype prototype, Scope evalScope) {
			this.prototype = prototype;
			this.evalScope = evalScope.push(this);
			this.members = prototype.classEvalScope.push(this);
		}

		@Override
		public String getPrototypeName() {
			return prototype.getPrototypeName();
		}

		@Override
		@Nullable
		public Object get(Context cx, Scope scope, Object self, String name) {
			var r = scope.getMember(name);

			if (r == Special.NOT_FOUND) {
				throw new MemberNotFoundError(name);
			}

			return r;
		}

		@Override
		public boolean set(Context cx, Scope scope, Object self, String name, @Nullable Object value) {
			scope.setMember(name, value);
			return true;
		}
	}
}
