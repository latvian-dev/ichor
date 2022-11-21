package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.token.TokenPosSupplier;

public class ParseError extends IchorError {
	public ParseError(TokenPosSupplier pos, ParseErrorMessage message) {
		super(message.getMessage());
		tokenPos = pos.getPos();
	}
}