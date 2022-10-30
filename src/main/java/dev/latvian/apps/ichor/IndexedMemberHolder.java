package dev.latvian.apps.ichor;

public interface IndexedMemberHolder {
	default Object getMember(Scope scope, int index) {
		return Special.NOT_FOUND;
	}

	default boolean setMember(Scope scope, int index, Object value) {
		return false;
	}
}
