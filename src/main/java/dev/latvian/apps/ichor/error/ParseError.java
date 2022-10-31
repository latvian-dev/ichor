package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.token.PositionedToken;

public class ParseError extends IchorError {
	public ParseError(PositionedToken token, String message) {
		super(token.pos() + ": " + message);
	}
}