package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.token.TokenPos;

public class TokenStreamError extends IchorError {
	public TokenStreamError(TokenPos pos, String message) {
		super(message);
		tokenPos = pos;
	}
}
