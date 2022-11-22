package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.expression.AstParam;
import dev.latvian.apps.ichor.ast.statement.AstClass;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.AssignType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Scope {
	public static class Slot {
		public Object value;
		public boolean immutable;

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	public final Scope parent;
	public RootScope root;
	public Map<String, Slot> members;
	public Object owner;
	private int depth;

	protected Scope(Scope parent) {
		this.parent = parent;

		if (this.parent != null) {
			this.root = parent.root;
			this.owner = parent.owner;
			this.depth = parent.depth + 1;

			if (this.depth > this.root.maxScopeDepth) {
				throw new ScriptError("Scope depth is > " + this.root.maxScopeDepth);
			}
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
				// slot.prototype = null;

				if (root.context.debugger != null) {
					root.context.debugger.assignSet(this, name, value);
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
			// slot.prototype = null;

			if (root.context.debugger != null) {
				root.context.debugger.assignNew(this, name, value);
			}
		}
	}

	public void declareParam(AstParam param, AssignType type) {
		declareMember(param.name, param.defaultValue.eval(this), type);
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

	public boolean setMember(String name, Object value, AssignType type) {
		// type == NONE = replace existing member, error if not found (x = y)
		// type == MUTABLE = create new mutable member (var x, let x)
		// type == IMMUTABLE = create new immutable member (const x)

		if (type == AssignType.MUTABLE || type == AssignType.IMMUTABLE) {
			declareMember(name, value, type);
			return true;
		}

		Scope s = this;

		do {
			if (s.hasDeclaredMember(name) != AssignType.NONE) {
				s.declareMember(name, value, AssignType.NONE);
				return true;
			}

			s = s.parent;
		}
		while (s != null);

		return false;
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

	public void add(String name, Prototype prototype) {
		declareMember(name, prototype, AssignType.IMMUTABLE);
	}

	public void add(String name, Class<?> type) {
		add(name, root.context.getClassPrototype(type));
	}

	public Scope push() {
		return push(owner);
	}

	public Scope push(Object owner) {
		var p = new Scope(this);
		p.owner = owner;

		if (root.context.debugger != null) {
			root.context.debugger.pushScope(this);
		}

		return p;
	}

	public Context getContext() {
		return root.context;
	}

	@Override
	public String toString() {
		if (owner instanceof AstClass.Instance c) {
			return "Scope[" + getDepth() + ']' + getDeclaredMemberNames() + ":" + c.getPrototypeName();
		}

		return "Scope[" + getDepth() + ']' + getDeclaredMemberNames();
	}

	public int getDepth() {
		return depth;
	}

	@Nullable
	public AstClass.Instance findOwnerClass() {
		var s = this;

		do {
			if (s.owner instanceof AstClass.Instance instance) {
				return instance;
			}

			s = s.parent;
		}
		while (s != null);

		return null;
	}
}
