package dev.latvian.apps.ichor.token;

import org.jetbrains.annotations.Nullable;

public class KeywordToken extends IdentifierToken {
	private boolean isIdentifier;
	private boolean isLiteralPre;
	private Token insertToken;

	public KeywordToken(String name) {
		super(name);
		isIdentifier = false;
		isLiteralPre = false;
	}

	@Override
	public boolean isIdentifier() {
		return isIdentifier;
	}

	public KeywordToken identifier() {
		isIdentifier = true;
		return this;
	}

	@Override
	public boolean isLiteralPre() {
		return isLiteralPre;
	}

	public KeywordToken insertToken(Token token) {
		insertToken = token;
		return this;
	}

	@Override
	@Nullable
	public Token getTokenBeforeNewline() {
		return insertToken;
	}

	public KeywordToken literalPre() {
		isLiteralPre = true;
		return this;
	}
}
