package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.IchorError;

public interface Ref {
	default Object getValue(Context cx, Scope scope, Object self) {
		throw new IchorError(this + " does not return a value!");
	}

	default void setValue(Context cx, Scope scope, Object self, Object value) {
		throw new IchorError(this + " cannot be set!");
	}

	default Object construct(Context cx, Scope scope, Object self, Object[] args, boolean hasNew) {
		throw new IchorError(this + " cannot be constructed!");
	}

	default Object invoke(Context cx, Scope scope, Object self, Object[] args) {
		throw new IchorError(this + " cannot be invoked!");
	}

	default Object getMember(Context cx, Scope scope, Object self, String name) {
		throw new IchorError(this + " does not have a member " + name + "!");
	}

	default void setMember(Context cx, Scope scope, Object self, String name, Object value) {
		throw new IchorError(this + " does not have a member " + name + "!");
	}
}
