package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.Scope;

public class ScopeMemberNotFoundError extends ScriptError {
	public final String name;
	public final Scope scope;

	public ScopeMemberNotFoundError(String name, Scope scope) {
		super("Member '" + name + "' not found");
		this.name = name;
		this.scope = scope;
	}
}
