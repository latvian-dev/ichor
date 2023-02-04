package dev.latvian.apps.ichor.token;

public class DeclaringToken extends KeywordToken {
	public final byte flags;

	public DeclaringToken(String name, byte flags) {
		super(name);
		this.flags = flags;
	}
}
