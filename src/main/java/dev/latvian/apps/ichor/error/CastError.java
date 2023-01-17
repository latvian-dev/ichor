package dev.latvian.apps.ichor.error;

public class CastError extends ScriptError {
	public final String fromType, toType;

	public CastError(String fromType, String toType) {
		super("Cannot cast " + fromType + " to " + toType);
		this.fromType = fromType;
		this.toType = toType;
	}
}
