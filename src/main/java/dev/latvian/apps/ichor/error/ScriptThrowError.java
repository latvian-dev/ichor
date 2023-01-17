package dev.latvian.apps.ichor.error;

public class ScriptThrowError extends ScriptError {
	public ScriptThrowError(String message) {
		super(message);
	}

	public ScriptThrowError(Throwable cause) {
		super(cause);
	}
}
