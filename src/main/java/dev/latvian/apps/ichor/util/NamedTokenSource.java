package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.token.TokenSource;

public record NamedTokenSource(String name) implements TokenSource {
	@Override
	public String getSourceName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
