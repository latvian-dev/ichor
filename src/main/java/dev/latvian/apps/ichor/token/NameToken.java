package dev.latvian.apps.ichor.token;

public record NameToken(String name) implements Token {
	@Override
	public String toString() {
		return name;
	}
}
