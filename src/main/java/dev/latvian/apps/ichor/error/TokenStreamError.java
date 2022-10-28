package dev.latvian.apps.ichor.error;

public class TokenStreamError extends IchorError {
	public TokenStreamError(int line, int pos, String message) {
		super(line + ":" + pos + ": " + message);
	}
}
