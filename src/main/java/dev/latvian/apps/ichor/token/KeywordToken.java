package dev.latvian.apps.ichor.token;

public class KeywordToken extends NameToken {
	public final boolean canBeName;

	public KeywordToken(String name, boolean canBeName) {
		super(name);
		this.canBeName = canBeName;
	}

	@Override
	public boolean canBeName() {
		return canBeName;
	}
}
