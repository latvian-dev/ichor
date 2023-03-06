package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.prototype.Prototype;

public class NamedMemberNotFoundError extends ScriptError {
	public final String name;
	public final Prototype<?> prototype;
	public final Object self;

	public NamedMemberNotFoundError(String name, Prototype<?> prototype, Object self) {
		super("Member '" + name + "' of " + self + " of type '" + prototype + "' not found");
		this.name = name;
		this.prototype = prototype;
		this.self = self;
	}
}
