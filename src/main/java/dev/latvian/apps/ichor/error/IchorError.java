package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.token.TokenPos;
import dev.latvian.apps.ichor.token.TokenPosSupplier;

public abstract class IchorError extends RuntimeException {
	public TokenPos tokenPos = TokenPos.UNKNOWN;

	public IchorError(String message) {
		super(message);
	}

	public IchorError(String message, Throwable cause) {
		super(message, cause);
	}

	public IchorError(Throwable cause) {
		super(cause);
	}

	public IchorError pos(TokenPosSupplier pos) {
		tokenPos = pos.getPos();
		return this;
	}

	@Override
	public String getMessage() {
		if (tokenPos != TokenPos.UNKNOWN) {
			return tokenPos + ": " + super.getMessage();
		}

		return super.getMessage();
	}
}
