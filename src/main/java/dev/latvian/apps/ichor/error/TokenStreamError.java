package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.token.TokenPos;

public class TokenStreamError extends IchorError {
	public final String code;

	public TokenStreamError(TokenPos tokenPos, String message, String code) {
		super(message);
		this.tokenPos = tokenPos;
		this.code = code;
	}

	@Override
	public String getCode() {
		return code;
	}
}
