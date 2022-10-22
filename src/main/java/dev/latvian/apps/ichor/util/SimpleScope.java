package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.error.IchorError;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleScope implements Scope {
	private Scope parent;
	private Map<String, Object> members;

	public SimpleScope() {
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
		var v = members == null ? Special.NOT_FOUND : members.getOrDefault(name, Special.NOT_FOUND);

		if (v instanceof ImmutableMember) {
			return ((ImmutableMember) v).value();
		}

		return v;
	}

	@Override
	public void declareMember(String name, Object value, AssignType type) {
		if (type == AssignType.NONE) {
			var v = members == null ? Special.NOT_FOUND : members.getOrDefault(name, Special.NOT_FOUND);

			if (v == Special.NOT_FOUND) {
				throw new IchorError("Member " + name + " not found");
			} else if (v instanceof ImmutableMember) {
				throw new IchorError("Can't reassign constant " + name);
			} else {
				members.put(name, value);
			}
		} else {
			if (members == null) {
				members = new HashMap<>(1);
			}

			members.put(name, type == AssignType.IMMUTABLE ? new ImmutableMember(value) : value);
		}
	}

	@Override
	public AssignType hasDeclaredMember(String name) {
		var v = members == null ? Special.NOT_FOUND : members.getOrDefault(name, Special.NOT_FOUND);

		if (v instanceof ImmutableMember) {
			return AssignType.IMMUTABLE;
		}

		return v == Special.NOT_FOUND ? AssignType.NONE : AssignType.MUTABLE;
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
