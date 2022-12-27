package dev.latvian.apps.ichor.token;

import java.util.Objects;

public class IdentifierToken implements Token {
	public final String name;

	public IdentifierToken(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		return name.equals(String.valueOf(obj));
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public boolean isIdentifier() {
		return true;
	}
}
