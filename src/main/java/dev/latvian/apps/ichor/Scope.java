package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Evaluable;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.AssignType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Scope {
	public static class Slot {
		public Object value;
		public boolean immutable;
		public Prototype prototype;

		public Prototype getPrototype(Context cx) {
			if (prototype == null) {
				prototype = cx.getPrototype(value);
			}

			return prototype;
		}
	}

	public final Scope parent;
	public RootScope root;
	public Map<String, Slot> members;

	protected Scope(Scope parent) {
		this.parent = parent;

		if (this.parent != null) {
			this.root = parent.root;
		}
	}

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

				if (root.context.debugger != null) {
					root.context.debugger.assignSet(name, value);
				}
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

			if (root.context.debugger != null) {
				root.context.debugger.assignNew(name, value);
			}
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

			s = s.parent;
		}
		while (s != null);

		return Special.NOT_FOUND;
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

			s = s.parent;
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

			s = s.parent;
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

	public Scope childScope() {
		return new Scope(this);
	}

	public Context getContext() {
		return root.context;
	}

	public Scope push() {
		root.current = childScope();

		if (root.context.debugger != null) {
			root.context.debugger.pushScope(root.current);
		}

		return root.current;
	}

	public void pop() {
		root.current = this;

		if (root.context.debugger != null) {
			root.context.debugger.popScope(this);
		}
	}

	@Override
	public String toString() {
		int depth = 0;
		var s = this;

		while (s.parent != null) {
			depth++;
			s = s.parent;
		}

		return "Scope[" + depth + ']' + getDeclaredMemberNames();
	}

	public Object eval(Object object) {
		return object instanceof Evaluable eval ? eval.eval(this) : object;
	}
}
