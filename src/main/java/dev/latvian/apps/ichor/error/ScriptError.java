package dev.latvian.apps.ichor.error;

public abstract class ScriptError extends IchorError {
	public ScriptError(String message) {
		super(message);
	}

	public ScriptError(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptError(Throwable cause) {
		super(cause);
	}
}
