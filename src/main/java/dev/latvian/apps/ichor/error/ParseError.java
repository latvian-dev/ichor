package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.token.TokenPosSupplier;

public class ParseError extends IchorError {
	public ParseError(TokenPosSupplier pos, ParseErrorType type, Object... args) {
		super(args.length == 0 ? type.message : type.message.formatted(args));
		tokenPos = pos.getPos();
	}
}