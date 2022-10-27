package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.RootScope;
import dev.latvian.apps.ichor.util.ScopeImpl;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface Scope {
	@Nullable
	default Scope getParentScope() {
		return null;
	}

	default RootScope getRootScope() {
		Scope s = this;

		while (s.getParentScope() != null) {
			s = s.getParentScope();
		}

		return (RootScope) s;
	}

	// Member Methods //

	default Object getDeclaredMember(String name) {
		return Special.NOT_FOUND;
	}

	default void declareMember(String name, Object value, AssignType type) {
	}

	default AssignType hasDeclaredMember(String name) {
		return AssignType.NONE;
	}

	default Object deleteDeclaredMember(String name) {
		return Special.NOT_FOUND;
	}

	default Set<String> getDeclaredMemberNames() {
		return Set.of();
	}

	default void deleteAllDeclaredMembers() {
		for (var id : Set.copyOf(getDeclaredMemberNames())) {
			deleteDeclaredMember(id);
		}
	}

	// Recursive Member Methods //

	default Object getMember(String name) {
		Scope s = this;

		do {
			var m = s.getDeclaredMember(name);

			if (m != Special.NOT_FOUND) {
				return m;
			}

			s = getParentScope();
		}
		while (s != null);

		return Special.UNDEFINED;
	}

	default void setMember(String name, Object value, AssignType type) {
		// type == NONE = replace existing member, error if not found (x = y)
		// type == MUTABLE = create new mutable member (var x, let x)
		// type == IMMUTABLE = create new immutable member (const x)

		if (type == AssignType.MUTABLE || type == AssignType.IMMUTABLE) {
			declareMember(name, value, type);
			return;
		}

		Scope s = this;

		do {
			if (s.hasDeclaredMember(name) != AssignType.NONE) {
				s.declareMember(name, value, AssignType.NONE);
				return;
			}

			s = getParentScope();
		}
		while (s != null);

		throw new IchorError("Member " + name + " not found");
	}

	default AssignType hasMember(String name) {
		Scope s = this;

		do {
			var t = s.hasDeclaredMember(name);

			if (t != AssignType.NONE) {
				return t;
			}

			s = getParentScope();
		}
		while (s != null);

		return AssignType.NONE;
	}

	default void add(Prototype prototype) {
		declareMember(prototype.getPrototypeName(), prototype, AssignType.IMMUTABLE);
	}

	default void add(Class<?> type) {
		add(getRootScope().context.getClassPrototype(type));
	}

	default Scope createChildScope() {
		var scope = new ScopeImpl();
		scope.setParentScope(this);
		return scope;
	}
}
