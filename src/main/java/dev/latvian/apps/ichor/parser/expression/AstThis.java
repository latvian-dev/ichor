package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.NamedMemberHolder;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.parser.AstStringBuilder;

import java.util.Collection;
import java.util.Set;

public class AstThis extends AstExpression {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("this");
	}

	@Override
	public Object eval(Scope scope) {
		return new ThisAccessor(scope);
	}

	public record ThisAccessor(Scope scope) implements NamedMemberHolder {
		@Override
		public Object getMember(Scope scope, String name) {
			var slot = scope.members == null ? null : scope.members.get(name);

			if (slot == null) {
				throw new ScriptError("Member " + name + " not found");
			}

			return slot.value;
		}

		@Override
		public boolean hasMember(Scope scope, String name) {
			return scope.members != null && scope.members.containsKey(name);
		}

		@Override
		public void setMember(Scope scope, String name, Object value) {
			var slot = scope.members == null ? null : scope.members.get(name);

			if (slot == null) {
				throw new ScriptError("Member " + name + " not found");
			} else if (slot.immutable) {
				throw new ScriptError("Member " + name + " is a constant");
			} else {
				slot.value = value;
			}
		}

		@Override
		public void deleteMember(Scope scope, String name) {
			if (scope.members != null && scope.members.containsKey(name)) {
				scope.members.remove(name);
			} else {
				throw new ScriptError("Member " + name + " not found");
			}
		}

		@Override
		public Collection<String> getMemberNames() {
			return scope.members == null ? Set.of() : scope.members.keySet();
		}
	}
}
