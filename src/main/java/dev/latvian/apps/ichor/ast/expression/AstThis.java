package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.Special;
import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.ScriptError;
import dev.latvian.apps.ichor.prototype.Prototype;

import java.util.Collection;
import java.util.Collections;

public class AstThis extends AstExpression implements Prototype {
	@Override
	public void append(AstStringBuilder builder) {
		builder.append("this");
	}

	@Override
	public String getPrototypeName() {
		return "this";
	}

	@Override
	public Object call(Scope scope, Object self, Object[] args) {
		return Special.NOT_FOUND;
	}

	@Override
	public Object get(Scope scope, Object self, String name) {
		var slot = scope.members == null ? null : scope.members.get(name);
		return slot == null ? Special.NOT_FOUND : slot.value;
	}

	@Override
	public boolean set(Scope scope, Object self, String name, Object value) {
		var slot = scope.members == null ? null : scope.members.get(name);

		if (slot == null) {
			throw new ScriptError("Member " + name + " not found");
		} else if (slot.immutable) {
			throw new ScriptError("Member " + name + " is a constant");
		} else {
			slot.value = value;
			return true;
		}
	}

	@Override
	public boolean delete(Scope scope, Object self, String name) {
		if (scope.members != null && scope.members.containsKey(name)) {
			scope.members.remove(name);
			return true;
		} else {
			throw new ScriptError("Member " + name + " not found");
		}
	}

	@Override
	public Collection<?> keys(Scope scope, Object self) {
		return scope.members == null ? Collections.emptySet() : scope.members.keySet();
	}

	@Override
	public Collection<?> values(Scope scope, Object self) {
		return scope.members == null ? Collections.emptySet() : scope.members.values();
	}

	@Override
	public Collection<?> entries(Scope scope, Object self) {
		return scope.members == null ? Collections.emptySet() : scope.members.entrySet();
	}
}
