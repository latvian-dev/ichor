package dev.latvian.apps.ichor.error;

public class IchorError extends RuntimeException {
	public IchorError(String message) {
		super(message);
	}

	public IchorError(String message, Throwable cause) {
		super(message, cause);
	}

	public IchorError(Throwable cause) {
		super(cause);
	}
}
