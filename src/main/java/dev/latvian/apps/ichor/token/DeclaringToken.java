package dev.latvian.apps.ichor.token;

public class DeclaringToken extends KeywordToken {
	private final boolean isConst;

	public DeclaringToken(String name, boolean isConst) {
		super(name);
		this.isConst = isConst;
	}

	public boolean isConst() {
		return isConst;
	}
}
