package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;
import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

public class AstThis extends AstExpression {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("this");
	}

	@Override
	public Object eval(Scope scope) {
		return new ThisAccessor(scope);
	}

	public record ThisAccessor(Scope scope) implements Prototype {
		@Override
		public String getPrototypeName() {
			return "this";
		}

		@Override
		public Object get(Scope scope, String name, @Nullable Object self) {
			var slot = scope.members == null ? null : scope.members.get(name);

			if (slot == null) {
				throw new ScriptError("Member " + name + " not found");
			}

			return slot.value;
		}

		@Override
		public boolean has(Scope scope, String name, @Nullable Object self) {
			return scope.members != null && scope.members.containsKey(name);
		}

		@Override
		public boolean set(Scope scope, String name, @Nullable Object self, Object value) {
			var slot = scope.members == null ? null : scope.members.get(name);

			if (slot == null) {
				throw new ScriptError("Member " + name + " not found");
			} else if (slot.immutable) {
				throw new ScriptError("Member " + name + " is a constant");
			} else {
				slot.value = value;
				return true;
			}
		}

		@Override
		public boolean delete(Scope scope, String name, @Nullable Object self) {
			if (scope.members != null && scope.members.containsKey(name)) {
				scope.members.remove(name);
				return true;
			} else {
				throw new ScriptError("Member " + name + " not found");
			}
		}

		@Override
		public Iterator<?> keyIterator(Scope scope, @Nullable Object self) {
			return scope.members == null ? Collections.emptyIterator() : scope.members.keySet().iterator();
		}

		@Override
		public Iterator<?> valueIterator(Scope scope, @Nullable Object self) {
			return scope.members == null ? Collections.emptyIterator() : scope.members.values().iterator();
		}

		@Override
		public Iterator<?> entryIterator(Scope scope, @Nullable Object self) {
			return scope.members == null ? Collections.emptyIterator() : scope.members.entrySet().iterator();
		}
	}
}
