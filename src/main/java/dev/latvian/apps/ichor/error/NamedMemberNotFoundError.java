package dev.latvian.apps.ichor.error;

public class NamedMemberNotFoundError extends ScriptError {
	public final String name;

	public NamedMemberNotFoundError(String name) {
		super("Member " + name + " not found");
		this.name = name;
	}
}
