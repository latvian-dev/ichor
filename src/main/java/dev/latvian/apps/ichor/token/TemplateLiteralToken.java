package dev.latvian.apps.ichor.token;

public record TemplateLiteralToken(String string) implements Token {
	@Override
	public String toString() {
		return '`' + string.replace("\\", "\\\\") + '`';
	}
}
