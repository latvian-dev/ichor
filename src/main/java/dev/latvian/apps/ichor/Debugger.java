package dev.latvian.apps.ichor;

public interface Debugger {
	default void pushScope(Scope scope) {
	}

	default void popScope(Scope scope) {
	}

	default void pushSelf(Object self) {
	}

	default void get(Object object, Object returnValue) {
	}

	default void set(Object object, Object value) {
	}

	default void call(Object callee, Object[] args, Object returnValue) {
	}

	default void assignNew(Object object, Object value) {
	}

	default void assignSet(Object object, Object value) {
	}
}
