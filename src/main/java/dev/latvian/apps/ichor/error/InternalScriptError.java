package dev.latvian.apps.ichor.error;

public class InternalScriptError extends ScriptError {
	public InternalScriptError(Throwable cause) {
		super("Internal error", cause);
	}
}
