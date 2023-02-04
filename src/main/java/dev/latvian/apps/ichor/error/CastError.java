package dev.latvian.apps.ichor.error;

public class CastError extends ScriptError {
	public final Object object;
	public final String type;

	public CastError(Object object, String type) {
		super("Cannot cast " + object + " (" + object.getClass().getName() + ")" + " to " + type);
		this.object = object;
		this.type = type;
	}
}
