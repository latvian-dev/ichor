package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.IchorError;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScopeImpl implements Scope {
	private Scope parent;
	private Map<String, Slot> members;

	public ScopeImpl() {
	}

	@Override
	@Nullable
	public Scope getParentScope() {
		return parent;
	}

	public void setParentScope(@Nullable Scope s) {
		parent = s;
	}

	@Override
	public Object getDeclaredMember(String name) {
		var slot = members == null ? null : members.get(name);
		return slot == null ? Special.NOT_FOUND : slot.value;
	}

	@Override
	public void declareMember(String name, Object value, AssignType type) {
		var slot = members == null ? null : members.get(name);

		if (type == AssignType.NONE) {
			if (slot == null) {
				throw new IchorError("Member " + name + " not found");
			} else if (slot.immutable) {
				throw new IchorError("Can't reassign constant " + name);
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

	@Override
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

	@Override
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

	@Override
	public Set<String> getDeclaredMemberNames() {
		return members == null ? Set.of() : members.keySet();
	}

	@Override
	public void deleteAllDeclaredMembers() {
		members = null;
	}
}
