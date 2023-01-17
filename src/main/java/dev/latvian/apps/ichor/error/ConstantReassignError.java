package dev.latvian.apps.ichor.error;

public class ConstantReassignError extends ScriptError {
	public final String name;

	public ConstantReassignError(String name) {
		super("Can't reassign constant " + name);
		this.name = name;
	}
}
