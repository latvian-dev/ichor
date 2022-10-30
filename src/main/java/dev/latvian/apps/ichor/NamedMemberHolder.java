package dev.latvian.apps.ichor;

import java.util.Collection;
import java.util.Set;

public interface NamedMemberHolder {
	default Object getMember(Scope scope, String name) {
		return Special.NOT_FOUND;
	}

	default boolean hasMember(Scope scope, String name) {
		return false;
	}

	default void setMember(Scope scope, String name, Object value) {
	}

	default void deleteMember(Scope scope, String name) {
	}

	default Collection<String> getMemberNames() {
		return Set.of();
	}
}
