package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.TokenSource;

public record NamedTokenSource(String name) implements TokenSource {
	@Override
	public String toString() {
		return name;
	}
}
