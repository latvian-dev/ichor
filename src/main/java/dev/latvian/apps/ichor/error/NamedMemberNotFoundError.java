package dev.latvian.apps.ichor.error;

public class NamedMemberNotFoundError extends ScriptError {
	public final String name;
	public final Object self;

	public NamedMemberNotFoundError(String name, Object self) {
		super("Member '" + name + "' of " + self + " not found");
		this.name = name;
		this.self = self;
	}
}
