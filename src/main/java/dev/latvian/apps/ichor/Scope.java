package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.Slot;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Scope {
	public Scope parent;
	public RootScope root;
	public Map<String, Slot> members;

	// Member Methods //

	public Object getDeclaredMember(String name) {
		var slot = members == null ? null : members.get(name);
		return slot == null ? Special.NOT_FOUND : slot.value;
	}

	public void declareMember(String name, Object value, AssignType type) {
		var slot = members == null ? null : members.get(name);

		if (type == AssignType.NONE) {
			if (slot == null) {
				throw new ScriptError("Member " + name + " not found");
			} else if (slot.immutable) {
				throw new ScriptError("Can't reassign constant " + name);
			} else {
				slot.value = value;
				slot.prototype = null;
			}
		} else {
			if (slot == null) {
				slot = new Slot();

				if (members == null) {
					members = new HashMap<>(1);
				}

				members.put(name, slot);
			}

			slot.value = value;
			slot.immutable = type == AssignType.IMMUTABLE;
			slot.prototype = null;
		}
	}

	public AssignType hasDeclaredMember(String name) {
		var slot = members == null ? null : members.get(name);

		if (slot == null) {
			return AssignType.NONE;
		} else if (slot.immutable) {
			return AssignType.IMMUTABLE;
		} else {
			return AssignType.MUTABLE;
		}
	}

	public Object deleteDeclaredMember(String name) {
		if (members != null && members.containsKey(name)) {
			var v = members.remove(name);

			if (members.isEmpty()) {
				members = null;
			}

			return v;
		}

		return Special.NOT_FOUND;
	}

	public Set<String> getDeclaredMemberNames() {
		return members == null ? Set.of() : members.keySet();
	}

	public void deleteAllDeclaredMembers() {
		for (var id : Set.copyOf(getDeclaredMemberNames())) {
			deleteDeclaredMember(id);
		}
	}

	// Recursive Member Methods //

	public Object getMember(String name) {
		Scope s = this;

		do {
			var m = s.getDeclaredMember(name);

			if (m != Special.NOT_FOUND) {
				return m;
			}

			s = parent;
		}
		while (s != null);

		return Special.UNDEFINED;
	}

	public void setMember(String name, Object value, AssignType type) {
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

			s = parent;
		}
		while (s != null);

		throw new ScriptError("Member " + name + " not found");
	}

	public AssignType hasMember(String name) {
		Scope s = this;

		do {
			var t = s.hasDeclaredMember(name);

			if (t != AssignType.NONE) {
				return t;
			}

			s = parent;
		}
		while (s != null);

		return AssignType.NONE;
	}

	public void add(Prototype prototype) {
		declareMember(prototype.getPrototypeName(), prototype, AssignType.IMMUTABLE);
	}

	public void add(Class<?> type) {
		add(root.context.getClassPrototype(type));
	}

	public Scope createChildScope() {
		var scope = new Scope();
		scope.parent = this;
		scope.root = root;
		return scope;
	}

	public Context getContext() {
		return root.context;
	}
}
