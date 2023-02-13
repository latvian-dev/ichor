package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.Scope;

public class RedeclarationError extends ScriptError {
	public final String name;
	public final Scope scope;

	public RedeclarationError(String name, Scope scope) {
		super("Member '" + name + "' already declared");
		this.name = name;
		this.scope = scope;
	}
}
