package dev.latvian.apps.ichor.util;

@FunctionalInterface
public interface CharacterScanner {
	CharacterScanner NO_INPUT = () -> {
		throw new IllegalStateException("No input");
	};

	CharacterScanner STDIN = () -> {
		try {
			return (char) System.in.read();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	};

	char nextChar();
}
