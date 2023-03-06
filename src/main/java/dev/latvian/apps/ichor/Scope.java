package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.error.ConstantReassignError;
import dev.latvian.apps.ichor.error.RedeclarationError;
import dev.latvian.apps.ichor.error.ScopeDepthError;
import dev.latvian.apps.ichor.error.ScopeMemberNotFoundError;
import dev.latvian.apps.ichor.slot.EmptySlotMap;
import dev.latvian.apps.ichor.slot.Slot;
import dev.latvian.apps.ichor.slot.SlotMap;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.ClassPrototype;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class Scope {
	public final Scope parent;
	public RootScope root;
	public SlotMap members;
	private int depth;
	public Object scopeOwner;
	public Scope scopeThis;
	public Scope scopeSuper;
	public Object[] scopeArguments;

	protected Scope(Scope parent) {
		this.parent = parent;
		this.members = EmptySlotMap.INSTANCE;

		if (this.parent != null) {
			this.root = parent.root;
			this.depth = parent.depth + 1;
			this.scopeOwner = parent.scopeOwner;
			this.scopeThis = parent.scopeThis;
			this.scopeSuper = parent.scopeSuper;
			this.scopeArguments = parent.scopeArguments;

			if (this.depth > this.root.maxScopeDepth) {
				throw new ScopeDepthError(this.root.maxScopeDepth);
			}
		}
	}

	// Member Methods //

	@Nullable
	public Slot getDeclaredMember(String name) {
		return members.getSlot(name);
	}

	public void add(String name, @Nullable Object value, byte flags) {
		var slot = members.getSlot(name);

		if (slot == null) {
			if (this != root) {
				var rootSlot = root.members.getSlot(name);

				if (rootSlot != null && rootSlot.isRoot()) {
					throw new RedeclarationError(name, this);
				}
			}

			slot = new Slot(name);
			members = members.upgradeSlotMap();
			members.setSlot(slot);
			slot.value = value;
			slot.flags = flags;
		} else {
			throw new RedeclarationError(name, this);
		}
	}

	public void addMutable(String name, @Nullable Object value) {
		add(name, value, Slot.DEFAULT);
	}

	public void addImmutable(String name, @Nullable Object value) {
		add(name, value, Slot.IMMUTABLE);
	}

	public void add(String name, Class<?> type) {
		addImmutable(name, root.context.getClassPrototype(type));
	}

	public void setScopeThis(Scope o) {
		scopeSuper = scopeThis;
		scopeThis = o;
	}

	public boolean setMember(String name, @Nullable Object value) {
		Scope s = this;

		do {
			var slot = s.members.getSlot(name);

			if (slot != null) {
				if (slot.value != Special.UNDEFINED && slot.isImmutable()) {
					throw new ConstantReassignError(name);
				} else {
					slot.value = value;
					// slot.prototype = null;
					return true;
				}
			}

			s = s.parent;
		}
		while (s != null);

		throw new ScopeMemberNotFoundError(name, this);
	}

	public AssignType hasDeclaredMember(String name) {
		var slot = members.getSlot(name);

		if (slot == null) {
			return AssignType.NONE;
		} else if (slot.isImmutable()) {
			return AssignType.IMMUTABLE;
		} else {
			return AssignType.MUTABLE;
		}
	}

	public void deleteDeclaredMember(String name) {
		var slot = members.getSlot(name);

		if (slot == null) {
			throw new ScopeMemberNotFoundError(name, this);
		}

		members.removeSlot(name);
	}

	public Set<String> getDeclaredMemberNames() {
		return members.getSlotNames();
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
			var slot = s.getDeclaredMember(name);

			if (slot != null) {
				return slot.value;
			}

			s = s.parent;
		}
		while (s != null);

		throw new ScopeMemberNotFoundError(name, this);
	}

	public AssignType hasMember(String name) {
		Scope s = this;

		do {
			var t = s.hasDeclaredMember(name);

			if (t.isSet()) {
				return t;
			}

			s = s.parent;
		}
		while (s != null);

		return AssignType.NONE;
	}

	public Scope push() {
		return push(scopeOwner);
	}

	public Scope push(Object owner) {
		var p = new Scope(this);
		p.scopeOwner = owner;
		root.checkTimeout();
		return p;
	}

	@Override
	public String toString() {
		if (scopeOwner instanceof ClassPrototype c) {
			return "Scope[" + getDepth() + ']' + getDeclaredMemberNames() + ":" + c.astClass.name;
		}

		return "Scope[" + getDepth() + ']' + getDeclaredMemberNames();
	}

	public int getDepth() {
		return depth;
	}
}
