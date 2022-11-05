package dev.latvian.apps.ichor;

public interface Debugger {
	default void pushScope(Scope scope) {
	}

	default void pushSelf(Scope scope, Object self) {
	}

	default void get(Scope scope, Object object, Object returnValue) {
	}

	default void set(Scope scope, Object object, Object value) {
	}

	default void call(Scope scope, Object callee, Object[] args, Object returnValue) {
	}

	default void assignNew(Scope scope, Object object, Object value) {
	}

	default void assignSet(Scope scope, Object object, Object value) {
	}

	default void exit(Scope scope, Object value) {
	}
}
