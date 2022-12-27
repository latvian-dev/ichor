package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.expression.AstCall;

public interface Debugger {
	Debugger DEFAULT = new Debugger() {
	};

	default void pushScope(Context cx, Scope scope) {
	}

	default void pushSelf(Context cx, Scope scope, Object self) {
	}

	default void get(Context cx, Scope scope, Object object, Object returnValue) {
	}

	default void set(Context cx, Scope scope, Object object, Object value) {
	}

	default void delete(Context cx, Scope scope, Object object) {
	}

	default void call(Context cx, Scope scope, AstCall call, Object func, Object[] args, Object returnValue) {
	}

	default void assignNew(Context cx, Scope scope, Object object, Object value) {
	}

	default void assignSet(Context cx, Scope scope, Object object, Object value) {
	}

	default void exit(Context cx, Scope scope, Object value) {
	}

	default void debuggerStatement(Context cx, Scope scope) {
	}
}
