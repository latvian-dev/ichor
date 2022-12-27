package dev.latvian.apps.ichor.token;

public class KeywordToken extends IdentifierToken {
	private boolean isIdentifier;
	private boolean isLiteralPre;

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

	public KeywordToken literalPre() {
		isLiteralPre = true;
		return this;
	}
}
