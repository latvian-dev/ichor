package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.prototype.Prototype;

import java.util.Collection;

public record ThisInstance(Scope evalScope) implements Prototype {
	@Override
	public String getPrototypeName() {
		return "this";
	}

	public Scope getActualScope(Scope scope) {
		var s = scope;

		do {
			if (s.owner instanceof ClassPrototype) {
				return s;
			}

			s = s.parent;
		}
		while (s != null);

		// arrow functions need to use parent scope
		return scope;
	}

	@Override
	public Object get(Context cx, Scope scope, Object self, String name) {
		var s = getActualScope(scope);

		var slot = s.getDeclaredMember(name);
		return slot == null ? Special.UNDEFINED : slot.value;
	}

	@Override
	public boolean set(Context cx, Scope scope, Object self, String name, Object value) {
		var s = getActualScope(scope);

		if (s.hasDeclaredMember(name) == AssignType.NONE) {
			s.addMutable(name, value);
		}

		return true;
	}

	@Override
	public boolean delete(Context cx, Scope scope, Object self, String name) {
		var s = getActualScope(scope);

		s.deleteDeclaredMember(name);
		return true;
	}

	@Override
	public Collection<?> keys(Context cx, Scope scope, Object self) {
		var s = getActualScope(scope);
		return s.members.getSlotNames();
	}

	@Override
	public Collection<?> values(Context cx, Scope scope, Object self) {
		var s = getActualScope(scope);
		return s.members.getSlotNames();
	}

	@Override
	public Collection<?> entries(Context cx, Scope scope, Object self) {
		var s = getActualScope(scope);
		return s.members.getSlotNames();
	}
}
